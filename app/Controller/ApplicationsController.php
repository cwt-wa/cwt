<?php
App::uses('AppController', 'Controller');

class ApplicationsController extends AppController
{
    public $name = 'Applications';
    public $scaffold = 'admin';


    public function index()
    {
        $this->helpers[] = 'Time';
        $this->Application->recursive = 1;
        $applicants = $this->Application->find('all', array(
            'order' => 'Application.created ASC'
        ));
        $this->set('applicants', $applicants);
    }


    public function view($id = null)
    {
        $this->Application->id = $id;
        if (!$this->Application->exists()) {
            throw new NotFoundException(__('Invalid application'));
        }
        $this->set('application', $this->Application->read(null, $id));
    }


    public function add()
    {
        if ($this->request->is('post')) {
            if ($this->request->data['Apply']['agree']) {
                $this->Application->User->save(array(
                    'id' => $this->Auth->user('id'),
                    'stage' => 'applied'
                ));

                $this->Application->save(array(
                    'user_id' => $this->Auth->user('id'),
                ));

                $this->Auth->login($this->User->re_login());

                $this->Session->setFlash('The staff will now consider your application.');
                $this->redirect('/applications');
            } else {
                $this->Session->setFlash('You have to agree on being acquainted with the rules.');
                $this->redirect('/rules/view/apply');
            }
        } else {
            $this->redirect($this->referer());
        }
    }
}
