<?php
App::uses('AppModel', 'Model');
App::uses('Folder', 'Utility');
App::uses('File', 'Utility');

class Game extends AppModel
{
    public $name = 'Game';
    public $displayField = 'id';
    public $hasOne = array(
        'Playoff' => array(
            'className' => 'Playoff',
            'foreignKey' => 'game_id'
        )
    );
    public $hasMany = array(
        'Comment' => array(
            'className' => 'Comment',
            'foreignKey' => 'game_id'
        ),
        'Rating' => array(
            'className' => 'Rating',
            'foreignKey' => 'game_id'
        ),
        'Trace' => array(
            'className' => 'Trace',
            'foreignKey' => 'on'
        )
    );
    public $belongsTo = array(
        'Group' => array(
            'className' => 'Group',
            'foreignKey' => 'group_id'
        ),
        'Home' => array(
            'className' => 'User',
            'foreignKey' => 'home_id'
        ),
        'Away' => array(
            'className' => 'User',
            'foreignKey' => 'away_id'
        ),
        'Report' => array(
            'className' => 'User',
            'foreignKey' => 'reporter_id'
        ),
        'Tournament' => array(
            'className' => 'Tournament',
            'foreignKey' => 'tournament_id'
        )
    );
    public $virtualFields = array(
        'comments' => 'SELECT COUNT(*) FROM comments as Comment WHERE Comment.game_id = Game.id',
        'stage' => 'SELECT `stepAssoc` FROM playoffs as Playoff WHERE Playoff.game_id = Game.id',
        'likes' => 'SELECT `likes` FROM ratings as Rating WHERE Rating.game_id = Game.id'
    );


    /**
     * A new game is being added and it should always be added using this method.
     * @TODO Instead of calling this yourself this should work using one of the callbacks. Probably beforeSave().
     *
     * @param $data
     * @return bool Whether or not adding succeeded.
     */
    public function report($data)
    {
        $data['user'] = AuthComponent::user('id');

        // Custom validation.
        $this->set($data);
        if (!$this->validates()) {
            return false;
        }
        unset($this->validate); // Validation has already been done.

        $tournament = $this->currentTournament();

        // Determine winner.
        if ($data['userScore'] > $data['opponentScore']) {
            $winner = $data['user'];
            $loser = $data['opponent'];
            $score_w = $data['userScore'];
            $score_l = $data['opponentScore'];
        } else {
            $winner = $data['opponent'];
            $loser = $data['user'];
            $score_w = $data['opponentScore'];
            $score_l = $data['userScore'];
        }

        // Update the group's table or playoff tree.
        if ($tournament['Tournament']['status'] == Tournament::GROUP) {
            $this->Group->Standing->recursive = 0;
            $standingWinner = $this->Group->Standing->find(
                'first',
                array(
                    'conditions' => array(
                        'Standing.user_id' => $winner,
                        'Group.tournament_id' => $tournament['Tournament']['id']
                    )
                )
            );
            $standingLoser = $this->Group->Standing->find(
                'first',
                array(
                    'conditions' => array(
                        'Standing.user_id' => $loser,
                        'Group.tournament_id' => $tournament['Tournament']['id']
                    )
                )
            );
            $group_id = $standingLoser['Group']['id'];
            $this->Group->updateReport($standingWinner['Standing'], $standingLoser['Standing'], $score_w, $score_l);
            $playoff_id = 0;
            $this->id = false;

            // Save the game to the database.
            $this->save(array(
                'group_id' => $group_id,
                'playoff_id' => $playoff_id,
                'home_id' => $winner,
                'away_id' => $loser,
                'score_h' => $score_w,
                'score_a' => $score_l,
                'reporter_id' => AuthComponent::user('id'),
                'tournament_id' => $tournament['Tournament']['id']
            ));
        } elseif ($tournament['Tournament']['status'] == Tournament::PLAYOFF) {
            $upd = $this->Playoff->updateReport($winner, $loser);
            $this->id = $upd['reportedGame'];

            if ($this->HomeOrAway($this->id, $winner) == 'home') {
                $score_h = $score_w;
                $score_a = $score_l;
            } else {
                $score_h = $score_l;
                $score_a = $score_w;
            }

            // Save the game to the database.
            $this->save(array(
                    'playoff_id' => $upd['reportedPO'],
                    'score_h' => $score_h,
                    'score_a' => $score_a,
                    'reporter_id' => AuthComponent::user('id'),
                    'tournament_id' => $tournament['Tournament']['id'],
                    'created' => gmdate('Y-m-d H:i:s') // Necessary?
                ),
                false,
                array(
                    'playoff_id', 'score_h', 'score_a', 'reporter_id', 'tournament_id', 'created'
                ));
        }

        $id = $this->id;
        $User = ClassRegistry::init('User');
        $result = $score_w . '-' . $score_l;
        $winnerUN = $User->field('username', array('id' => $winner));
        $loserUN = $User->field('username', array('id' => $loser));
        $filename = "[$id] $winnerUN $result $loserUN";
        $file = new File("files/replays/$filename.rar", true);

        // Transfer temporary file contents to the new file.
        $tmp_file = new File($data['replays']['tmp_name']);
        $contents = $tmp_file->read();
        $file->write($contents);

        // Writes some nice Tournament News to the Infoboard.
        $IBwinner = "<a href=\"/users/view/$winner\">$winnerUN</a>";
        $IBloser = "<a href=\"/users/view/$loser\">$loserUN</a>";
        $IBresult = "<a href=\"/games/view/$id\">$result</a>";

        $Infoboard = ClassRegistry::init('Infoboard');
        $Infoboard->save(array(
            'message' => "$IBwinner defeated $IBloser by $IBresult",
            'category' => 3
        ));

        return true;
    }

