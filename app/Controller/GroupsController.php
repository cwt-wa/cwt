<?php
App::uses('AppController', 'Controller');

class GroupsController extends AppController {
	public $name = 'Groups';
    public $scaffold = 'admin';

    public function beforeFilter() {
    	parent::beforeFilter();

    	// Only permit access when tournament is running.
    	// Status: group, playoff, finished
    	if(in_array($this->action,
        array('edit', 'delete', 'add', 'index', 'view'))) {
        	if(!$this->Group->tourneyStarted()) {
            	$this->Session->setFlash('The tournament has not yet started.', 'default', array('class' => 'error'));
            	$this->redirect($this->referer());
	        }
        }
    }

    // Picking 31 random players and Zemke and apply them to the tourney.
    public function temporary1() {
    	$this->loadModel('Application');
    	$this->loadModel('User');

    	$this->Application->save(array(
    		'user_id' => '1'
    	));

    	$this->User->save(array(
    		'id' => '1',
    		'stage' => 'applied'
    	));

    	$applicants[0] = '1';

    	for($i = 1; $i <= 31; $i++) {
    		do {
    			$applicant = rand(1, $this->User->find('count'));
    		} while(in_array($applicant, $applicants));

    		$applicants[$i] = $applicant;

    		unset($this->Application->id);
    		$this->Application->save(array(
    			'user_id' => $applicant
    		));

    		$this->User->save(array(
	    		'id' => $applicant,
	    		'stage' => 'applied'
	    	));
    	}
    }

    // Resetting the applications.
    public function temporary2() {
    	$this->loadModel('Application');
    	$this->loadModel('User');

    	$this->User->updateAll(
		    array('User.stage' => "'retired'"),
		    array('User.stage' => 'applied')
		);

		$this->Application->query("TRUNCATE TABLE `applications`");
    }

    public function temporary() {
    	$this->loadModel('Game');
    	$replays = @$this->request->data['Report']['replays'];
    	$groupAll = $this->Group->find('all');
    	$groupNum = array('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H');
    	$posssibleResult = array('3-0', '3-1', '3-2', '0-3', '1-3', '2-3');

	   	$cGroupNum = 0; $cSpot = 0;
	   	foreach($groupAll as $user) {
	   		$group[$groupNum[$cGroupNum]][$cSpot] = $user['User']['id'];

	   		$cSpot++;

	   		if($cSpot % 4 == 0) {
	   			$cGroupNum++;
	   			$cSpot = 0;
	   		}
    	}

    	for($i = 0; $i <= 7; $i++) {
    		$cGame = 1;
    		for($i2 = 0; $i2 <= 2; $i2++) {
    			$i3 = $i2;
    			while($i3 < 3) {
    				$i3 += 1;
    				$rndResult = $posssibleResult[rand(0, 5)];

	    			$data[$groupNum[$i]][$cGame]['user'] = $group[$groupNum[$i]][$i2];
	    			$data[$groupNum[$i]][$cGame]['userScore'] = substr($rndResult, 0, 1);
	    			$data[$groupNum[$i]][$cGame]['opponentScore'] = substr($rndResult, -1);
	    			$data[$groupNum[$i]][$cGame]['opponent'] = $group[$groupNum[$i]][$i3];

	    			if($this->request->is('post')) {
	    				$this->Auth->logout();
				    	$user = $this->Group->User->read(null, $group[$groupNum[$i]][$i2]);
					    $this->Auth->login($user['User']);
					    
	    				$data[$groupNum[$i]][$cGame]['replays'] = $replays;
	    				$this->Game->report($data[$groupNum[$i]][$cGame]);
	    			}

	    			$cGame++;
    			}
    			unset($i3); 
    		}
    	}

    	debug($data); return;
    }


