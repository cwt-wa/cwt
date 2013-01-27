<?php
App::uses('AppModel', 'Model');

class Group extends AppModel {
    public $name = 'Group';
    public $displayField = 'group';

    public $groupAssoc = array(
        'A' => array(1, 2, 3, 4),
        'B' => array(5, 6, 7, 8),
        'C' => array(9, 10, 11, 12),
        'D' => array(13, 14, 15, 16),
        'E' => array(17, 18, 19, 20),
        'F' => array(21, 22, 23, 24),
        'G' => array(25, 26, 27, 28),
        'H' => array(29, 30, 31, 32)
    );
    
    public $hasMany = array(
        'Game' => array(
            'className'  => 'Game',
            'foreignKey' => 'group_id',
            'conditions' => array('Game.playoff_id' => 0)
        )
    );
    public $belongsTo = array(
        'User' => array(
            'className'  => 'User',
            'foreignKey' => 'user_id'
        )
    );

    // Return the group $user is in.
    public function getGroup($user = false) {
        $user = $user ? $user : AuthComponent::user('id');
        $group = $this->find('first', array(
            'conditions' => array('Group.user_id' => $user)));
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

        $groups = array('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H');
        $numGroup = 0;
        for($i = 1; $i <= 32; $i++) {
            unset($this->id);
            $this->save(array(
                    'group' => $groups[$numGroup],
                    'user_id' => $data['player' . $i]
            ));
            if($i % 4 == 0) {$numGroup++;}
        }
        return true;
    }

    // Returns number of current applications.
    public function applicants() {
        $applicants = $this->User->find('count',
            array('conditions' => array('stage' => 'applied')));

        if($applicants >= 32) {return true;}
        else {return false;}
    }

    // Get opponents the current user is allowed to play against.
    public function allowedOpponents($attendees) {
        foreach($attendees as $groupmateID => $ID) {
            // Remove yourself from opponents.
            if($groupmateID == AuthComponent::user('id')) {
                unset($attendees[$groupmateID]);
            }
            
            // Remove players you've already played against.
            $playedGame[1] = $this->Game->find('count', array(
                'conditions' => array(
                    'home_id' => AuthComponent::user('id'),
                    'away_id' => $groupmateID
                )
            ));
            $playedGame[2] = $this->Game->find('count', array(
                'conditions' => array(
                    'home_id' => $groupmateID,
                    'away_id' => AuthComponent::user('id')
                )
            ));
            if($playedGame[1] || $playedGame[2]) {
                unset($attendees[$groupmateID]);
            }
        }

        return $attendees;
    }

    // Get id and name of users of a certain group.
    public function attendees() {
        $group = $this->getGroup();

        // Get the players' rows in groups table.
        $playerIDs = array(
            $this->groupAssoc[$group][0],
            $this->groupAssoc[$group][1],
            $this->groupAssoc[$group][2],
            $this->groupAssoc[$group][3],
        );

        // Get users' ids in the current group.
        $userID = $this->find('list', array(
            'conditions' => array('Group.group' => $group),
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
}