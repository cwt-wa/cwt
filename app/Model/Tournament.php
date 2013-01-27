<?php
App::uses('AppModel', 'Model');

class Tournament extends AppModel {
	public $name = 'Tournament';
	public $displayField = 'year';

	public $belongsTo = array(
		'User' => array(
			'className'  => 'User',
			'foreignKey' => 'host_id'
		),
		'Helpers' => array(
			'className'  => 'User',
			'foreignKey' => 'helpers_id'
		),
		'Gold' => array(
			'className'  => 'User',
			'foreignKey' => 'gold_id'
		),
		'Silver' => array(
			'className'  => 'User',
			'foreignKey' => 'silver_id'
		),
		'Bronze' => array(
			'className'  => 'User',
			'foreignKey' => 'bronze_id'
		)
	);
	
	public $hasMany = array(
		'Restore' => array(
			'className'  => 'Restore',
			'foreignKey' => 'tournament_id'
		)
	);

	// Returns whole row of the most recent tourney.
    public function info() {    
        $row = $this->find('first', array(
            'limit' => 1,
            'order' => array('Tournament.id' => 'desc')
        ));
        return $row['Tournament'];
    }

    /* Checks if a user is still in the tournament.
    public function active($user_id = AuthComponent::user('id')) {
    	$tourney = $this->info();

    	if($user_id != AuthComponent::user('id')) {
    		$user = $this->User->read(null, $user_id);
    		$user = $user['User'];
    	} else {
    		$user = AuthComponent::user();
    	}

    	if($tourney['status'] == $user['stage'])
    }*/

	// Start a whole new tournament.
	public function start($data) {
		$helpers = array();
		for($i = 1; $i <= $data['Number']; $i++) {
			if(!empty($data['Helper' . $i])) {
				if(in_array($data['Helper' . $i], $helpers)) {
					return false;
				}
				$helpers[] = $data['Helper' . $i];
			}
		}		

		$helpers_id = '';
		foreach($helpers as $helper) {
			$helpers_id .= $helper . ',';
		}

		$helpers_id = substr($helpers_id, 0, strlen($helpers_id) - 1);

		unset($this->id);
		$this->save(array(
			'year' 	  	 => gmdate('Y'),
			'status' 	 => 'pending',
			'host_id' 	 => AuthComponent::user('id'),
			'helpers_id' => $helpers_id
		));

		return true;
	}

	// Listing the organizers team in the footer.
	public function niceStaff() {
		$tourney = $this->info();

		$helpers = explode(',',  $tourney['host_id'] . ',' . $tourney['helpers_id']);
        unset($tourney['helpers_id']);

        foreach($helpers as $helper) {
            $staff[] = $this->User->field('username', array('User.id' => $helper));
        }

        return $staff;
	}

	// Go to the next stage of the tournament.
	public function next() {
		$tourney = $this->info();

		switch($tourney['status']) {
			case 'pending':
				if($this->afterPending()) {return true;}
				else {return false;}
			break;
			case 'group':
				if($this->afterGroup()) {return true;}
				else {return false;} 
			break;
			case 'playoff':
				if($this->afterPlayoff()) {return true;}
				else {return false;}
			break;
			case 'finished':
				if($this->afterFinished()) {return true;}
				else {return false;}
		}
	}

	public function afterPending() {
		$this->bindModel(array('hasMany' => array('Group' => array('className' => 'Group'))));
		
		// Checking if groups have already been created.
		if(!$this->Group->find('count')) {
			return false;
		}

		// All the applicants who were moved to one group are now in group stage.
		$assignedOnes = $this->Group->find('all');
		foreach($assignedOnes as $assignedOne) {
			$this->User->updateAll(
				array('User.stage' => "'group'"),
				array('User.stage' => 'applied', 'User.id' => $assignedOne['Group']['user_id'])
			);	
		}
		
		// All the applicants who were not moved to one group are retired again.
		$this->User->updateAll(
			array('User.stage' => "'retired'"),
			array('User.stage' => 'applied')
		);

		// At last go to the next stage of the tournament.
		$this->id = $this->field('id', null, 'year DESC');
		$this->saveField('status', 'group', true);

		return true;
	} 

	public function afterGroup() {
		$this->bindModel(
			array('hasMany' => array('Playoff' => array('className' => 'Playoff'))));
		$this->bindModel(
			array('hasMany' => array('Game' => array('className' => 'Game'))));

		// Playoff tree must've been built before.
		if(!$this->Playoff->find('count')) {
			return false;
		}
		
		// Users who advance will be in playoff stage.
		$assignedOnes = $this->Game->find('all', array(
			'conditions' => array(
				'Game.playoff_id !=' => 0,
				'Game.group_id' => 0
			)
		));
		foreach($assignedOnes as $assignedOne) {
			$this->User->updateAll(
				array('User.stage' => "'playoff'"),
				array('User.id' => $assignedOne['Game']['home_id'])
			);	
			$this->User->updateAll(
				array('User.stage' => "'playoff'"),
				array('User.id' => $assignedOne['Game']['away_id'])
			);
		}

		// Getting the KO'd users of a user.
		$ko = $this->User->find('all', array(
			'conditions' => array(
				'User.stage' => 'group'
			)
		));
		
		// Updatind KO'd users' timelines and stages.
		foreach($ko as $user) {
			$this->User->save(array(
				'id' => $user['User']['id'],
				'timeline' => $user['User']['timeline'] . '1',
				'stage' => 'retired'
			));
		}

		// At last go to the next stage of the tournament.
		$this->id = $this->field('id', null, 'year DESC');
		$this->saveField('status', 'playoff', true);

		return true;
	}

	public function afterPlayoff() {
		
	}

	// This is actually archiving the tournament.
	public function afterFinished() {
		// Geht HTML of a page.

		// Either that works
		//$handle = @fopen("'http://www.webmasterworld.com", "rt");
		//$source_code = fread($handle,9000);

		// or that works.
		//$url = 'http://www.example.com.somepage.php';
		//$contents = file_get_contents($url);
	}
}
