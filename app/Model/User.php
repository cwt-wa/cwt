<?php
App::uses('AppModel', 'Model');

class User extends AppModel
{
    /**
     * The user is retired and by that is not part of the current tournament (anymore).
     * This can be the case in all of the current tournament's stages.
     */
    const RETIRED = 0;
    /**
     * The user is willing to play the next tournament and has applied for participation.
     * This can be the case in the Tournament::PENDING stage of the tournament.
     */
    const APPLIED = 1;
    /**
     * The user is in the group stage.
     * This can be the case in the Tournament::GROUP stage of the tournament.
     */
    const GROUP = 2;
    /**
     * The user is in the playoff stage.
     * This can be the case in the Tournament::PLAYOFF stage of the tournament.
     */
    const PLAYOFF = 3;
    public $name = 'User';
    public $displayField = 'username';
    public $hasOne = array(
        'Profile' => array(
            'className' => 'Profile',
            'foreignKey' => 'user_id',
            'dependent' => true
        ),
        'Application' => array(
            'className' => 'Application',
            'foreignKey' => 'user_id'
        ),
        'Stream' => array(
            'className' => 'Stream',
            'foreignKey' => 'maintainer_id'
        ),
        'News' => array(
            'className' => 'News',
            'foreignKey' => 'user_id'
        )
    );
    public $hasAndBelongsToMany = array(
        'Moderator' => array(
            'className' => 'Tournament',
            'joinTable' => 'tournaments_moderators',
            'foreignKey' => 'moderator_id',
            'associationForeignKey' => 'tournament_id',
            'unique' => false
        ),
    );
    public $hasMany = array(
        'Standing' => array(
            'className' => 'Standing',
            'foreignKey' => 'user_id'
        ),
        'Home' => array(
            'className' => 'Game',
            'foreignKey' => 'home_id'
        ),
        'Away' => array(
            'className' => 'Game',
            'foreignKey' => 'away_id'
        ),
        'Report' => array(
            'className' => 'Game',
            'foreignKey' => 'reporter_id'
        ),
        'Gold' => array(
            'className' => 'Tournament',
            'foreignKey' => 'gold_id'
        ),
        'Silver' => array(
            'className' => 'Tournament',
            'foreignKey' => 'silver_id'
        ),
        'Bronze' => array(
            'className' => 'Tournament',
            'foreignKey' => 'bronze_id'
        ),
        'Comment' => array(
            'className' => 'Comment',
            'foreignKey' => 'user_id'
        ),
        'Infoboard' => array(
            'className' => 'Infoboard',
            'foreignKey' => 'user_id'
        ),
        'Rule' => array(
            'className' => 'Rule',
            'foreignKey' => 'user_id'
        ),
        'Trace' => array(
            'className' => 'Trace',
            'foreignKey' => 'user_id'
        ),
        'Scheduler' => array( // Scheduled by.
            'className' => 'Schedule',
            'foreignKey' => 'home_id'
        ),
        'Scheduled' => array( //  Scheduled against.
            'className' => 'Schedule',
            'foreignKey' => 'away_id'
        ),
        'HomeRestore' => array(
            'className' => 'Restore',
            'foreignKey' => 'home_id'
        ),
        'AwayRestore' => array(
            'className' => 'Game',
            'foreignKey' => 'away_id'
        ),
        'Restore' => array(
            'className' => 'Restore',
            'foreignKey' => 'submitter_id'
        ),
    );
    public $validate = array(
        'username' => array(
            'notEmpty' => array(
                'rule' => array('notEmpty'),
                'message' => 'You definitely need a username.'
            ),
            'between' => array(
                'rule' => array('between', 3, 16),
                'message' => 'Username must be between 3 and 16 characters long.'
            ),
            'isUnique' => array(
                'rule' => array('isUnique'),
                'message' => 'This username has already been taken.'
            ),
            'alphaNumeric' => array(
                'rule' => array('alphaNumeric'),
                'message' => 'Your username may only contain letters and numbers.'
            ),
            'AdminPrivilege' => array(
                'rule' => array('AdminPrivilege'),
                'message' => 'Your username can\'t be â€œadminsâ€�.'
            )
        ),
        'password' => array(
            'notEmpty' => array(
                'rule' => array('notEmpty'),
                'message' => 'Please protect your account with a password.'
            ),
            'compare_passwords' => array(
                'rule' => array('compare_passwords'),
                'message' => 'Your passwords do not match.'
            )
        ),
        'password_confirmation' => array(
            'notEmpty' => array(
                'rule' => array('notEmpty'),
                'message' => 'Please repeat your password.'
            ),
        )
    );