	public function index() {
		$this->Group->unbindModel(array('hasMany' => array('Tournament')));
		$this->loadModel('Rating');
		$groupArray = array('*', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H');

		$games = $this->Group->Game->find('all', array(
			'order' => 'Game.created DESC'
		));

		for($i = 1; $i <= 8; $i++) {
			$group[$i]['group'] = $groupArray[$i];

			$groupAll = $this->Group->find('all', array(
				'conditions' => array(
					'Group.group' => $groupArray[$i]
				),
				'order' => 'Group.points DESC, Group.game_ratio DESC, Group.round_ratio DESC'
			));

			for($i2 = 0; $i2 <= 3; $i2++) {
				$group[$i][$i2 + 1] = array(
					'User' => $groupAll[$i2]['User'],
					'Group' => $groupAll[$i2]['Group']
				);
				$group[$i][$i2 + 1]['User']['flag'] = 'flags/' . str_replace(' ', '_', strtolower($this->Group->User->Profile->field('country', array('user_id' => $groupAll[$i2]['User']['id'])))) . '.png';
			}

			$cGames = 1;
			foreach($games as $game) {
				if(in_array($game['Game']['group_id'], $this->Group->groupAssoc[$groupArray[$i]])) {
					$group[$i]['Game'][$cGames] = $game;
					$group[$i]['Game'][$cGames]['Rating'][0] = $this->Rating->ratingStats($game['Game']['id']);
					$cGames++;
				}
			}
		}

		$this->set('group', $group); //debug($group);
	}


	public function view($id = null) {
		$this->Group->id = $id;
		if (!$this->Group->exists()) {
			throw new NotFoundException(__('Invalid group'));
		}
		$this->set('group', $this->Group->read(null, $id));
	}


	public function add() {
		if ($this->request->is('post')) {
			$this->Group->create();
			if ($this->Group->save($this->request->data)) {
				$this->Session->setFlash(__('The group has been saved'));
				$this->redirect(array('action' => 'index'));
			} else {
				$this->Session->setFlash(__('The group could not be saved. Please, try again.'));
			}
		}
		$users = $this->Group->User->find('list');
		$this->set(compact('users'));
	}


	public function edit($id = null) {
		$this->Group->id = $id;
		if (!$this->Group->exists()) {
			throw new NotFoundException(__('Invalid group'));
		}
		if ($this->request->is('post') || $this->request->is('put')) {
			if ($this->Group->save($this->request->data)) {
				$this->Session->setFlash(__('The group has been saved'));
				$this->redirect(array('action' => 'index'));
			} else {
				$this->Session->setFlash(__('The group could not be saved. Please, try again.'));
			}
		} else {
			$this->request->data = $this->Group->read(null, $id);
		}
	}


	public function delete($id = null) {
		if (!$this->request->is('post')) {
			throw new MethodNotAllowedException();
		}
		$this->Group->id = $id;
		if (!$this->Group->exists()) {
			throw new NotFoundException(__('Invalid group'));
		}
		if ($this->Group->delete()) {
			$this->Session->setFlash(__('Group deleted'));
			$this->redirect(array('action'=>'index'));
		}
		$this->Session->setFlash(__('Group was not deleted'));
		$this->redirect(array('action' => 'index'));
	}


	public function admin_index() {
		$this->Group->recursive = 0;
		$this->set('groups', $this->paginate());
	}


	public function admin_view($id = null) {
		$this->Group->id = $id;
		if (!$this->Group->exists()) {
			throw new NotFoundException(__('Invalid group'));
		}
		$this->set('group', $this->Group->read(null, $id));
	}


 	public function admin_add() {
        if(!$this->Group->applicants()) {
			$this->Session->setFlash(
				'There have to be at least 32 applicants to create the groups.',
				'default', array('class' => 'error'));
			$this->redirect($this->referer());
		}

        if($this->request->is('post')) {
			if($this->Group->start($this->request->data['Group'])) {
				$this->Session->setFlash('The groups have been created.');
				$this->redirect('/admin/tournaments/edit');
			} else {
				$this->Session->setFlash('At least one user has been assigned multiple times.',
				'default', array('class' => 'error'));
			}
		}

		$users = $this->Group->User->find('list', array(
			'conditions' => array(
				'stage' => 'applied'
			), 
			'fields' => array(
				'User.username'
			),
			'order' => 'User.username ASC'
		));
		uasort($users, 'strcasecmp');
		$this->set(compact('users'));
	}


	// Replacing a player from the group stage.
	public function admin_edit() {
		if($this->request->is('post')) {
			$this->Group->replacePlayer($this->request->data);
			$this->Auth->login($this->User->re_login());
			$this->Session->setFlash(
				'The players have been exchanged successfully.');
			$this->redirect('/groups');
		}

		// Waiting players:
		$active = $this->User->find('list', array(
			'conditions' => array(
				'User.stage !=' => 'group'
			), 'order' => 'User.username ASC'
		));

		// Players in the group stage.
		$inactive = $this->User->find('list', array(
			'conditions' => array(
				'User.stage' => 'group'
			), 'order' => 'User.username ASC'
		));

		$this->set(compact('active'));
		$this->set(compact('inactive'));
	}


	public function admin_delete($id = null) {
		if (!$this->request->is('post')) {
			throw new MethodNotAllowedException();
		}
		$this->Group->id = $id;
		if (!$this->Group->exists()) {
			throw new NotFoundException(__('Invalid group'));
		}
		if ($this->Group->delete()) {
			$this->Session->setFlash(__('Group deleted'));
			$this->redirect(array('action'=>'index'));
		}
		$this->Session->setFlash(__('Group was not deleted'));
		$this->redirect(array('action' => 'index'));
	}
}
