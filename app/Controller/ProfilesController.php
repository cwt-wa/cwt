<?php
App::uses('AppController', 'Controller');

class ProfilesController extends AppController
{
    public $name = 'Profiles';


    public function beforeFilter()
    {
        parent::beforeFilter();
        $this->Auth->allow('settings');
    }


    // Quick edit of users whose country is unknown.
    public function quickCountry()
    {
        if ($this->request->is('post')) {
            $this->Profile->save(array(
                'id' => $this->request->data['user_id'],
                'country' => $this->request->data['country']
            ));
        }

        $users = $this->Profile->find('all', array(
            'conditions' => array(
                'Profile.country' => 'unknown'
            )
        ));

        $this->set('users', $users);
        $this->set('country', $this->Profile->flags('unknown'));
    }


    public function settings()
    {
        if ($this->request->is('post')) {
            if ($this->request->data['hideProfile'] === 'false') {
                $hideProfile = 0;
            } elseif ($this->request->data['hideProfile'] === 'true') {
                $hideProfile = 1;
            }

            if ($this->request->data['hideEmail'] === 'false') {
                $hideEmail = 0;
            } elseif ($this->request->data['hideEmail'] === 'true') {
                $hideEmail = 1;
            }

            $this->Profile->id = $this->Auth->user('id');
            $this->Profile->save(array(
                'hideProfile' => $hideProfile,
                'hideEmail' => $hideEmail
            ));
        }

        $user = $this->Profile->read(null, $this->Auth->user('id'));

        $setProfile = $user['Profile']['hideProfile'] ? 'checked="checked"' : '';
        $setEmail = $user['Profile']['hideEmail'] ? 'checked="checked"' : '';

        $this->set('setProfile', $setProfile);
        $this->set('setEmail', $setEmail);
    }


    public function photo()
    {
        if ($this->request->is('post')) {
            if (isset($this->request->data['Delete'])) {
                if ($this->Profile->deletePhoto()) {
                    $this->Session->setFlash('Your profile photo has been removed.');
                } else {
                    $this->Session->setFlash('Something has gone wrong. Please try again.',
                        'default', array('class' => 'error'));
                }
            } elseif (isset($this->request->data['Upload'])) {
                if ($this->Profile->uploadPhoto($this->request->data['Upload'])) {
                    $this->Session->setFlash('Your profile photo has been uploaded.');
                } else {
                    $this->Session->setFlash('Something has gone wrong. Please try again.',
                        'default', array('class' => 'error'));
                }
            }
        }

        $this->set('photo', $this->Profile->displayPhoto($this->Auth->user('username')));
        $this->set('title_for_layout', 'Change your Photo');
    }


    public function index()
    {
        $this->Profile->recursive = 0;
        $this->set('profiles', $this->paginate());
    }


    public function view($id = null)
    {
        $this->Profile->id = $id;
        if (!$this->Profile->exists()) {
            throw new NotFoundException(__('Invalid profile'));
        }
        $this->set('profile', $this->Profile->read(null, $id));
    }


    public function edit()
    {
        $id = $this->Auth->user('id');
        $this->Profile->id = $id;
        if (!$this->Profile->exists()) {
            throw new NotFoundException(__('Invalid profile'));
        }

        if ($this->request->is('post') || $this->request->is('put')) {
            $this->Profile->User->id = $this->Auth->user('id');

            if ($this->Profile->save($this->request->data)
                && $this->Profile->User->save($this->request->data)
            ) {
                $this->Session->setFlash('Your profile has been updated');
                $newUser = $this->Profile->User->find('first', array(
                    'conditions' => array(
                        'User.id' => $this->Auth->user('id')
                    )
                ));
                $this->Auth->login($newUser['User']);
                $this->redirect('/profiles/edit');
            } else {
                $this->Session->setFlash(
                    'The profile could not be saved. Please, try again.',
                    'default', array('class' => 'error'));
            }
        } else {
            $this->request->data = $this->Profile->read(null, $id);
        }

        $this->Profile->User->id = $this->Auth->user('id');
        $username = $this->Profile->User->field('username');
        $this->set('title_for_layout', 'Edit your Profile');
        $this->set('username', $username);
        $this->set('country', $this->Profile->flags($this->request->data['Profile']['country']));
    }
}
