<?php
App::uses('AppController', 'Controller');

class InfoboardsController extends AppController {
	public $name = 'Infoboards';
	public $scaffold = 'admin';

	public function beforeFilter() {
		parent::beforeFilter();
		$this->Auth->allow('show', 'submit');
	}


	public function show($category = 1) {
		// Non logged in users may only see the Guestbook.
		if(!$this->Auth->loggedIn()) {
			$category = 4;
		}

		$this->set('category', $category);
		$this->set('messages', $this->Infoboard->messages($category));

		$this->helpers[] = 'Text';
		$this->helpers[] = 'Time';

		/*$messages = $this->Infoboard->messages($category);
        if($this->request->is('requested')) {
            return $messages;
        } else {
            $this->set('messages', $messages);
        }*/
	}


	public function nick_suggest() {
		$str = $this->request->data['str'];
		$count = $this->request->data['count'];
		$this->set('suggestions', $this->Infoboard->nick_suggestions($str, $count));
	}

	public function submit() {
		if(@$this->request->data['guest'] !== 'undefined') {
            // No guest book entries anymore.
            return;
		} else {
			$check = $this->Infoboard->submit($this->request->data['message']);
		}

		if(is_array($check)) {
			$this->set('unknowns', $check);
		}
	}


	public function index() {
		$this->Infoboard->recursive = 0;
		$this->set('infoboard', $this->paginate());
	}


	public function view($id = null) {
		$this->Infoboard->id = $id;
		if (!$this->Infoboard->exists()) {
			throw new NotFoundException(__('Invalid infoboard'));
		}
		$this->set('infoboard', $this->Infoboard->read(null, $id));
	}


	public function add() {
		if ($this->request->is('post')) {
			$this->Infoboard->create();
			if ($this->Infoboard->save($this->request->data)) {
				$this->Session->setFlash(__('The infoboard has been saved'));
				$this->redirect(array('action' => 'index'));
			} else {
				$this->Session->setFlash(__('The infoboard could not be saved. Please, try again.'));
			}
		}
		$users = $this->Infoboard->User->find('list');
		$this->set(compact('users'));
	}


	public function edit($id = null) {
		$this->Infoboard->id = $id;
		if (!$this->Infoboard->exists()) {
			throw new NotFoundException(__('Invalid infoboard'));
		}
		if ($this->request->is('post') || $this->request->is('put')) {
			if ($this->Infoboard->save($this->request->data)) {
				$this->Session->setFlash(__('The infoboard has been saved'));
				$this->redirect(array('action' => 'index'));
			} else {
				$this->Session->setFlash(__('The infoboard could not be saved. Please, try again.'));
			}
		} else {
			$this->request->data = $this->Infoboard->read(null, $id);
		}
		$users = $this->Infoboard->User->find('list');
		$this->set(compact('users'));
	}


	public function delete($id = null) {
		if (!$this->request->is('post')) {
			throw new MethodNotAllowedException();
		}
		$this->Infoboard->id = $id;
		if (!$this->Infoboard->exists()) {
			throw new NotFoundException(__('Invalid infoboard'));
		}
		if ($this->Infoboard->delete()) {
			$this->Session->setFlash(__('Infoboard deleted'));
			$this->redirect(array('action' => 'index'));
		}
		$this->Session->setFlash(__('Infoboard was not deleted'));
		$this->redirect(array('action' => 'index'));
	}


	public function admin_index() {
		$this->Infoboard->recursive = 0;
		$this->set('infoboards', $this->paginate());
	}


	public function admin_view($id = null) {
		$this->Infoboard->id = $id;
		if (!$this->Infoboard->exists()) {
			throw new NotFoundException(__('Invalid infoboard'));
		}
		$this->set('infoboard', $this->Infoboard->read(null, $id));
	}


	public function admin_add() {
		if ($this->request->is('post')) {
			$this->Infoboard->create();
			if ($this->Infoboard->save($this->request->data)) {
				$this->Session->setFlash(__('The infoboard has been saved'));
				$this->redirect(array('action' => 'index'));
			} else {
				$this->Session->setFlash(__('The infoboard could not be saved. Please, try again.'));
			}
		}
		$users = $this->Infoboard->User->find('list');
		$this->set(compact('users'));
	}


	public function admin_edit($id = null) {
		$this->Infoboard->id = $id;
		if (!$this->Infoboard->exists()) {
			throw new NotFoundException(__('Invalid infoboard'));
		}
		if ($this->request->is('post') || $this->request->is('put')) {
			if ($this->Infoboard->save($this->request->data)) {
				$this->Session->setFlash(__('The infoboard has been saved'));
				$this->redirect(array('action' => 'index'));
			} else {
				$this->Session->setFlash(__('The infoboard could not be saved. Please, try again.'));
			}
		} else {
			$this->request->data = $this->Infoboard->read(null, $id);
		}
		$users = $this->Infoboard->User->find('list');
		$this->set(compact('users'));
	}


	public function admin_delete($id = null) {
		if (!$this->request->is('post')) {
			throw new MethodNotAllowedException();
		}
		$this->Infoboard->id = $id;
		if (!$this->Infoboard->exists()) {
			throw new NotFoundException(__('Invalid infoboard'));
		}
		if ($this->Infoboard->delete()) {
			$this->Session->setFlash(__('Infoboard deleted'));
			$this->redirect(array('action' => 'index'));
		}
		$this->Session->setFlash(__('Infoboard was not deleted'));
		$this->redirect(array('action' => 'index'));
	}
}
