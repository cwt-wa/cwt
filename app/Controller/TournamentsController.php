<?php
App::uses('AppController', 'Controller');

class TournamentsController extends AppController {
	public $name = 'Tournaments';
	public $scaffold = 'admin';
	
	var $helpers = array('AjaxMultiUpload.Upload');
	var $components = array('Session', 'AjaxMultiUpload.Upload');


	public function beforeFilter() {
		parent::beforeFilter();
		$this->Auth->allow('download');
	}
	

	public function admin_index() {
		$this->Tournament->recursive = 0;
		$this->set('tournaments', $this->paginate());
	}


	public function admin_view($id = null) {
		$this->Tournament->id = $id;
		if (!$this->Tournament->exists()) {
			throw new NotFoundException(__('Invalid tournament'));
		}
		$this->set('tournament', $this->Tournament->read(null, $id));
	}


	public function admin_add() {
		$tourney = $this->Tournament->info();
		$this->Tournament->id = $tourney['id'];

		if($tourney['status'] != 'archived') {
			$this->Session->setFlash('There is already a tournament running at the moment.');
			$this->redirect($this->referer());
		}

		if($this->request->is('post')) {
			if($this->Tournament->start($this->request->data['Start'])) {
				$this->Auth->login($this->User->re_login());
				$this->Session->setFlash(
					'The tournament has started and users are now able
					to apply for participation.');
				$this->redirect('/pages/admin');
			} else {
				$this->Session->setFlash(
					'A new tournament has not been started.
					You have entered the same helper twice.',
					'default', array('class' => 'error'));
			}
		}

		$this->Tournament->bindModel(array(
			'hasMany' => array(
				'User' => array(
					'className' => 'User'
				)
			)
		));

		$users = $this->Tournament->User->find('list');
		unset($users[$this->Auth->user('id')]);
		uasort($users, 'strcasecmp');
		$this->set('users', $users);
	}

	public function admin_upload() {
		$tourney = $this->Tournament->info();
		$this->Tournament->id = $tourney['id']; 
		$this->set('tournament', $this->Tournament->read());
	}

	public function download($download) {
		$this->viewClass = 'Media';

		switch($download) {
			case 'replays':
				$this->set(array(
                    'id'        => 'CWT all replays.rar',
		            'name'      => 'CWT all replays',
		            'download'  => true,
		            'extension' => 'rar',
		            'path'      => 'files' . DS . 'downloads' . DS
		        ));
			break;
			case 'scheme':
				$this->set(array(
                    'id'        => 'CWT Intermediate.wsc',
		            'name'      => 'CWT Intermediate',
		            'download'  => true,
		            'extension' => 'wsc',
		            'path'      => 'files' . DS . 'downloads' . DS
		        ));
			break;
			case 'cwt2009':
				$this->set(array(
                    'id'        => 'cwt2009.zip',
		            'name'      => 'cwt2009',
		            'download'  => true,
		            'extension' => 'zip',
		            'path'      => 'files' . DS . 'downloads' . DS
		        ));
			break;
		}
	}


	public function admin_edit() {
		// It's always the msot recent tournament.
		$tourney = $this->Tournament->info();
		$this->Tournament->id = $tourney['id'];

		/* Redirecting is a bit of a problem here.
		 * Depending on the status we have different
		 * error/success messages and redirecting paths.
		 * (Do that step by step.)
		*/

		switch($tourney['status']) { // Next status.
			case 'pending':
				$next = 'Enter the Group stage';
				$Smsg = 'Players who were assigned to groups can now report their group stage games';
				$Emsg = 'You have got to create the groups first.';
				$redirect = '/groups';

				$this->loadModel('Group');

				if(!$this->Group->find('count')) {
					$this->Session->setFlash(
						'You need to assign players to their groups
						 before starting the group stage.',
						'default', array('class' => 'error'));
					$this->redirect('/admin/groups/add');
				}
			break;
			case 'group':
				$next = 'Start the Playoff';
				$Smsg = 'Players who made it into the playoff can now report their playoff games.';
				$redirect = '/playoffs';

				$this->loadModel('Playoff');

				if(!$this->Playoff->find('count')) {
					$this->redirect('/admin/playoffs/add');
				}
			break;
			case 'finished':
				$next = 'Archive the tournament';
				$Smsg = 'The current tournament has now been made available in the Archive.';
				$redirect = '/';
		}

		if($this->request->is('post')) {
			if($this->Tournament->next()) {
				$this->Auth->login($this->User->re_login());
				$this->Session->setFlash($Smsg);
				$this->redirect($redirect);
			} else {
				$this->Session->setFlash(
					$Emsg,
					'default', array('class' => 'error')); //yes
			}
		}

		$this->set('next', $next);
		$this->set('status', $tourney['status']);
	}


	public function admin_delete($id = null) {
		if (!$this->request->is('post')) {
			throw new MethodNotAllowedException();
		}
		$this->Tournament->id = $id;
		if (!$this->Tournament->exists()) {
			throw new NotFoundException(__('Invalid tournament'));
		}
		if ($this->Tournament->delete()) {
			$this->Session->setFlash(__('Tournament deleted'));
			$this->redirect(array('action' => 'index'));
		}
		$this->Session->setFlash(__('Tournament was not deleted'));
		$this->redirect(array('action' => 'index'));
	}
}
