<?php

App::uses('Controller', 'Controller');

class AppController extends Controller {

    public $components = array(
        'RequestHandler',
        'Session',
        'Auth' => array(
            'loginRedirect' => array(
                'controller' => 'users',
                'action' => 'login'
            ),
            'logoutRedirect' => array(
                'controller' => 'users',
                'action' => 'logout'
            ),
            'authError' => 'You don\'t have the required
                    rights to access that page.',
            'authorize' => array('Controller')
        )
    );
    public $helpers = array(
        'Js' => array('Jquery'),
        'Form',
        'Html',
        'Session',
        'Paginator',
        'Text'
    );

    public function isAuthorized($user) {
        if (@$this->request->params['admin']) {
            if (!$user['admin']) {
                return false;
            } else {
                $this->set('title_for_layout', 'Admin!');
                return true;
            }
        }

        if ($this->action == 'delete') {
            return false;
        }
        return true;  
    }

    public function beforeFilter() {
        $this->adminsOnlyMode(false);
        $this->maintenanceMode(false);

        $this->Auth->allow('index', 'view');

        if ($this->Auth->loggedIn()) {
            $this->loadModel('User');
            $current_user = $this->Auth->user();
            $current_user['up_menu'] = $this->User->user_menu();
            $this->set('current_user', $current_user);

            $this->loadModel('Stream');
            $this->set('up_stream', $this->Stream->checkings());
        } else {
            $this->set('up_stream', false);
        }

        $this->set('logged_in', $this->Auth->loggedIn());
    }

    // Will limit access to the page for non-admins.
    public function adminsOnlyMode($bool) {
        if ($bool == false)
            return;

        if ($_SERVER['REQUEST_URI'] == '/pages/offline') {
            $this->layout = false;
        } else {
            if (!in_array($this->action, array('login', 'logout'))) {
                if (!$this->Auth->user('admin')) {
                    $this->redirect('/pages/offline');
                }
            }
        }
    }

    // Will limit access to the page for non-Zemkes.
    public function maintenanceMode($bool) {
        if ($bool == false)
            return;

        if ($_SERVER['REQUEST_URI'] == '/pages/maintenance') {
            $this->layout = false;
        } else {
            if (!in_array($this->action, array('login', 'logout'))) {
                if ($this->Auth->user('id') != 1) {
                    $this->redirect('/pages/maintenance');
                }
            }

            Configure::write('debug', 2);
        }
    }

}