    public function HomeOrAway($gameId, $userId = false)
    {
        $userId = $userId ? $userId : AuthComponent::user('id');

        $isHome = $this->find('count', array(
            'conditions' => array(
                'Game.id' => $gameId,
                'Game.home_id' => $userId
            )
        ));

        return ($isHome ? 'home' : 'away');
    }

    // Return row of game according to who's winner and loser,

    public function reportTechwin($winner, $loser)
    {
        $currentTournament = $this->currentTournament();

        if ($currentTournament['Tournament']['status'] == Tournament::GROUP) {
            $this->Group->Standing->recursive = 1;

            $winnerStanding = $this->Group->Standing->find(
                'first',
                array(
                    'conditions' => array(
                        'Standing.user_id' => $winner,
                        'Group.tournament_id' => $currentTournament['Tournament']['id']
                    )
                )
            );

            $loserStanding = $this->Group->Standing->find(
                'first',
                array(
                    'conditions' => array(
                        'Standing.user_id' => $loser,
                        'Group.tournament_id' => $currentTournament['Tournament']['id']
                    )
                )
            );

            $this->Group->updateReport($winnerStanding['Standing'], $loserStanding['Standing'], 3, 0);

            $groupId = $winnerStanding['Group']['id'];

            $this->save(array(
                'tournament_id' => $currentTournament['Tournament']['id'],
                'group_id' => $groupId,
                'playoff_id' => 0,
                'home_id' => $winner,
                'away_id' => $loser,
                'score_h' => 3,
                'score_a' => 0,
                'techwin' => true,
                'reporter_id' => AuthComponent::user('id'), // The admin.
            ));
        } elseif ($currentTournament['Tournament']['status'] == Tournament::PLAYOFF) {
            $upd = $this->Playoff->updateReport($winner, $loser);

            if ($this->HomeOrAway($this->id, $winner) == 'home') {
                if ($upd['stepAssoc'] == 'Final' || $upd['stepAssoc'] == 'Third Place') {
                    $score_h = 4;
                } else {
                    $score_h = 3;
                }
                $score_a = 0;
            } else {
                $score_h = 0;
                if ($upd['stepAssoc'] == 'Final' || $upd['stepAssoc'] == 'Third Place') {
                    $score_a = 4;
                } else {
                    $score_a = 3;
                }
            }

            $this->save(array(
                    'id' => $upd['reportedGame'],
                    'playoff_id' => $upd['reportedPO'],
                    'score_h' => $score_h,
                    'score_a' => $score_a,
                    'reporter_id' => AuthComponent::user('id'), // The admin.
                    'tournament_id' => $currentTournament['Tournament']['id'],
                    'techwin' => true
                ),
                false,
                array(
                    'playoff_id', 'score_h', 'score_a', 'reporter_id', 'created', 'techwin'
                )
            );
        }

        // Writes some nice Tournament News to the Infoboard.
        $id = $this->id;
        $User = ClassRegistry::init('User');
        $winnerUsername = $User->field('username', array('id' => $winner));
        $loserUsername = $User->field('username', array('id' => $loser));
        $IBwinner = "<a href=\"/users/view/$winner\">$winnerUsername</a>";
        $IBloser = "<a href=\"/users/view/$loser\">$loserUsername</a>";
        $IBresult = "<a href=\"/games/view/$id\">3-0</a>";

        $Infoboard = ClassRegistry::init('Infoboard');
        $Infoboard->save(array(
            'message' => "$IBwinner defeated $IBloser by $IBresult (tech. win)",
            'category' => 3
        ));

        return true;
    }

