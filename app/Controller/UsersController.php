<?php
App::uses('AppController', 'Controller');

class UsersController extends AppController {
    public $name = 'Users';
    public $scaffold = 'admin';

    public $paginate = array(
    	'limit' => 30,
    	'order' => 'User.participations DESC'
    );
 
    
    public function beforeFilter() {
        parent::beforeFilter();
        $this->Auth->allow('add', 'logout', 'timeline', 'password', 'foreign', 'resetPW');
    }


    public function resetPW($id) {
    	if(!$this->Auth->user('admin')) {
    		$this->Session->setFlash('Admins only!');
    		$this->redirect('/users');
    	} else {
    		$this->User->save(array(
	    		'id' => $id,
	    		'password' => 'NeverForgetItAgain2012'
	    	), false);

	    	$this->Session->setFlash('Hey Joschi, ' . $this->User->field('username') . '\'s Passwort wurde geändert zu “NeverForgetItAgain2012”.');
	    	$this->redirect('/users/view/' . $this->User->id);
    	}
    }


    public function newRows() {
    	$users = $this->User->find('list');

    	foreach($users as $key => $val) {
    		// Timeline update.
    		$timeline = $this->User->timeline($key);
    		$shortened = array_slice($timeline, 0, 10);
    		$final = implode('', $shortened);		
    		debug($final);

    		// Participations update.
    		$participations = 0;
    		foreach($shortened as $tournament) {
    			if($tournament !== '0') $participations++;
    		}
    		debug($participations);

    		// Saving all the stuff.
    		$this->User->save(array(
    			'id' => $key,
    			'timeline' => $final,
    			'participations' => $participations
    		));
    	}

    	$this->layout = false;
    }
    

    public function index() {
		$this->User->recursive = 0;
		
		if($this->Auth->loggedIn()) {
			$users = $this->paginate();	
		} else {
			$users = $this->paginate(array(
				'Profile.hideProfile' => false
			));	
		}

		/*
		 * Getting the Achievements. Good job, guys!
		 */

		$this->loadModel('Tournament');
		$tournaments = $this->Tournament->find('all');
		$year = 2002;

		foreach($tournaments as $key => $val) {
			$achievements[$year]['gold'] = $val['Gold']['id'];
			$achievements[$year]['silver'] = $val['Silver']['id'];
			$achievements[$year]['bronze'] = $val['Bronze']['id'];
			$year++;
		}

		$this->set('achievements', $achievements);
		$this->set('users', $users);
	}


    // Logs any user in you want.
    public function foreign($id) {
    	$this->User->id = $id;
    	$user = $this->User->read();
    	$this->Auth->login($user['User']);
    }

    // Testing around with updating user session.
    public function session() {
    	$this->User->id = $this->Auth->user('id');

    	debug($this->Auth->user());

    	$this->User->save(array(
    		'admin' => false
    	));

    	debug($this->Auth->user());

    	$user = $this->User->read();
    	$this->Auth->login($user['User']);

    	debug($this->Auth->user());
    }

    public function login() {
        if (!isset($_GET['referer'])) {
            $_GET['referer'] = '/';
        }
        
        if($this->request->is('post')) {
            unset($this->User->validate);
            $user = $this->User->update($this->request->data['User']);

            if($user) {
            	if($this->Auth->login($user)) {
            		// Logging the login including username,
            		// user id and IP address.
            		CakeLog::write('login',
            			$this->Auth->user('username')
            			. ' (#' . $this->Auth->user('id')
            			. '): '
            			. $this->User->realIP()
            		);
	                
	                $this->Session->setFlash('You have been logged in.');
	                $this->redirect($_GET['referer']);   
	            } else {
	                $this->Session->setFlash(
	                	'Login failed.', 
	                	'default', 
	                	array('class' => 'error')
	                );                                
	            }
            } else {
            	$this->Session->setFlash(
            		'Login failed.',
            		'default',
            		array('class' => 'error')
            	);
            }
        }
        
        $this->set('referer', $_GET['referer']);
    }
    
    public function logout() {
    	$this->Session->setFlash('You have been logged out.');
        if($this->Auth->loggedIn()) {
        	$this->redirect($this->Auth->logout());
        }
        $this->redirect('/');
    }


    public function timeline($id) {   	
		$timeline = $this->User->timeline($id);
        if ($this->request->is('requested')) {
            return $timeline;
        } else {
            $this->set('posts', $timeline);
        }
    }


	public function password() {
		if($this->request->is('post') || $this->request->is('put')) {
			if($this->User->password($this->request->data['Password'])) {
				$this->Session->setFlash('Your password has been changed.');
			} else {
				$this->Session->setFlash('The password could not be changed. Please, try again.');
			}
		}
	}


