<?php
App::uses('AppController', 'Controller');

/**
 * @property Group Group
 */
class GroupsController extends AppController
{
    public $name = 'Groups';
    public $scaffold = 'admin';

    public function beforeFilter()
    {
        parent::beforeFilter();

        // Only permit access when tournament is running.
        // Status: group, playoff, finished
        if (in_array($this->action,
            array('edit', 'delete', 'add', 'index', 'view'))
        ) {
            if (!$this->Group->gamesCanBeReported()) {
                $this->Session->setFlash('There is no tournament right now. Here is the most recent tournament from the archive.',
                    'default', array('class' => 'error'));
                $this->loadModel('Tournament');
                $mostRecentTournament = $this->Tournament->find('first', array('order' => 'Tournament.year DESC'));
                $this->redirect('/archive/' . $mostRecentTournament['Tournament']['year']);
            }
        }
    }

    public function index($tournamentId = null)
    {
        $group = $this->Group->findForGroupsPage($tournamentId);
        $this->set('group', $group);
    }

    public function view($id = null)
    {
        $this->Group->id = $id;
        if (!$this->Group->exists()) {
            throw new NotFoundException(__('Invalid group'));
        }
        $this->set('group', $this->Group->read(null, $id));
    }

    public function admin_add()
    {
        if ($this->Group->numberOfApplicants() < Tournament::PARTICIPANTS) {
            $this->Session->setFlash(
                'There have to be at least 32 applicants to create the groups.',
                'default', array('class' => 'error'));
            $this->redirect($this->referer());
        }

        if ($this->request->is('post')) {
            if ($this->Group->start($this->request->data['Group'])) {
                $this->Session->setFlash('Groups successfully drawn, now enter the group stage.');
                $this->redirect('/admin/tournaments/edit');
            } else {
                $this->Session->setFlash('At least one user has been assigned multiple times.',
                    'default', array('class' => 'error'));
            }
        }

        $this->loadModel('Application');
        $this->Application->recursive = 1;
        $users = $this->Application->find('all');
        $applicants = array();

        foreach ($users as $user) {
            $applicants[$user['User']['id']] = $user['User']['username'];
        }

        uasort($applicants, 'strcasecmp');
        $this->set('users', $applicants);
    }


    // Replacing a player from the group stage.

    public function admin_edit()
    {
        if ($this->request->is('post')) {
            if ($this->Group->replacePlayer($this->request->data['Group']['Inactive'], $this->request->data['Group']['Active'])) {
                $this->Auth->login($this->User->re_login());
                $this->Session->setFlash(
                    'The players have been exchanged successfully.');
                $this->redirect('/groups');
            } else {
                $this->Session->setFlash(
                    'An error occurred executing the command. Please retry.',
                    'default', array('class' => 'error'));
                $this->redirect($this->referer());
            }
        }

        // Waiting players:
        $active = $this->User->find('list', array(
            'conditions' => array(
                'User.stage !=' => 'group'
            ), 'order' => 'User.username ASC'
        ));

        // Players in the group stage.
        $inactive = $this->User->find('list', array(
            'conditions' => array(
                'User.stage' => 'group'
            ), 'order' => 'User.username ASC'
        ));

        $this->set(compact('active'));
        $this->set(compact('inactive'));
    }
}