    // Returns true/false according whether the game has alread been reported.

    public function gameWL($winner, $loser)
    {
        $this->unbindModel(
            array('hasMany' => array('Tournament'))
        );

        $this->recursive = 1;

        // The problematic thing about this is,
        // figuring out whether home or away is winner.
        $game1 = $this->find('first', array(
            'conditions' => array(
                'Game.home_id' => $winner,
                'Game.away_id' => $loser,
                'Game.reporter_id' => 0
            )
        ));
        $game2 = $this->find('first', array(
            'conditions' => array(
                'home_id' => $loser,
                'away_id' => $winner,
                'Game.reporter_id' => 0
            ),
        ));

        $game = $game1 ? $game1 : $game2;
        return $game;
    }

    public function isReported($p1, $p2, $status = false)
    {
        $currentTournament = $this->currentTournament();

        if ($status == Tournament::GROUP && $currentTournament['Tournament']['status'] == Tournament::PLAYOFF) {
            return true;
        }

        if ($currentTournament['Tournament']['status'] == Tournament::GROUP) {
            $not_in = 'playoff_id';
        } else {
            $not_in = 'group_id';
        }

        $isReported = $this->find('count', array(
            'conditions' => array(
                'OR' => array(
                    array(
                        'Game.home_id' => $p1,
                        'Game.away_id' => $p2
                    ),
                    array(
                        'Game.away_id' => $p1,
                        'Game.home_id' => $p2
                    )
                ),
                'AND' => array(
                    'Game.' . $not_in => 0,
                    'Game.reporter_id !=' => 0
                )
            )
        ));

        return (bool)$isReported;
    }

    public function playedby($userId = false)
    {
        $userId = $userId ? $userId : AuthComponent::user('id');

        return $this->find('all', array(
            'conditions' => array(
                'OR' => array(
                    'Game.away_id' => $userId,
                    'Game.home_id' => $userId
                )
            )
        ));
    }

    // This is actually not secure as everything is checked client-side.

    public function replaySecurity($data)
    {
        $allowedMIMEsZip = array(
            'application/zip', 'application/x-compressed', 'application/x-zip-compressed',
            'multipart/x-zip', 'application/octet-stream'
        );

        $allowedMIMEsRAR = array(
            'application/rar', 'application/x-compressed', 'application/x-rar-compressed',
            'multipart/x-rar', 'application/octet-stream', 'application/exe'

        );

        if ((substr($data['replays']['name'], -4) != '.zip'
                && substr($data['replays']['name'], -4) != '.rar')
            || (!in_array($data['replays']['type'], $allowedMIMEsZip)
                && !in_array($data['replays']['type'], $allowedMIMEsRAR))
        ) {
            return false;
        }
        return true;
    }

    public function allowedResults($data)
    {
        return true;

        $Tournament = ClassRegistry::init('Tournament');
        $tourney = $Tournament->info();

        $allowedResults = array('3-0', '3-1', '3-2', '0-3', '1-3', '2-3');
        $inputResult = $data['userScore'] . '-' . $this->data['Game']['opponentScore'];

        if ($tourney['status'] == 'playoff') {
            $currentGame = $this->Playoff->currentGame();

            if ($currentGame['Playoff']['step'] > 3) {
                $allowedResults['4'] = '4';
            }
        }

        if (!in_array($inputResult, $allowedResults)) {
            $this->invalidate('opponentScore');
            return false;
        }
        return true;
    }

    public function isUser($data)
    {
        if (AuthComponent::user('id') != $data['user']) {
            return false;
        }
        return true;
    }

    public function allowedOpponent($data)
    {
        if (AuthComponent::user('stage') == 'group') {
            $allowedOpponents = $this->Group->allowedOpponents($this->Group->attendees());
        } elseif (AuthComponent::user('stage') == 'playoff') {
            $allowedOpponents = $this->Playoff->allowedOpponents($this->Playoff->attendees());
        }

        if (!array_key_exists($data['opponent'], $allowedOpponents)) {
            return false;
        }
        return true;
    }
}
