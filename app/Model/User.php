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
        )
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

    public function gatherAchievements() {
        $Tournament = ClassRegistry::init('Tournament');
        $Tournament->recursive = 0;
        $tournaments = $Tournament->find('all');
        $year = 2002;

        foreach ($tournaments as $key => $val) {
            $achievements[$year]['gold'] = $val['Gold']['id'];
            $achievements[$year]['silver'] = $val['Silver']['id'];
            $achievements[$year]['bronze'] = $val['Bronze']['id'];
            $year++;
        }

        if ($achievements == null) {
            $achievements = array();
        }

        return $achievements;
    }

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
        $this->recursive = -1;
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
        $currentTournament = $this->currentTournament();

        switch ($currentTournament['Tournament']['status']) {
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
                        'Game.reporter_id !=' => 0,
                        'Game.tournament_id' => $currentTournament['Tournament']['id']
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
                        'Game.reporter_id' => 0,
                        'Game.tournament_id' => $currentTournament['Tournament']['id']
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
    public function isStillInTournament($userId = null)
    {
        $userId = $userId === null ? AuthComponent::user('id') : $userId;

        $currentTournament = $this->currentTournament();

        if ($currentTournament['Tournament']['status'] == Tournament::GROUP) {
            $Group = ClassRegistry::init('Group');
            $userGroup = $Group->findGroup($userId, $currentTournament['Tournament']['id']);

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
    public function getAllUsersStillInTournament()
    {
        $this->recursive = 1;
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
    public function findAllowedOpponents($userId = null)
    {
        $userId = $userId ? $userId : AuthComponent::user('id');
        $currentTournament = ClassRegistry::init('Tournament')->currentTournament();

        if ($currentTournament['Tournament']['status'] == Tournament::GROUP) {
            $Group = ClassRegistry::init('Group');
            $userGroup = $Group->findGroup($userId, $currentTournament['Tournament']['id']);

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

    public function findAllUsersInGroupStage()
    {
        $usersInGroupStage = ClassRegistry::init('User')->find(
            'list',
            array(
                'conditions' => array(
                    'User.stage' => 'group'
                )
            )
        );

        return $usersInGroupStage;
    }

    public function replaceUsers($legacyUserIds, $newUserId) {
        $relationships = array_merge($this->hasOne, $this->hasMany);

        foreach ($this->hasAndBelongsToMany as $hasAndBelongsToManyRelationship) {
            $relationships[$hasAndBelongsToManyRelationship['joinTable']] = array(
                'className' => $hasAndBelongsToManyRelationship['joinTable'],
                'foreignKey' => $hasAndBelongsToManyRelationship['foreignKey']
            );
        }

        $queries = array();

        foreach ($relationships as $relationship) {
            $table = Inflector::tableize($relationship['className']);
            $sql = "UPDATE `$table` SET `{$relationship['foreignKey']}`=$newUserId WHERE `{$relationship['foreignKey']}`={$legacyUserIds[0]}";

            for ($i = 1; $i < count($legacyUserIds); $i++) {
                    $sql .= " OR `{$relationship['foreignKey']}`={$legacyUserIds[$i]}";
            }

            $queries[] = $sql;
        }

        debug($relationships);
        debug($queries);
        debug(implode('; ', $queries));

        $this->query(implode('; ', $queries));

        App::uses('TimelineShell', 'Console/Command');
        $timelineShell = new TimelineShell();
        $userToUpdateAsListRequest = $this->find('list',
            array('conditions' => array('id' => $newUserId)));
        $tournamentToUpdateAsListRequest = ClassRegistry::init('Tournament')->find('list',
            array('conditions' => array('status' => Tournament::ARCHIVED)));
        $timelineShell->main($userToUpdateAsListRequest, $tournamentToUpdateAsListRequest);

        foreach ($legacyUserIds as $legacyUserId) {
            $this->delete($legacyUserId);
        }

        return $queries;

    }

    /**
     * @see http://book.cakephp.org/2.0/en/models/callback-methods.html#beforedelete
     * @param bool $cascade
     * @return bool
     */
    public function beforeDelete($cascade = true) {
        CakeLog::write('user_deletion',
            'A user is going to be deleted! ' . print_r($this->findById($this->id), true));
        return true;
    }
}
