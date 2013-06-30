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
        	if(!$this->Group->gamesCanBeReported()) {
                $this->Session->setFlash(
                    'The tournament has not yet started.',
                    'default', array('class' => 'error'));
            	$this->redirect($this->referer());
	        }
        }
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
        if($this->Group->numberOfApplicants() < Tournament::PARTICIPANTS) {
			$this->Session->setFlash(
				'There have to be at least 32 applicants to create the groups.',
				'default', array('class' => 'error'));
			$this->redirect($this->referer());
		}

        if($this->request->is('post')) {
			if($this->Group->start($this->request->data['Group'])) {
				$this->Session->setFlash('Groups successfully drawn, now enter the group stage.');
				$this->redirect('/admin/tournaments/edit');
			} else {
				$this->Session->setFlash('At least one user has been assigned multiple times.',
				'default', array('class' => 'error'));
			}
		}

		$users = $this->Group->User->Application->find('all');
        $applicants = array();

        foreach ($users as $user) {
            $applicants[$user['User']['id']] = $user['User']['username'];
        }

		uasort($applicants, 'strcasecmp');
		$this->set('users', $applicants);
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
