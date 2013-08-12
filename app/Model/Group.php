<?php
App::uses('AppModel', 'Model');

class Group extends AppModel {

    public $name = 'Group';
    public $displayField = 'group';
    public $hasMany = array(
        'Game' => array(
            'className'  => 'Game',
            'foreignKey' => 'group_id',
             'conditions' => array('Game.playoff_id' => 0)
        ),
        'Standing' => array(
            'className'  => 'Standing',
            'foreignKey' => 'group_id'
        )
    );

    /**
     * Find the group the given user is or was in.
     *
     * @param null $userId The user's id to find the group of or the currently logged in user if the param wasn't given.
     * @return String The capital letter of the group or null if th user was not found in any group.
     */
    public function findGroup($userId = null) {
        $userId = $userId ? $userId : AuthComponent::user('id');

        $group = $this->find('first', array(
            'conditions' => array(
                'Group.user_id' => $userId
            )
        ));

        if (empty($group)) {
            return null;
        }

        return $group['Group']['group'];
    }

    // Assign players to the groups.

    public function start($data) {
        // Checking if a user has been assigned multiple times.
        $duplicates = array_count_values($data);
        for($i = 1; $i <= 32; $i++) {
            if($duplicates[$data['player' . $i]] > 1) {
                return false;
            }
        }

        $currentTournament = ClassRegistry::init('Tournament')->currentTournament();

        $groups = array('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H');
        $numGroup = 0;
        for($i = 1; $i <= 32; $i++) {
            $this->create();
            $this->save(array(
                    'group' => $groups[$numGroup],
                    'user_id' => $data['player' . $i],
                    'tournament_id' => $currentTournament['Tournament']['id']
            ));
            if($i % 4 == 0) {$numGroup++;}
        }
        return true;
    }

    /**
     * How many users have applied for participation?
     *
     * @return integer number of users who applied.
     */
    public function numberOfApplicants() {
        $applicants = $this->User->Application->find('count');
        return $applicants;
    }

    /**
     * Get all the users the given user is allowed to report a game against.
     *
     * @param $attendees Provide it with this class's attendees() method. @TODO This method should do that itself.
     * @param int $userId The user the opponents are to be found of. If not provided it's the currently logged in user.
     * @return array What's left of $attendees after users $userId isn't allowed to play against are removed.
     */
    public function allowedOpponents($attendees, $userId = null) {
        $userId = $userId ? $userId : AuthComponent::user('id');

        foreach($attendees as $groupmateID => $ID) {
            // Remove yourself from opponents.
            if($groupmateID == $userId) {
                unset($attendees[$groupmateID]);
            }

            // Remove players you've already played against.
            $playedGame[1] = $this->Game->find('count', array(
                'conditions' => array(
                    'home_id' => $userId,
                    'away_id' => $groupmateID
                )
            ));
            $playedGame[2] = $this->Game->find('count', array(
                'conditions' => array(
                    'home_id' => $groupmateID,
                    'away_id' => $userId
                )
            ));
            if($playedGame[1] || $playedGame[2]) {
                unset($attendees[$groupmateID]);
            }
        }

        return $attendees;
    }

    /**
     * Get the users of a group as CakePHP list.
     * @TODO Misleading method name.
     *
     * @param $groupString
     * @return array
     */
    public function attendees($groupString) {
        // Get the players' rows in groups table.
        $playerIDs = array(
            $this->groupAssoc[$groupString][0],
            $this->groupAssoc[$groupString][1],
            $this->groupAssoc[$groupString][2],
            $this->groupAssoc[$groupString][3],
        );

        // Get users' ids in the current group.
        $userID = $this->find('list', array(
            'conditions' => array('Group.group' => $groupString),
            'fields' => array('Group.user_id')
        ));

        // Get users' names in the current group.
        $userName[0] = $this->User->find('list', array(
            'conditions' => array('User.id' => $userID[$playerIDs[0]])));
        $userName[1] = $this->User->find('list', array(
            'conditions' => array('User.id' => $userID[$playerIDs[1]])));
        $userName[2] = $this->User->find('list', array(
            'conditions' => array('User.id' => $userID[$playerIDs[2]])));
        $userName[3] = $this->User->find('list', array(
        'conditions' => array('User.id' => $userID[$playerIDs[3]])));

        return array(
            $userID[$playerIDs[0]] => $userName[0][$userID[$playerIDs[0]]],
            $userID[$playerIDs[1]] => $userName[1][$userID[$playerIDs[1]]],
            $userID[$playerIDs[2]] => $userName[2][$userID[$playerIDs[2]]],
            $userID[$playerIDs[3]] => $userName[3][$userID[$playerIDs[3]]]
        );
    }

    // Replace a player.

