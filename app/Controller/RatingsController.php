<?php
App::uses('AppController', 'Controller');

class RatingsController extends AppController {
	public $name = 'Ratings';
	public $scaffold = 'admin';


	public function index() {
		$this->Rating->recursive = 0;
		$this->set('Ratings', $this->paginate());
	}


	public function view($game_id) {
		$rating = $this->Rating->ratingStats($game_id);

		$this->loadModel('Trace');
		$possibleTraces = array('likes', 'dislikes', 'lightside', 'darkside');

		foreach($possibleTraces as $possibleTrace) {
			if($this->Trace->check('Rating', 'add', $possibleTrace, $game_id, 'read') != null) {
				$rating['trace'][$possibleTrace] = true;
			} else {
				$rating['trace'][$possibleTrace] = false;
			}
		}

		if($this->request->is('requested')) {
		    return $rating;
		} else {
		    $this->set('rating', $rating);
		}
		
		$this->set('rating', $rating);
		$this->set('gameId', $game_id);
	}


	public function add() {
		if($this->request->is('post')) {			
			$this->loadModel('Trace');

			switch($this->request->data['rating']) {
				case 'like': $do = 'likes'; break;
				case 'dislike': $do = 'dislikes'; break;
				case 'lightside': $do = 'lightside'; break;
				case 'darkside': $do = 'darkside';
			}

			if($this->Trace->check('Rating', 'add', $do, $this->request->data['gameId'], 'read') != null) {
				return false; // Has already rated the game.
			}

			$exists = $this->Rating->find('first', array(
				'conditions' => array(
					'Rating.game_id' => $this->request->data['gameId']
				)
			));

			if(!$exists || $exists == null) {
				$this->Rating->save(array(
					$do => 1,
					'game_id' => $this->request->data['gameId']
				));
			} else {
				$this->Rating->id = $exists['Rating']['id'];
				$this->Rating->save(array(
					$do => $this->Rating->field($do) + 1,
				));
			}

			// Tournament News!
			if($do == 'likes' || $do == 'dislikes') {
				$this->loadModel('Infoboard');
				$this->Infoboard->tournamentNews(
					$this->request->data['gameId'], $do);
			}
		
			$this->Trace->check('Rating', 'add', $do, $this->request->data['gameId'], 'write');
		}
	}


	public function edit($id = null) {
		$this->Rating->id = $id;
		if (!$this->Rating->exists()) {
			throw new NotFoundException(__('Invalid Rating'));
		}
		if ($this->request->is('post') || $this->request->is('put')) {
			if ($this->Rating->save($this->request->data)) {
				$this->Session->setFlash(__('The Rating has been saved'));
				$this->redirect(array('action' => 'index'));
			} else {
				$this->Session->setFlash(__('The Rating could not be saved. Please, try again.'));
			}
		} else {
			$this->request->data = $this->Rating->read(null, $id);
		}
		$users = $this->Rating->User->find('list');
		$this->set(compact('users'));
	}


	public function delete($id = null) {
		if (!$this->request->is('post')) {
			throw new MethodNotAllowedException();
		}
		$this->Rating->id = $id;
		if (!$this->Rating->exists()) {
			throw new NotFoundException(__('Invalid Rating'));
		}
		if ($this->Rating->delete()) {
			$this->Session->setFlash(__('Rating deleted'));
			$this->redirect(array('action' => 'index'));
		}
		$this->Session->setFlash(__('Rating was not deleted'));
		$this->redirect(array('action' => 'index'));
	}


	public function admin_index() {
		$this->Rating->recursive = 0;
		$this->set('Ratings', $this->paginate());
	}


	public function admin_view($id = null) {
		$this->Rating->id = $id;
		if (!$this->Rating->exists()) {
			throw new NotFoundException(__('Invalid Rating'));
		}
		$this->set('Rating', $this->Rating->read(null, $id));
	}


	public function admin_add() {
		if ($this->request->is('post')) {
			$this->Rating->create();
			if ($this->Rating->save($this->request->data)) {
				$this->Session->setFlash(__('The Rating has been saved'));
				$this->redirect(array('action' => 'index'));
			} else {
				$this->Session->setFlash(__('The Rating could not be saved. Please, try again.'));
			}
		}
		$users = $this->Rating->User->find('list');
		$this->set(compact('users'));
	}


	public function admin_edit($id = null) {
		$this->Rating->id = $id;
		if (!$this->Rating->exists()) {
			throw new NotFoundException(__('Invalid Rating'));
		}
		if ($this->request->is('post') || $this->request->is('put')) {
			if ($this->Rating->save($this->request->data)) {
				$this->Session->setFlash(__('The Rating has been saved'));
				$this->redirect(array('action' => 'index'));
			} else {
				$this->Session->setFlash(__('The Rating could not be saved. Please, try again.'));
			}
		} else {
			$this->request->data = $this->Rating->read(null, $id);
		}
		$users = $this->Rating->User->find('list');
		$this->set(compact('users'));
	}


	public function admin_delete($id = null) {
		if (!$this->request->is('post')) {
			throw new MethodNotAllowedException();
		}
		$this->Rating->id = $id;
		if (!$this->Rating->exists()) {
			throw new NotFoundException(__('Invalid Rating'));
		}
		if ($this->Rating->delete()) {
			$this->Session->setFlash(__('Rating deleted'));
			$this->redirect(array('action' => 'index'));
		}
		$this->Session->setFlash(__('Rating was not deleted'));
		$this->redirect(array('action' => 'index'));
	}
}