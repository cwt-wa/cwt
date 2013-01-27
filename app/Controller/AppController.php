<?php
App::uses('Controller', 'Controller');

class AppController extends Controller {
    public $components = array(
        'RequestHandler',
        'Session',
        'Auth' => array(
            'loginRedirect' => array('controller' => 'users', 'action' => 'login'),
            'logoutRedirect'=> array('controller' => 'users', 'action' => 'logout'),
            'authError' => 
                'You don\'t have the required rights to access that page.',
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
        if(@$this->request->params['admin']) {
            if(!$user['admin']) {
                return false; // Admin access.
            } else {
                $this->set('title_for_layout', 'Admin!');
                return true;
            }

        } 

        if($this->action == 'delete') return false; // Deny delete
        return true; // index, view, edit, add  
    }  

    public function beforeFilter() {
        $this->loadModel('User');
        $this->loadModel('Tournament');
        $this->loadModel('Stream');
        // If true, only admins have full access to the site.
        $this->adminsOnlyMode(false);

        // If true, only Zemke has full access to the site.
        $this->maintenanceMode(false);

        // Because we want to submit changes to the array.
        $current_user = $this->Auth->user();

        // User Panel options.
        $current_user['up_menu'] = $this->User->user_menu();
        
        // User Panel options regarding his stream.
        $stream = $this->Stream->checkings();

        // index and view are allowed for non logged in users.
        $this->Auth->allow('index', 'view');
        
        $tourney = $this->Tournament->info();

        // debug($stream);
        // debug($this->Auth->loggedIn());
        // debug($current_user);
        // debug($tourney);
        // return;

        // $this->set('up_stream', $stream);
        $this->set('logged_in', $this->Auth->loggedIn());
        $this->set('current_user', $current_user);
        // $this->set('tourney', $tourney);
    }

    // Will limit access to the page for non-admins.
    public function adminsOnlyMode($bool) {
        if($bool == false) return;

        if($_SERVER['REQUEST_URI'] == '/pages/offline') {
            $this->layout = false;
        } else {
            if(!in_array($this->action, array('login', 'logout'))) {
                 if(!$this->Auth->user('admin')) {
                    $this->redirect('/pages/offline');    
                }
            }
        }
    }

    // Will limit access to the page for non-Zemkes.
    public function maintenanceMode($bool) {
        if($bool == false) return;

        if($_SERVER['REQUEST_URI'] == '/pages/maintenance') {
            $this->layout = false;
        } else {
            if(!in_array($this->action, array('login', 'logout'))) {
                 if($this->Auth->user('id') != 1) {
                    $this->redirect('/pages/maintenance');    
                }
            }

            Configure::write('debug', 2);
        }
    }
}