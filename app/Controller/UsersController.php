<?php
App::uses('AppController', 'Controller');

class UsersController extends AppController
{
    public $name = 'Users';
    public $scaffold = 'admin';


    public function beforeFilter()
    {
        parent::beforeFilter();
        $this->Auth->allow(
            'add', 'logout', 'timeline', 'password', 'password_forgotten', 'reset_password', 'ranking');
    }

    public function ranking() {
        $this->User->recursive = 0;

        $users = $this->User->find('all', array(
            'conditions' => array(
                    'User.achievements >' => 0,
                    'User.participations >' => 1
                ),
                'order' => array('User.achievements' => 'desc')
        ));
        $achievements = $this->User->gatherAchievements();
        $usersLength = count($users);
        $equalizer = 0;

        for ($i = 0; $i < $usersLength; $i++) {
            $users[$i]['position'] = $i + 1;

            if ($i == 0) {
                continue;
            }

            if ($users[$i - 1]['User']['achievements'] == $users[$i]['User']['achievements']) {
                $users[$i]['position'] = $i - $equalizer;
                $equalizer++;
            } else {
                $equalizer = 0;
            }
        }

        $this->set('title_for_layout', 'All-Time Ranking');
        $this->set('achievements', $achievements);
        $this->set('users', $users);
    }

    public function index()
    {
        $this->User->recursive = 0;

        $this->Paginator->settings = array(
            'User' => array(
                'order' => array('User.participations' => 'desc'),
                'limit' => 15
            )
        );

        if ($this->Auth->loggedIn()) {
            $users = $this->paginate();
        } else {
            $users = $this->paginate(array(
                'Profile.hideProfile' => false
            ));
        }

        $achievements = $this->User->gatherAchievements();

        $this->set('achievements', $achievements);
        $this->set('users', $users);
    }


    // Logs any user in you want.
    public function foreign($id)
    {
        $this->User->id = $id;
        $user = $this->User->read();
        $this->Auth->login($user['User']);
    }

    // Testing around with updating user session.
    public function session()
    {
        $this->User->id = $this->Auth->user('id');

        $this->User->save(array(
            'admin' => false
        ));


        $user = $this->User->read();
        $this->Auth->login($user['User']);
    }

