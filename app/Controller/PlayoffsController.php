<?php
App::uses('AppController', 'Controller');

class PlayoffsController extends AppController {
	public $name = 'Playoffs';
    public $scaffold = 'admin';
	
	public function beforeFilter() {
		parent::beforeFilter();

		$tourney = $this->Tournament->info();

		if($this->Auth->user('admin')
		&& $this->request->params['action'] == 'admin_add') {
			$this->Session->setFlash(
  				'Please build the playoff tree.');
		} else {
			if($tourney['status'] != 'playoff') {
	   			$this->Session->setFlash(
	    			'The playoff stage has not yet started.',
	     			'default', array('class' => 'error'));
	     		$this->redirect($this->referer());
	     	}
		}
	}

	public function index($bet = false) {
		$this->Playoff->unbindModel(array('hasMany' => array('Tournament')));
		$this->loadModel('Rating');
		$this->loadModel('Trace');

		// User has bet on a game.
		if($bet) {
			$bet = ($this->request->data['h_or_a'] == 'home') ? 'bet_h' : 'bet_a';

			if($this->Trace->check('Bet', 'add', $bet, $this->request->data['game_id'], 'read') != null) {
				return false; // Already placed bet.
			}

			$game = $this->Playoff->find('first', array(
				'conditions' => array(
					'Playoff.game_id' => $this->request->data['game_id']
				)
			));

			$calcBets = $game['Playoff'][$bet] + 1;

			$this->Playoff->save(array(
				'id' => $game['Playoff']['id'],
				$bet => $calcBets
			));

			$this->Trace->check('Bet', 'add', $bet, $this->request->data['game_id'], 'write');

			unset($game);
		}
		
		$playoff = $this->Playoff->Game->find('all', array(
			'conditions' => array(
				'Game.playoff_id !=' => 0,
				'Game.group_id' => 0
			)
		));

		foreach($playoff as $key => $val) {
			$needle = array(
				'step' => $val['Playoff']['step'],
				'spot' => $val['Playoff']['spot']
			);

			$newKey = array_search($needle, $this->Playoff->keyAssoc);
			$game[$newKey] = $val;
		}		

		$playoff = $game;

		foreach($playoff as $key => $val) {
			$playoff[$key]['Rating'][0] = $this->Rating->ratingStats($playoff[$key]['Game']['id']);

			$bet_h = $this->Trace->check('Bet', 'add', 'bet_h', $playoff[$key]['Playoff']['game_id'], 'read');
			$bet_a = $this->Trace->check('Bet', 'add', 'bet_a', $playoff[$key]['Playoff']['game_id'], 'read');

			$playoff[$key]['Playoff']['bet_h_traced'] = ($bet_h == null) ? false : true;
			$playoff[$key]['Playoff']['bet_a_traced'] = ($bet_a == null) ? false : true;

			$playoff[$key]['Playoff']['bets'] = $this->Playoff->betStats($playoff[$key]['Playoff']['game_id']);
		}

		$this->set('playoff', $playoff);
		//debug($playoff);
	}


	public function view($id = null) {
		$this->Playoff->id = $id;
		if (!$this->Playoff->exists()) {
			throw new NotFoundException(__('Invalid playoff'));
		}
		$this->set('playoff', $this->Playoff->read(null, $id));
	}


	public function add() {
		if ($this->request->is('post')) {
			$this->Playoff->create();
			if ($this->Playoff->save($this->request->data)) {
				$this->Session->setFlash(__('The playoff has been saved'));
				$this->redirect(array('action' => 'index'));
			} else {
				$this->Session->setFlash(__('The playoff could not be saved. Please, try again.'));
			}
		}
		$games = $this->Playoff->Game->find('list');
		$this->set(compact('games'));
	}


	public function edit($id = null) {
		$this->Playoff->id = $id;
		if (!$this->Playoff->exists()) {
			throw new NotFoundException(__('Invalid playoff'));
		}
		if ($this->request->is('post') || $this->request->is('put')) {
			if ($this->Playoff->save($this->request->data)) {
				$this->Session->setFlash(__('The playoff has been saved'));
				$this->redirect(array('action' => 'index'));
			} else {
				$this->Session->setFlash(__('The playoff could not be saved. Please, try again.'));
			}
		} else {
			$this->request->data = $this->Playoff->read(null, $id);
		}
		$games = $this->Playoff->Game->find('list');
		$this->set(compact('games'));
	}


	public function delete($id = null) {
		if (!$this->request->is('post')) {
			throw new MethodNotAllowedException();
		}
		$this->Playoff->id = $id;
		if (!$this->Playoff->exists()) {
			throw new NotFoundException(__('Invalid playoff'));
		}
		if ($this->Playoff->delete()) {
			$this->Session->setFlash(__('Playoff deleted'));
			$this->redirect(array('action' => 'index'));
		}
		$this->Session->setFlash(__('Playoff was not deleted'));
		$this->redirect(array('action' => 'index'));
	}


	public function admin_index() {
		$this->Playoff->recursive = 0;
		$this->set('playoffs', $this->paginate());
	}


	public function admin_view($id = null) {
		$this->Playoff->id = $id;
		if (!$this->Playoff->exists()) {
			throw new NotFoundException(__('Invalid playoff'));
		}
		$this->set('playoff', $this->Playoff->read(null, $id));
	}


	// Building the playoff tree.
	public function admin_add() {
		if($this->request->is('post')) {
			$this->Playoff->create();

			if($this->Playoff->start($this->request->data['Playoff'])) {
				$this->Session->setFlash('The playoff tree has been built');
				$this->redirect(array('action' => 'index'));
			} else {
				$this->Session->setFlash('At least one user has been assigned multiple times.', 'default', array('class' => 'error'));
			}
		}

		$players = $this->Playoff->attendees();
		$this->set('players', $players);
	}


	public function admin_edit($id = null) {
		$this->Playoff->id = $id;
		if (!$this->Playoff->exists()) {
			throw new NotFoundException(__('Invalid playoff'));
		}
		if ($this->request->is('post') || $this->request->is('put')) {
			if ($this->Playoff->save($this->request->data)) {
				$this->Session->setFlash(__('The playoff has been saved'));
				$this->redirect(array('action' => 'index'));
			} else {
				$this->Session->setFlash(__('The playoff could not be saved. Please, try again.'));
			}
		} else {
			$this->request->data = $this->Playoff->read(null, $id);
		}
		$games = $this->Playoff->Game->find('list');
		$this->set(compact('games'));
	}


	public function admin_delete($id = null) {
		if (!$this->request->is('post')) {
			throw new MethodNotAllowedException();
		}
		$this->Playoff->id = $id;
		if (!$this->Playoff->exists()) {
			throw new NotFoundException(__('Invalid playoff'));
		}
		if ($this->Playoff->delete()) {
			$this->Session->setFlash(__('Playoff deleted'));
			$this->redirect(array('action' => 'index'));
		}
		$this->Session->setFlash(__('Playoff was not deleted'));
		$this->redirect(array('action' => 'index'));
	}
}