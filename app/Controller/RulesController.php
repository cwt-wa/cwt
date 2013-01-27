<?php
App::uses('AppController', 'Controller');

class RulesController extends AppController {
	public $name = 'Rules';
	public $scaffold = 'admin';


	public function index() {
		$this->Rule->recursive = 0;
		$this->set('rules', $this->paginate());
	}


	public function view($apply = false) {
		$this->helpers[] = 'Bbcode';

		$this->loadModel('Tournament');
		$info = $this->Tournament->info();

		if($apply && $this->Auth->loggedIn()
		&& $this->Auth->user('stage') == 'retired'
		&& $info['status'] == 'pending') {
			$this->set('apply', $apply);
		}

		$this->set('rule', $this->Rule->read(null, 1));
	}


	public function admin_index() {
		$this->Rule->recursive = 0;
		$this->set('rules', $this->paginate());
	}


	public function admin_view($id = null) {
		$this->Rule->id = $id;
		if (!$this->Rule->exists()) {
			throw new NotFoundException(__('Invalid rule'));
		}
		$this->set('rule', $this->Rule->read(null, $id));
	}


	public function admin_edit() {
		$preview = $this->Rule->field('preview', 1);
        if ($this->request->is('requested')) {
            return $preview;
        } else {
            $this->set('preview', $preview);
            $this->set('destination', '/admin/rules/edit');
            $this->set('value', $this->Rule->field('text', 1));
        }

		if($this->request->is('post')) {
			if($this->request->data['action'] == 'preview') {
				$this->Rule->save(array(
					'id' => 1,
					'preview' => $this->request->data['text']
				));

				$this->render('/Elements/bbcodepreview');
			} elseif($this->request->data['action'] == 'submit') {
				$this->Rule->save(array(
					'id' => 1,
					'text' => $this->request->data['text'],
					'user_id' => $this->Auth->user('id'),
					'modified' => gmdate('Y-m-d H:i:s'),
					'preview' => ''
				));
			}
		}
	}


	public function admin_add($id = null) {
		$this->Rule->id = $id;
		if (!$this->Rule->exists()) {
			throw new NotFoundException(__('Invalid rule'));
		}
		if ($this->request->is('post') || $this->request->is('put')) {
			if ($this->Rule->save($this->request->data)) {
				$this->Session->setFlash(__('The rule has been saved'));
				$this->redirect(array('action' => 'index'));
			} else {
				$this->Session->setFlash(__('The rule could not be saved. Please, try again.'));
			}
		} else {
			$this->request->data = $this->Rule->read(null, $id);
		}
	}
}