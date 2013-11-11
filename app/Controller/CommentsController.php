<?php
App::uses('AppController', 'Controller');

class CommentsController extends AppController {
	public $name = 'Comments';
	public $scaffold = 'admin';


	public function index() {
		$this->Comment->recursive = 0;
		$this->set('comments', $this->paginate());
	}


	public function view($game_id) {
		$this->helpers[] = 'Time'; $this->helpers[] = 'Bbcode';

		$this->Comment->recursive = 0;
		$comments = $this->Comment->find('all', array(
			'conditions' => array(
				'Comment.game_id' => $game_id
			), 'order' => 'Comment.created DESC'
		));
		if($this->request->is('requested')) {
		    return $comments;
		} else {
		    $this->set('comments', $comments);
		}

		$this->set('comments', $comments);
	}


	public function add($game_id) {
		if(!$this->Auth->loggedIn()) {
			$this->Auth->flash($this->Auth->authError);
			$this->redirect($this->referer());
		}

		if($this->request->is('post')) {
			$this->Comment->create();

			$update = $this->Comment->find('first', array(
				'conditions' => array(
					'Comment.game_id' => $game_id,
					'Comment.user_id' => $this->Auth->user('id'),
					'Comment.message' => ''
				)
			));

			$this->Comment->id = (bool)$update ? $update['Comment']['id'] : false;

			if($this->request->data['action'] == 'preview') {
				$preview = $this->Comment->field('preview', array(
					'Comment.game_id' => $game_id,
					'Comment.user_id' => $this->Auth->user('id'),
					'Comment.message' => ''
				));
		        if($this->request->is('requested')) {
		            return $preview;
		        } else {
		            $this->set('preview', $preview);
		            $this->set('destination', '/comments/add/' . $game_id);
		        }


				$this->Comment->save(array(
					'game_id'  => $game_id,
					'user_id'  => $this->Auth->user('id'),
					'preview'  => $this->request->data['text'],
					'modified' => '0000-00-00 00:00:00'
				));

				$this->render('/Elements/bbcodepreview');
			} elseif($this->request->data['action'] == 'submit') {
				$save = array(
					'game_id'  => $game_id,
					'message'  => $this->request->data['text'],
					'user_id'  => $this->Auth->user('id'),
					'preview'  => '',
					'modified' => '0000-00-00 00:00:00'
				);

				if($this->Comment->save($save)) {
					$this->loadModel('Infoboard');
					$this->Infoboard->tournamentNews(
						$game_id, 'commented');
				}
			}
		}

		$this->Comment->Game->recursive = 0;
		$comment = $this->Comment->Game->read(null, $game_id);

		if($comment['Game']['group_id']) {
			$comment['stage'] = 'Group ' . $comment['Group']['label'];
		} else if ($comment['Game']['playoff_id']) {
            $comment['stage'] = $comment['Playoff']['stepAssoc'];
        }

		//debug($comment);
		$this->set('comment', $comment);
	}


	public function edit($id = null) {
		$this->Comment->id = $id;
		if (!$this->Comment->exists()) {
			throw new NotFoundException(__('Invalid comment'));
		}

		$comment = $this->Comment->find('first', array(
			'conditions' => array(
				'Comment.id' => $id
			)
		));

		if($comment['Comment']['user_id'] != $this->Auth->user('id')) {
			$this->Auth->flash('You can\'t edit someone else\'s comment.');
			$this->redirect($this->referer());
		}

		if($this->request->is('post')) {
			$this->Comment->create();
			$this->Comment->id = $id;

			if($this->request->data['action'] == 'preview') {
				$preview = $this->Comment->field('preview');
		        if($this->request->is('requested')) {
		            return $preview;
		        } else {
		            $this->set('preview', $preview);
		            $this->set('destination', '/comments/edit/' . $id);
		        }

				$this->Comment->save(array(
					'preview'  => $this->request->data['text'],
					'modified' => '0000-00-00 00:00:00'
				));

				$this->render('/Elements/bbcodepreview');
			} elseif($this->request->data['action'] == 'submit') {
				if($this->request->data['text'] == ''
				|| $this->request->data['text'] == null) {
					$this->Comment->delete($id, false);
				} else {
					$this->Comment->save(array(
						'message'  => $this->request->data['text'],
						'preview'  => ''
					));
				}
			}
		}

		$game = $this->Comment->Game->read(null, $comment['Game']['id']);

		if($game['Game']['group_id']) {
			$game['stage'] = 'Group ' . $game['Group']['group'];
		}

		$this->set('comment', $comment);
		$this->set('game', $game);
	}


	public function delete($id = null) {
		if (!$this->request->is('post')) {
			throw new MethodNotAllowedException();
		}
		$this->Comment->id = $id;
		if (!$this->Comment->exists()) {
			throw new NotFoundException(__('Invalid comment'));
		}
		if ($this->Comment->delete()) {
			$this->Session->setFlash(__('Comment deleted'));
			$this->redirect(array('action' => 'index'));
		}
		$this->Session->setFlash(__('Comment was not deleted'));
		$this->redirect(array('action' => 'index'));
	}


	public function admin_index() {
		$this->Comment->recursive = 0;
		$this->set('comments', $this->paginate());
	}


	public function admin_view($id = null) {
		$this->Comment->id = $id;
		if (!$this->Comment->exists()) {
			throw new NotFoundException(__('Invalid comment'));
		}
		$this->set('comment', $this->Comment->read(null, $id));
	}


	public function admin_add() {
		if ($this->request->is('post')) {
			$this->Comment->create();
			if ($this->Comment->save($this->request->data)) {
				$this->Session->setFlash(__('The comment has been saved'));
				$this->redirect(array('action' => 'index'));
			} else {
				$this->Session->setFlash(__('The comment could not be saved. Please, try again.'));
			}
		}
		$games = $this->Comment->Game->find('list');
		$users = $this->Comment->User->find('list');
		$this->set(compact('games', 'users'));
	}


	public function admin_edit($id = null) {
		$this->Comment->id = $id;
		if (!$this->Comment->exists()) {
			throw new NotFoundException(__('Invalid comment'));
		}
		if ($this->request->is('post') || $this->request->is('put')) {
			if ($this->Comment->save($this->request->data)) {
				$this->Session->setFlash(__('The comment has been saved'));
				$this->redirect(array('action' => 'index'));
			} else {
				$this->Session->setFlash(__('The comment could not be saved. Please, try again.'));
			}
		} else {
			$this->request->data = $this->Comment->read(null, $id);
		}
		$games = $this->Comment->Game->find('list');
		$users = $this->Comment->User->find('list');
		$this->set(compact('games', 'users'));
	}


	public function admin_delete($id = null) {
		if (!$this->request->is('post')) {
			throw new MethodNotAllowedException();
		}
		$this->Comment->id = $id;
		if (!$this->Comment->exists()) {
			throw new NotFoundException(__('Invalid comment'));
		}
		if ($this->Comment->delete()) {
			$this->Session->setFlash(__('Comment deleted'));
			$this->redirect(array('action' => 'index'));
		}
		$this->Session->setFlash(__('Comment was not deleted'));
		$this->redirect(array('action' => 'index'));
	}
}