    /**
     * DEPRECATED - Use AppModel's getVisitorIp() instead.
     */
    public function realIP()
    {
        // Test if it is a shared client.
        if (!empty($_SERVER['HTTP_CLIENT_IP'])) {
            return $_SERVER['HTTP_CLIENT_IP'];
            // Is it a proxy address?
        } elseif (!empty($_SERVER['HTTP_X_FORWARDED_FOR'])) {
            return $_SERVER['HTTP_X_FORWARDED_FOR'];
        } else {
            return $_SERVER['REMOTE_ADDR'];
        }
    }

    // In order to maintain the old passwords we do our own login method.

    public function update($data)
    {
        $first = $this->find('first', array(
            'conditions' => array(
                'User.username' => $data['username']
            )
        ));

        // md5password record is empty, which means the user's password has already been updated.
        if ($first['User']['md5password'] == '') {
            if ($first['User']['password'] == AuthComponent::password($data['password'])
                && $first['User']['username'] == $data['username']
            ) {
                return $first['User'];
            }

            return false;
        }

        $second = $this->find('first', array(
            'conditions' => array(
                'User.username' => $data['username']
            )
        ));

        // User's password has yet to be updated.
        if ($first['User']['md5password'] == md5($data['password'])
            && $first['User']['username'] == $data['username']
        ) {
            $this->id = $first['User']['id'];
            $this->save(array(
                'id' => $this->id,
                'md5password' => '',
                'password' => $data['password']
            ));

            return $second['User'];
        }

        return false;
    }

    // Change password.

    /**
     * Generate a random password.
     *
     * @return string A random String (the password).
     */
    function randomPassword()
    {
        $alphabet = "abcdefghijklmnopqrstuwxyzABCDEFGHIJKLMNOPQRSTUWXYZ0123456789";
        $pass = array();
        $alphaLength = strlen($alphabet) - 1;
        for ($i = 0; $i < 8; $i++) {
            $n = rand(0, $alphaLength);
            $pass[] = $alphabet[$n];
        }
        return implode($pass);
    }

    // This function reads the timeline of a user.

    public function password($data)
    {
        $this->id = AuthComponent::user('id');

        if (AuthComponent::password($data['old']) == $this->field('password')
            && $data['new1'] == $data['new2']
        ) {
            $this->save(array(
                'password' => $data['new1']
            ), false);
            return true;
        }
        return false;
    }

    public function timeline($userID)
    {
        $user = $this->find('first', array(
            'conditions' => array(
                'User.id' => $userID
            )
        ));

        return str_split($user['User']['timeline']);
    }

    // Dispaly user's photo or random one.

    public function games($id)
    {
        $home = $this->find('all', array(
            'conditions' => array(
                'Game.home_id' => $id
            )
        ));

        $away = $this->find('all', array(
            'conditions' => array(
                'Game.away_id' => $id
            )
        ));

        return array_merge($home, $away);
    }

    // Checking status of user and set user menu according to that.

    public function displayPhoto($user)
    {
        $folder = new Folder('img/users');

        $photo = $folder->find("\b$user\b.*");

        if (empty($photo)) {
            return 'random/' . rand(1, 14) . '.jpg';
        }

        return $photo[0];
    }

    // Logs the user out and in again to update the session.

