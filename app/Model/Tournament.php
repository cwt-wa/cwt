<?php

App::uses('AppModel', 'Model');

class Tournament extends AppModel
{

    public $name = 'Tournament';
    public $displayField = 'year';
    public $belongsTo = array(
        'Gold' => array(
            'className' => 'User',
            'foreignKey' => 'gold_id'
        ),
        'Silver' => array(
            'className' => 'User',
            'foreignKey' => 'silver_id'
        ),
        'Bronze' => array(
            'className' => 'User',
            'foreignKey' => 'bronze_id'
        )
    );
    public $hasAndBelongsToMany = array(
        'Moderator' => array(
            'className' => 'User',
            'joinTable' => 'tournaments_moderators',
            'foreignKey' => 'tournament_id',
            'associationForeignKey' => 'moderator_id',
            'unique' => false
        )
    );

    /**
     * The tournament is open for people to apply for participation. That's the default status.
     */
    const PENDING = 1;
    /**
     * Applied people were seeded to groups and can start playing by now.
     */
    const GROUP = 2;
    /**
     * Group stage has finished and playoff begun.
     */
    const PLAYOFF = 3;
    /**
     * The tournament's three best (gold, silver, bronze) have been found or it was manually finished by an admin.
     */
    const FINISHED = 4;
    /**
     * The tournament has been moved to the archive.
     */
    const ARCHIVED = 5;

    /**
     * The number of participants in a tournament.
     */
    const PARTICIPANTS = 32;


    /**
     * Return the current tournament in a CakePHP typical array.
     *
     * @return array|null The current tournament or null if there are only archived tournaments.
     */
    public function currentTournament()
    {
        $this->recursive = 1;
        $currentTournament = $this->find('first', array(
            'conditions' => array(
                'status !=' => Tournament::ARCHIVED
            )
        ));

        if (empty($currentTournament)) {
            return null;
        }
        return $currentTournament;
    }

    // Start a whole new tournament.
    public function start($data)
    {
        $this->save(array(
            'Tournament' => array(
                'year' => gmdate('Y'),
                'status' => Tournament::PENDING,
            )
        ));

        $this->save(array(
            'Tournament' => array(
                'id' => $this->id
            ),
            'Moderator' => array(
                'id' => AuthComponent::user('id')
            )
        ));

        $moderators = array();
        $moderators[] = AuthComponent::user('id');

        for ($i = 1; $i <= $data['Number']; $i++) {
            if (!empty($data['Helper' . $i])) {
                if (in_array($data['Helper' . $i], $moderators)) {
                    return false;
                }

                $this->save(array(
                    'Tournament' => array(
                        'id' => $this->id
                    ),
                    'Moderator' => array(
                        'id' => $data['Helper' . $i]
                    )
                ));
            }
        }

        return true;
    }

    // Listing the organizers team in the footer.
    public function niceStaff()
    {
        $tourney = $this->info();

        $helpers = explode(',', $tourney['host_id'] . ',' . $tourney['helpers_id']);
        unset($tourney['helpers_id']);

        foreach ($helpers as $helper) {
            $staff[] = $this->User->field('username', array('User.id' => $helper));
        }

        return $staff;
    }

    // Go to the next stage of the tournament.
    public function next()
    {
        $currentTournament = $this->currentTournament();

        switch ($currentTournament['Tournament']['status']) {
            case Tournament::PENDING:
                if ($this->afterPending($currentTournament)) {
                    return true;
                } else {
                    return false;
                }
                break;
            case Tournament::GROUP:
                if ($this->afterGroup()) {
                    return true;
                } else {
                    return false;
                }
                break;
            case Tournament::PLAYOFF:
                $this->afterPlayoff($currentTournament);
                return true;
                break;
        }
    }

    public function afterPending($currentTournament)
    {
        $this->bindModel(array('hasMany' => array('Group' => array('className' => 'Group'))));

        // Checking if groups have already been created.
        $groupsCreated = (bool)$this->Group->find('count', array(
            'conditions' => array(
                'tournament_id' => $currentTournament['Tournament']['id']
            )
        ));
        if (!$groupsCreated) {
            return false;
        }

        $this->save(array(
            'id' => $currentTournament['Tournament']['id'],
            'status' => Tournament::GROUP
        ));

        $User = ClassRegistry::init('User');
            
        $User->updateAll(array('stage' => "'retired'"));

        $usersInCurrentGroupStage = $this->findUsersInCurrentGroupStage($currentTournament);

        foreach ($usersInCurrentGroupStage as $userId => $username) {
            $User->save(array(
                'id' => $userId,
                'stage' => 'group'
            ));
        }

        return true;
    }

    /**
     * In the current tournament return all the user
     * who are, will be or were in the group stage.
     * 
     * @return array(userId => username)
     */
    public function findUsersInCurrentGroupStage($currentTournament = false) {
        $currentTournament = $currentTournament ? $currentTournament : $this->currentTournament(); 

        // $this->Standing->Group->recursive = 2;
        $groups = ClassRegistry::init('Standing')->Group->find('all', array(
            'conditions' => array(
                'tournament_id' => $currentTournament['Tournament']['id']
            ),
            'recursive' => 2
        ));

        $usersInCurrentGroupStage = array();

        foreach ($groups as $group) {
            for ($i = 0; $i < 4; $i++) {
                $usersInCurrentGroupStage[$group['Standing'][$i]['User']['id']] =
                        $group['Standing'][$i]['User']['username'];
            }
        }

        return $usersInCurrentGroupStage;
    }

    public function afterGroup()
    {
        $Playoff = ClassRegistry::init('Playoff');

        // Playoff tree must've been built before.
        if (!$Playoff->find('count')) {
            return false;
        }

        $Game = ClassRegistry::init('Game');

        // Users who advance will be in playoff stage.
        $assignedOnes = $Game->find('all', array(
            'conditions' => array(
                'Game.playoff_id !=' => 0,
                'Game.group_id' => 0
            )
        ));

        $User = ClassRegistry::init('User');

        foreach ($assignedOnes as $assignedOne) {
            $User->updateAll(
                array('User.stage' => "'playoff'"), array('User.id' => $assignedOne['Game']['home_id'])
            );
            $User->updateAll(
                array('User.stage' => "'playoff'"), array('User.id' => $assignedOne['Game']['away_id'])
            );
        }

        // Getting the KO'd users of a user.
        $ko = $User->find('all', array(
            'conditions' => array(
                'User.stage' => 'group'
            )
        ));

        // Updatind KO'd users' stages.
        foreach ($ko as $user) {
            $User->save(array(
                'id' => $user['User']['id'],
                'stage' => 'retired'
            ));
        }

        // At last go to the next stage of the tournament.
        $currentTournament = $this->currentTournament();
        $this->id = $currentTournament['Tournament']['id'];
        $this->saveField('status', Tournament::PLAYOFF, true);

        return true;
    }

    private function afterPlayoff($currentTournament)
    {
        $this->id = $currentTournament['Tournament']['id'];

        $this->save(array(
            'status' => Tournament::ARCHIVED
        ));

        // Execute the shell that updates all users' timelines.
        App::uses('TimelineShell', 'Console/Command');
        $timelineShell = new TimelineShell();
        $timelineShell->main();

        ClassRegistry::init('Application')->query('TRUNCATE TABLE `applications`');
        return true;
    }

}
