<?php

class SchedulesController extends AppController {
	public $name = 'Schedules';
	public $scaffold = 'admin';


	public function beforeFilter() {
		parent::beforeFilter();
		$this->Auth->allow('delete');
	}


	public function index() {
		$this->loadModel('Tournament');
        $this->loadModel('Game');
        $this->loadModel('Group');
        $this->loadModel('Playoff');
        $this->loadModel('Stream');
		
		$schedules = $this->Schedule->find('all', array(
			'order' => 'Schedule.when ASC'));

        $schedules = $this->Schedule->scheduledStreams($schedules);
        $schedules = $this->Schedule->filterSchedule($schedules);

        /*
         * Getting the opponents the current user can play against.
         */

		$tourney = $this->Tournament->info();

		if($tourney['status'] == 'group') {
			$opps = $this->Group->attendees();
			$opps = $this->Group->allowedOpponents($opps);
		} elseif($tourney['status'] == 'playoff') {
			$currentGame = $this->Playoff->currentGame();

			if(!$currentGame) {
				$opps = null;
			} else {
				$opps = $this->Playoff->attendees();
				$opps = $this->Playoff->allowedOpponents($opps);

				if($currentGame['Playoff']['step'] > 3)
					$allowedResults['4'] = '4';
			}
		}

		$schedules['opponents'] = !@array_key_exists(0, $opps) ? $opps : false;

		$schedules['datetimes'] = array(
        	'days' => $this->Schedule->daysLeft(),
        	'times' => $this->Schedule->getTimes()
        );

        if($this->request->is('requested')) {
            return $schedules;
        } else {
            $this->set('schedules', $schedules);
        }
	}


	public function view($id = null) {
		$this->Schedule->id = $id;
		if (!$this->Schedule->exists()) {
			throw new NotFoundException(__('Invalid schedule'));
		}
		$this->set('schedule', $this->Schedule->read(null, $id));
	}


	public function add() {
		if ($this->request->isAjax()) {
			$this->Schedule->save($this->request->data);
			$this->render('/Elements/scheduler');
		}
	}

	public function edit($id = null) {
		$this->Schedule->id = $id;
		if (!$this->Schedule->exists()) {
			throw new NotFoundException(__('Invalid schedule'));
		}
		if ($this->request->is('post') || $this->request->is('put')) {
			if ($this->Schedule->save($this->request->data)) {
				$this->Session->setFlash(__('The schedule has been saved'));
				$this->redirect(array('action' => 'index'));
			} else {
				$this->Session->setFlash(__('The schedule could not be saved. Please, try again.'));
			}
		} else {
			$this->request->data = $this->Schedule->read(null, $id);
		}
		$users = $this->Schedule->User->find('list');
		$this->set(compact('users'));
	}


	public function delete($id = null) {
		$game = $this->Schedule->read(null, $id);

		if($game['Schedule']['home_id'] == $this->Auth->user('id')) {
			$this->Schedule->delete($id);
		}

		$this->render('/Elements/scheduler');
	}


	public function admin_index() {
		$this->Schedule->recursive = 0;
		$this->set('schedules', $this->paginate());
	}


	public function admin_view($id = null) {
		$this->Schedule->id = $id;
		if (!$this->Schedule->exists()) {
			throw new NotFoundException(__('Invalid schedule'));
		}
		$this->set('schedule', $this->Schedule->read(null, $id));
	}


	public function admin_add() {
		if ($this->request->is('post')) {
			$this->Schedule->create();
			if ($this->Schedule->save($this->request->data)) {
				$this->Session->setFlash(__('The schedule has been saved'));
				$this->redirect(array('action' => 'index'));
			} else {
				$this->Session->setFlash(__('The schedule could not be saved. Please, try again.'));
			}
		}
		$users = $this->Schedule->User->find('list');
		$this->set(compact('users'));
	}


	public function admin_edit($id = null) {
		$this->Schedule->id = $id;
		if (!$this->Schedule->exists()) {
			throw new NotFoundException(__('Invalid schedule'));
		}
		if ($this->request->is('post') || $this->request->is('put')) {
			if ($this->Schedule->save($this->request->data)) {
				$this->Session->setFlash(__('The schedule has been saved'));
				$this->redirect(array('action' => 'index'));
			} else {
				$this->Session->setFlash(__('The schedule could not be saved. Please, try again.'));
			}
		} else {
			$this->request->data = $this->Schedule->read(null, $id);
		}
		$users = $this->Schedule->User->find('list');
		$this->set(compact('users'));
	}


	public function admin_delete($id = null) {
		if (!$this->request->is('post')) {
			throw new MethodNotAllowedException();
		}
		$this->Schedule->id = $id;
		if (!$this->Schedule->exists()) {
			throw new NotFoundException(__('Invalid schedule'));
		}
		if ($this->Schedule->delete()) {
			$this->Session->setFlash(__('Schedule deleted'));
			$this->redirect(array('action' => 'index'));
		}
		$this->Session->setFlash(__('Schedule was not deleted'));
		$this->redirect(array('action' => 'index'));
	}
}
