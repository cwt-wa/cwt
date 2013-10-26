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

        $group = $this->Standing->find('first', array(
            'conditions' => array(
                'Standing.user_id' => $userId
            )
        ));

        if (empty($group)) {
            return null;
        }

        return $group['Group']['label'];
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

        $currentTournament = $this->currentTournament();

        $groups = array('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H');
        $playerCounter = 1;

        foreach ($groups as $group) {
            $this->create();
            $this->save(array(
                'tournament_id' => $currentTournament['Tournament']['id'],
                'label' => $group,
            ));

            for ($i = 0; $i < 4; $i++) {
                $this->Standing->create();
                $this->Standing->save(array(
                    'group_id' => $this->id,
                    'user_id' => $data['player' . $playerCounter]
                ));
                $playerCounter++;
            }
        }

        return true;
    }

    /**
     * How many users have applied for participation?
     *
     * @return integer number of users who applied.
     */
    public function numberOfApplicants() {
        $applicants = ClassRegistry::init('Application')->find('count');
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
        $currentTournament = $this->currentTournament();

        foreach($attendees as $groupmateID => $ID) {
            // Remove yourself from opponents.
            if($groupmateID == $userId) {
                unset($attendees[$groupmateID]);
            }

            // Remove players you've already played against.
            $playedGame[1] = $this->Game->find('count', array(
                'conditions' => array(
                    'Game.home_id' => $userId,
                    'Game.away_id' => $groupmateID,
                    'Game.tournament_id' => $currentTournament['Tournament']['id']
                )
            ));
            $playedGame[2] = $this->Game->find('count', array(
                'conditions' => array(
                    'Game.home_id' => $groupmateID,
                    'Game.away_id' => $userId,
                    'Game.tournament_id' => $currentTournament['Tournament']['id']
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
        $currentTournament = $this->currentTournament();

        $attendees = array();

        $users = $this->Standing->find(
            'all',
            array(
                'conditions' => array(
                    'Group.tournament_id' => $currentTournament['Tournament']['id'],
                    'Group.label' => $groupString
                )
            )
        );

        foreach ($users as $user) {
            $attendees[$user['User']['id']] = $user['User']['username'];
        }

        return $attendees;
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

    public function updateReport($oldwinner, $oldloser, $score_w, $score_l) {
        $result = $score_w . '-' . $score_l;

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

        $newWinner = array(
            'id' => $oldwinner['id'],
            'user_id' => $oldwinner['user_id'],
            'points' => $newWinnerPoints,
            'games' => $oldwinner['games'] + 1,
            'game_ratio' => $oldwinner['game_ratio'] + 1,
            'round_ratio' => $oldwinner['round_ratio'] - $score_l + $score_w
        );
        $newLoser = array(
            'id' => $oldloser['id'],
            'user_id' => $oldloser['user_id'],
            'points' => $newLoserPoints,
            'games' => $oldloser['games'] + 1,
            'game_ratio' => $oldloser['game_ratio'] - 1,
            'round_ratio' => $oldloser['round_ratio']  - $score_w + $score_l
        );

        $this->Standing->save($newWinner);
        $this->Standing->save($newLoser);
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
        $groupArray = array('*', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H');
        $groups = $this->find('list', array(
            'conditions' => array(
                'tournament_id' => $tournamentId
            ),
            'fields' => array('Group.id')
        ));
        $group = array();
        $games = $this->Game->recursive = 1;
        $games = $this->Game->find('all', array(
            'conditions' => array(
                'Game.tournament_id' => $tournamentId,
                'Game.playoff_id' => 0, // Why do I need to specify this, I've already done that on the model association?
                'Game.group_id !=' => 0
            ),
            'order' => 'Game.created DESC'
        ));

        for ($i = 1; $i <= 8; $i++) {
            $currentlyLoopedGroupId = array_slice($groups, $i - 1, $i);
            $currentlyLoopedGroupId = $currentlyLoopedGroupId[0];
            $group[$i]['group'] = $groupArray[$i];

            $this->Standing->recursive = 0;
            $groupAll = $this->Standing->find('all', array(
                'conditions' => array(
                    'Group.tournament_id' => $tournamentId,
                    'Group.label' => $groupArray[$i]
                ),
                'order' => 'Standing.points DESC, Standing.game_ratio DESC, Standing.round_ratio DESC'
            ));

            for ($i2 = 0; $i2 < count($groupAll); $i2++) {
                $group[$i][$i2 + 1] = array(
                    'User' => $groupAll[$i2]['User'],
                    'Group' => $groupAll[$i2]['Group'],
                    'Standing' => $groupAll[$i2]['Standing']
                );
                $group[$i][$i2 + 1]['User']['flag'] = $this->Standing->User->Profile->displayCountry($groupAll[$i2]['User']['id']);
            }

            $cGames = 1;
            foreach ($games as $game) {
                if ($currentlyLoopedGroupId == $game['Group']['id']) {
                    $group[$i]['Game'][$cGames] = $game;
                    $group[$i]['Game'][$cGames]['Rating'][0] = $Rating->ratingStats($game['Game']['id']);
                    $cGames++;
                }
            }
        }
        return $group;
    }
}