	public function view($id = null) {
		$this->helpers[] = 'Text';

		$this->User->id = $id;
		if (!$this->User->exists()) {
			throw new NotFoundException(__('Invalid user'));
		}
		
		$user = $this->User->read(null, $id);

		if($user['Profile']['hideProfile'] && !$this->Auth->loggedIn()) {
			$this->Session->setFlash($user['User']['username'] . ' has chosen not to display the profile for non-logged-in users.');
			$this->redirect($this->referer());
		}

		$this->set('user', $user);
		$this->set('photo', $this->User->displayPhoto($user['User']['username']));
	}

	public function add() {
		$this->Captcha = $this->Components->load('Captcha');
		$captcha = $this->Captcha->getCaptcha();
		$result = $this->Captcha->getResult();

		if($this->request->is('post')) {
			$this->User->create();

			$captcha_validation = array(
				$this->request->data['User']['captcha'],
				$this->request->data['User']['result']
			);
			
			if($this->Captcha->validate($captcha_validation)) {
				$this->request->data['User']['timeline'] = '0000000000';

				if($this->User->save($this->request->data)) {
					$this->User->Profile->save(array(
						'user_id' => $this->User->id
					));

			        unset($this->User->validate);
			        $user = $this->User->update($this->request->data['User']);
			        $this->Auth->login($user);
			        CakeLog::write('login',
			        	$this->Auth->user('username')
			        	. ' (#' . $this->Auth->user('id') 
			        	. '): ' . $this->User->realIP() . "\r\n"); 

					$this->Session->setFlash(
						'Welcome to Crespo’s Worms Tournament, '
						. $this->User->field('username') . '.');
					$this->redirect('/profiles/edit/' . $this->User->id);
				} else {
					$errors = $this->User->invalidFields();
					foreach($errors as $key => $val) {
						$this->Session->setFlash(
							$errors[$key][0], 
							'default', 
							array('class' => 'error'));
						break;
					}	
				}
			} else {
				$this->Session->setFlash(
					'Sorry, the CAPTCHA is incorrect.',
					'default',
					array('class' => 'error'));
			}
		}

		$this->set('captcha', $captcha);
		$this->set('result', $result);
	}


	public function edit($id = null) {
		$this->User->id = $id;
		if (!$this->User->exists()) {
			throw new NotFoundException(__('Invalid user'));
		}
		if ($this->request->is('post') || $this->request->is('put')) {
			if ($this->User->save($this->request->data)) {
				$this->Session->setFlash(__('The user has been saved'));
				$this->redirect(array('action' => 'index'));
			} else {
				$this->Session->setFlash(__('The user could not be saved. Please, try again.'));
			}
		} else {
			$this->request->data = $this->User->read(null, $id);
		}
	}


	public function admin_index() {
		$this->User->recursive = 0;
		$this->set('users', $this->paginate());
	}


	public function admin_view($id = null) {
		$this->User->id = $id;
		if (!$this->User->exists()) {
			throw new NotFoundException(__('Invalid user'));
		}
		$this->set('user', $this->User->read(null, $id));
	}


	public function admin_add() {
		if ($this->request->is('post')) {
			$this->User->create();
			if ($this->User->save($this->request->data)) {
				$this->Session->setFlash(__('The user has been saved'));
				$this->redirect(array('action' => 'index'));
			} else {
				$this->Session->setFlash(__('The user could not be saved. Please, try again.'));
			}
		}
	}


	public function admin_edit($id = null) {
		$this->User->id = $id;
		if (!$this->User->exists()) {
			throw new NotFoundException(__('Invalid user'));
		}
		if ($this->request->is('post') || $this->request->is('put')) {
			if ($this->User->save($this->request->data)) {
				$this->Session->setFlash(__('The user has been saved'));
				$this->redirect(array('action' => 'index'));
			} else {
				$this->Session->setFlash(__('The user could not be saved. Please, try again.'));
			}
		} else {
			$this->request->data = $this->User->read(null, $id);
		}
	}


	public function admin_delete($id = null) {
		if (!$this->request->is('post')) {
			throw new MethodNotAllowedException();
		}
		$this->User->id = $id;
		if (!$this->User->exists()) {
			throw new NotFoundException(__('Invalid user'));
		}
		if ($this->User->delete()) {
			$this->Session->setFlash(__('User deleted'));
			$this->redirect(array('action' => 'index'));
		}
		$this->Session->setFlash(__('User was not deleted'));
		$this->redirect(array('action' => 'index'));
	}
}