    public function user_menu()
    {
        $currentTorunament = ClassRegistry::init('Tournament')->currentTournament();

        switch ($currentTorunament['Tournament']['status']) {
            case Tournament::PENDING:
                if (AuthComponent::user('stage') == 'retired') {
                    return 'apply';
                }
                break;
            case Tournament::GROUP:
                $playedGames = ClassRegistry::init('Game')->find('count', array(
                    'conditions' => array(
                        'OR' => array(
                            'Game.home_id' => AuthComponent::user('id'),
                            'Game.away_id' => AuthComponent::user('id')
                        ),
                        'Game.playoff_id' => 0,
                        'Game.reporter_id !=' => 0
                    )
                ));

                if (AuthComponent::user('stage') == 'group'
                    && $playedGames < 3
                ) {
                    return 'report';
                }
                break;
            case Tournament::PLAYOFF:
                $hasOpp = ClassRegistry::init('Game')->find('count', array(
                    'conditions' => array(
                        'OR' => array(
                            array(
                                'Game.home_id' => AuthComponent::user('id'),
                                'Game.away_id !=' => 0
                            ),
                            array(
                                'Game.home_id !=' => 0,
                                'Game.away_id' => AuthComponent::user('id')
                            )
                        ),
                        'Game.group_id' => 0,
                        'Game.reporter_id' => 0
                    )
                ));

                if ($hasOpp && AuthComponent::user('stage') == 'playoff') {
                    return 'report';
                }
                break;
        }
    }

    public function re_login()
    {
        $this->id = AuthComponent::user('id');
        $user = $this->read();
        return $user['User'];
    }

    public function AdminPrivilege($data)
    {
        return (bool)strcasecmp($data['username'], 'admins');
    }

    public function compare_passwords($data)
    {
        if ($data['password'] == $this->data['User']['password_confirmation']) {
            return true;
        }
        $this->invalidate('password_confirmation', 'Your passwords do not match.');
        return false;
    }

    public function beforeSave($options = array())
    {
        if (isset($this->data['User']['password'])) {
            $this->data['User']['password'] = AuthComponent::password($this->data['User']['password']);
        }
        return true;
    }

    /**
     * Is the user still in the tournament, not yet out, can actually still report games? I'm listening to Rihanna right now.
     *
     * @param int|null $userId The user to check, if not supplied or null it is the logged in user.
     * @return True if yeah, false if nay.
     */
    public function isStillInTournament($userId = null) {
        $userId = $userId === null ? AuthComponent::user('id') : $userId;

        $Tournament = ClassRegistry::init('Tournament');
        $currentTournament = $Tournament->currentTournament();

        if ($currentTournament['Tournament']['status'] == Tournament::GROUP) {
            $Group = ClassRegistry::init('Group');
            $userGroup = $Group->findGroup($userId);

            if ($userGroup === null) {
                return false;
            }

            $isInGroupStage = $Group->allowedOpponents($Group->attendees($userGroup), $userId);
        } elseif ($currentTournament['Tournament']['status'] == Tournament::PLAYOFF) {
            $Playoff = ClassRegistry::init('Playoff');
            $isInPlayoffStage = $Playoff->allowedOpponents(null, $userId);
        }

        if (empty($isInGroupStage) && empty($isInPlayoffStage)) {
            return false;
        }
        return true;
    }

    /**
     * Get a select input field compatible list of users that are still in the tournament.
     * Definition of still being in a tournament: You can report games.
     *
     * @return array
     */
    public function getAllUsersStillInTournament() {
        $allUsers = $this->find('list');

        foreach ($allUsers as $userId => $username) {
            if (!$this->isStillInTournament($userId)) {
                unset($allUsers[$userId]);
            }
        }

        return $allUsers;
    }

    /**
     * Get CakePHP list of users the given user can report a game against.
     *
     * @param null $userId
     * @return array
     */
    public function findAllowedOpponents($userId = null) {
        $userId = $userId ? $userId : AuthComponent::user('id');
        $currentTournament = ClassRegistry::init('Tournament')->currentTournament();

        if ($currentTournament['Tournament']['status'] == Tournament::GROUP) {
            $Group = ClassRegistry::init('Group');
            $userGroup = $Group->findGroup($userId);

            if ($userGroup === null) {
                return array();
            }

            $allowedOpponents = $Group->allowedOpponents($Group->attendees($userGroup), $userId);
        } elseif ($currentTournament['Tournament']['status'] == Tournament::PLAYOFF) {
            $Playoff = ClassRegistry::init('Playoff');
            $allowedOpponents = $Playoff->allowedOpponents(null, $userId);
        }

        return $allowedOpponents;
    }

    public function findAllUsersInGroupStage() {
        $groups = $this->Group->find('all');
        $usersInGroupStage = array();

        foreach ($groups as $group) {
            $usersInGroupStage[$group['User']['id']] = $group['User']['username'];
        }

        return $usersInGroupStage;
    }
}