    public function login()
    {
        if (!isset($_GET['referer'])) {
            $_GET['referer'] = '/';
        }

        if ($this->request->is('post')) {
            unset($this->User->validate);
            $user = $this->User->update($this->request->data['User']);

            if ($user) {
                if ($this->Auth->login($user)) {
                    // Logging the login including username,
                    // user id and IP address.
                    CakeLog::write('login',
                        $this->Auth->user('username')
                        . ' (#' . $this->Auth->user('id')
                        . '): '
                        . $this->User->realIP()
                    );

                    // Setting the User Cookie for three months.
                    $this->Cookie->write(
                        'User', $this->Auth->user('id'),
                        true, 60 * 60 * 24 * 30 * 3);

                    $this->Session->setFlash('You have been logged in.');
                    $this->redirect($this->referer());
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

    public function logout()
    {
        $this->Cookie->delete('User');
        $this->Cookie->destroy();

        $this->Session->setFlash('You have been logged out.');
        if ($this->Auth->loggedIn()) {
            $this->redirect($this->Auth->logout());
        }
        $this->redirect('/');
    }

    public function password_forgotten()
    {
        if ($this->request->is('post')) {
            $this->User->recursive = 0;
            $user = $this->User->findById($this->request->data['User']['userWhoForgot']);
            $this->User->id = $user['User']['id'];

            if (empty($user['Profile']['email'])) {
                $response = 'You’ve never provided an email address, '
                    . 'hence we can’t send you a new password. Please reach out to us. '
                    . ' <a href="mailto:support@cwtsite.com">support@cwtsite.com</a>';
                $responsePositive = false;
            } else {
                $response = 'An email was sent to ' . $user['Profile']['email']
                    . '. Did not receive anything? - Please reach out to us. '
                    . '<a href="mailto:support@cwtsite.com">support@cwtsite.com</a>';
                $responsePositive = true;

                $resetKey = Security::hash($this->User->randomPassword());

                $this->User->save(array(
                    'reset_key' => $resetKey
                ), false); // No validation needed.

                App::uses('CakeEmail', 'Network/Email');

                $Email = new CakeEmail();
                $Email->template('password_forgotten');
                $Email->emailFormat('html');
                $Email->viewVars(array(
                    'resetKey' => $resetKey,
                    'username' => $user['User']['username']
                ));
                $Email->from(array('support@cwtsite.com' => 'CWT Support'));
                $Email->to($user['Profile']['email']);
                $Email->subject('Password Recovery');
                $Email->send();
            }

            if ($responsePositive) {
                $this->Session->setFlash($response);
            } else {
                $this->Session->setFlash($response, 'default', array('class' => 'error'));
            }
        }

        $this->set('userWhoForgots', $this->User->find('list'));
    }

    public function reset_password($resetKey)
    {
        $user = $this->User->find('first', array(
            'conditions' => array(
                'reset_key' => $resetKey
            )
        ));

        if (empty($user)) {
            $this->Session->setFlash(
                'Sorry, something went wrong. Please reach out to us <a href="mailto:support@cwtsite.com">support@cwtsite.com</a>.',
                'default', array('class' => 'error'));
            $this->redirect('/users/password_forgotten');
            return;
        }

        $this->User->id = $user['User']['id'];

        if ($this->request->is('post')) {
            if ($this->request->data['Password']['new1'] == $this->request->data['Password']['new2']) {
                $this->User->save(array(
                    'password' => $this->request->data['Password']['new1'],
                    'md5password' => '',
                    'reset_key' => ''
                ), false);
                $this->Auth->login($this->User->update(array(
                    'username' => $user['User']['username'],
                    'password' => $this->request->data['Password']['new1']
                )));
                $this->Session->setFlash('Your password has been reset and you are logged in.');
                $this->redirect('/');
            } else {
                $this->Session->setFlash(
                    'The passwords do not match. Please try again.',
                    'default', array('class' => 'error'));
            }
        }
    }

    public function timeline($id)
    {
        $timeline = $this->User->timeline($id);
        if ($this->request->is('requested')) {
            return $timeline;
        } else {
            $this->set('posts', $timeline);
        }
    }


    public function password()
    {
        if ($this->request->is('post') || $this->request->is('put')) {
            if ($this->User->password($this->request->data['Password'])) {
                $this->Session->setFlash('Your password has been changed.');
            } else {
                $this->Session->setFlash('The password could not be changed. Please, try again.',
                    'default', array('class' => 'error'));
            }
        }

        $this->set('title_for_layout', 'Change your Password');
    }


    public function view($id = null)
    {
        $this->helpers[] = 'Text';

        if (is_numeric($id)) {
            $this->User->id = $id;
        } else {
            $userByUsername = $this->User->find('first', array(
                'conditions' => array(
                    'username' => $id
                )
            ));
            $this->User->id = $userByUsername['User']['id'];
        }

        if (!$this->User->exists()) {
            throw new NotFoundException(__('Invalid user'));
        }

        $this->User->recursive = -1;
        $user = $this->User->read();
        $this->User->Profile->recursive = -1;
        $profile = $this->User->Profile->find(
            'first',
            array(
                'conditions' => array(
                    'Profile.user_id' => $this->User->id
                )
            )
        );
        $user['Profile'] = $profile['Profile'];

        if ($user['Profile']['hideProfile'] && !$this->Auth->loggedIn()) {
            $this->Session->setFlash($user['User']['username'] . ' has chosen not to display the profile for non-logged-in users.',
                'default', array('class' => 'error'));
            $this->redirect($this->referer());
        }

        $this->set('user', $user);
        $this->set('photo', $this->User->displayPhoto($user['User']['username']));
        $this->set('title_for_layout', $user['User']['username']);
    }

    public function add()
    {
        $this->Captcha = $this->Components->load('Captcha');
        $captcha = $this->Captcha->getCaptcha();
        $result = $this->Captcha->getResult();

        if ($this->request->is('post')) {
            $this->User->create();

            $captcha_validation = array(
                $this->request->data['User']['captcha'],
                $this->request->data['User']['result']
            );

            if ($this->Captcha->validate($captcha_validation)) {
                $this->request->data['User']['timeline'] = '';
                $this->loadModel('Tournament');
                $numberOfArchivedTournaments = $this->Tournament->find('count', array(
                    'conditions' => array(
                        'Tournament.status' => Tournament::ARCHIVED
                    )
                ));
                for ($i = 0; $i < $numberOfArchivedTournaments; $i++) {
                    $this->request->data['User']['timeline'] .= '0';
                }

                if ($this->User->save($this->request->data)) {
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
                    foreach ($errors as $key => $val) {
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

    public function admin_merge_duplicating_users()
    {
        if ($this->request->is('post')) {
            sort($this->request->data['Merge']['Legacy'], SORT_NUMERIC);
            debug($this->request->data);
        }

        $this->set('users', $this->User->find('list', array('order' => 'username ASC')));
    }

}