    public function replacePlayer($data) {
        $Game = ClassRegistry::init('Game');
        $User = ClassRegistry::init('User');
        $data['Inactive']['id'] = $data['Group']['Inactive'];
        $data['Active']['id'] = $data['Group']['Active'];
        $data['Inactive']['games'] = $Game->playedby($data['Inactive']['id']);

        foreach($data['Inactive']['games'] as $game) {
            // Who's the opponent of Inactive?
            if($game['Game']['home_id'] == $data['Inactive']['id']) {
                $opponent['id'] = $game['Game']['away_id'];
                $opponent['score'] = $game['Game']['score_a'];

                $inactive['id'] = $game['Game']['home_id'];
                $inactive['score'] = $game['Game']['score_h'];
            } else {
                $opponent['id'] = $game['Game']['home_id'];
                $opponent['score'] = $game['Game']['score_h'];

                $inactive['id'] = $game['Game']['away_id'];
                $inactive['score'] = $game['Game']['score_a'];
            }

            switch($opponent['score']) {
                case '3':
                    $opponent['new']['points'] = -3;
                    $opponent['new']['game_ratio'] = -1;
                break;
                case '2':
                    $opponent['new']['points'] = -1;
                    $opponent['new']['game_ratio'] = 1;
                break;
                case '1':
                case '0':
                    $opponent['new']['game_ratio'] = 1;
            }

            $opponent['current'] = $this->find('first', array(
                'conditions' => array(
                    'Group.user_id' => $opponent['id']
                )
            ));

            $opponent['new']['id'] = $opponent['current']['Group']['id'];

            $opponent['new']['points'] =
                $opponent['new']['points']
                + $opponent['current']['Group']['points'];

            $opponent['new']['game_ratio'] =
                $opponent['new']['game_ratio']
                + $opponent['current']['Group']['game_ratio'];

            $opponent['new']['games'] =
                $opponent['current']['Group']['games'] - 1;

            $opponent['new']['round_ratio'] =
                $opponent['current']['Group']['round_ratio']
                - ($opponent['score'] - $inactive['score']);

            // debug($opponent['current']['User']['username']);
            // debug($opponent['current']['Group']);
            // debug($opponent['new']);

            $this->save($opponent['new']);
            $Game->delete($game['Game']['id']);
        }

        $this->updateAll(array(
            'Group.user_id' => $data['Active']['id'],
            'Group.points' => 0,
            'Group.games' => 0,
            'Group.game_ratio' => 0,
            'Group.round_ratio' => 0
        ), array(
            'Group.user_id' => $data['Inactive']['id']
        ));

        $User->save(array(
            'id' => $data['Inactive']['id'],
            'stage' => 'retired'
        ));

        $User->save(array(
            'id' => $data['Active']['id'],
            'stage' => 'group'
        ));
    }

    // A new group game has been reported. Call by GameModel.

    public function updateReport($winner, $loser, $score_w, $score_l) {
        $result = $score_w . '-' . $score_l;

        // Getting the rows of winner an loser in groups table.
        $winner_row = $this->field('id', array('user_id' => $winner));
        $loser_row = $this->field('id', array('user_id' => $loser));
        $oldwinner = $this->read(null, $winner_row); $oldwinner = $oldwinner['Group'];
        $oldloser = $this->read(null, $loser_row); $oldloser = $oldloser['Group'];

        // Calculating the new points.
        switch($result) {
            case '3-0':
                $newWinnerPoints = $oldwinner['points'] + 3;
                $newLoserPoints = $oldloser['points'];
            break;
            case '3-1':
                $newWinnerPoints = $oldwinner['points'] + 3;
                $newLoserPoints = $oldloser['points'];
            break;
            case '3-2':
                $newWinnerPoints = $oldwinner['points'] + 3;
                $newLoserPoints = $oldloser['points']  + 1;
        }

        // Arrays of new information.
        $newWinner = array(
            'id' => $winner_row,
            'user_id' => $winner,
            'points' => $newWinnerPoints,
            'games' => $oldwinner['games'] + 1,
            'game_ratio' => $oldwinner['game_ratio'] + 1,
            'round_ratio' => $oldwinner['round_ratio'] - $score_l + $score_w
        );
        $newLoser = array(
            'id' => $loser_row,
            'user_id' => $loser,
            'points' => $newLoserPoints,
            'games' => $oldloser['games'] + 1,
            'game_ratio' => $oldloser['game_ratio'] - 1,
            'round_ratio' => $oldloser['round_ratio']  - $score_w + $score_l
        );

        // Update the group's table.
        $this->id = $winner_row; $this->save($newWinner);
        $this->id = $loser_row; $this->save($newLoser);
    }

    /**
     * Collects information and summarizes them for the groups page to display.
     *
     * @param $tournamentId The tournament to find the groups of.
     * @return array The data needed for the view.
     */
    public function findForGroupsPage($tournamentId = null) {
        if ($tournamentId == null) {
            $currentTournament = $this->currentTournament();
            $tournamentId = $currentTournament['Tournament']['id'];
        }

        $Rating = ClassRegistry::init('Rating');
        $this->recursive = -1; // Required for making joins work.
        $groupArray = array('*', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H');
        $group = array();
        $games = $this->Game->find('all', array(
            'conditions' => array(
                'Game.tournament_id' => $tournamentId
            ),
            'order' => 'Game.created DESC'
        ));

        for ($i = 1; $i <= 8; $i++) {
            $group[$i]['group'] = $groupArray[$i];

            $groupAll = $this->find('all', array(
                'conditions' => array(
                    'Group.tournament_id' => $tournamentId,
                    'Group.label' => $groupArray[$i]
                ),
                'joins' => array(
                    array(
                        'table' => 'standings',
                        'alias' => 'Standing',
                        'type' => 'LEFT',
                        'conditions' => array(
                            'Standing.group_id = Group.id'
                        ),
                        'order' => 'Standing.points DESC, Standing.game_ratio DESC, Standing.round_ratio DESC'
                    )
                ),
            ));

            for ($i2 = 0; $i2 <= 3; $i2++) {
                $group[$i][$i2 + 1] = array(
                    'User' => $groupAll[$i2]['User'],
                    'Group' => $groupAll[$i2]['Group']
                );
                $group[$i][$i2 + 1]['User']['flag'] = $this->User->Profile->displayCountry($groupAll[$i2]['User']['id']);
            }

            $cGames = 1;
            foreach ($games as $game) {
                if (in_array($game['Game']['group_id'], $this->groupAssoc[$groupArray[$i]])) {
                    $group[$i]['Game'][$cGames] = $game;
                    $group[$i]['Game'][$cGames]['Rating'][0] = $Rating->ratingStats($game['Game']['id']);
                    $cGames++;
                }
            }
        }
        return $group;
    }
}
