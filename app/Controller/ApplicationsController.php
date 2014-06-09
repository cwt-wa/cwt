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


    public function edit($id = null)
    {
        $this->Application->id = $id;
        if (!$this->Application->exists()) {
            throw new NotFoundException(__('Invalid application'));
        }
        if ($this->request->is('post') || $this->request->is('put')) {
            if ($this->Application->save($this->request->data)) {
                $this->Session->setFlash(__('The application has been saved'));
                $this->redirect(array('action' => 'index'));
            } else {
                $this->Session->setFlash(__('The application could not be saved. Please, try again.'));
            }
        } else {
            $this->request->data = $this->Application->read(null, $id);
        }
        $users = $this->Application->User->find('list');
        $this->set(compact('users'));
    }


    public function delete($id = null)
    {
        if (!$this->request->is('post')) {
            throw new MethodNotAllowedException();
        }
        $this->Application->id = $id;
        if (!$this->Application->exists()) {
            throw new NotFoundException(__('Invalid application'));
        }
        if ($this->Application->delete()) {
            $this->Session->setFlash(__('Application deleted'));
            $this->redirect(array('action' => 'index'));
        }
        $this->Session->setFlash(__('Application was not deleted'));
        $this->redirect(array('action' => 'index'));
    }


    public function admin_index()
    {
        $this->Application->recursive = 0;
        $this->set('applications', $this->paginate());
    }


    public function admin_view($id = null)
    {
        $this->Application->id = $id;
        if (!$this->Application->exists()) {
            throw new NotFoundException(__('Invalid application'));
        }
        $this->set('application', $this->Application->read(null, $id));
    }


    public function admin_add()
    {
        if ($this->request->is('post')) {
            $this->Application->create();
            if ($this->Application->save($this->request->data)) {
                $this->Session->setFlash(__('The application has been saved'));
                $this->redirect(array('action' => 'index'));
            } else {
                $this->Session->setFlash(__('The application could not be saved. Please, try again.'));
            }
        }
        $users = $this->Application->User->find('list');
        $this->set(compact('users'));
    }


    public function admin_edit($id = null)
    {
        $this->Application->id = $id;
        if (!$this->Application->exists()) {
            throw new NotFoundException(__('Invalid application'));
        }
        if ($this->request->is('post') || $this->request->is('put')) {
            if ($this->Application->save($this->request->data)) {
                $this->Session->setFlash(__('The application has been saved'));
                $this->redirect(array('action' => 'index'));
            } else {
                $this->Session->setFlash(__('The application could not be saved. Please, try again.'));
            }
        } else {
            $this->request->data = $this->Application->read(null, $id);
        }
        $users = $this->Application->User->find('list');
        $this->set(compact('users'));
    }


    public function admin_delete($id = null)
    {
        if (!$this->request->is('post')) {
            throw new MethodNotAllowedException();
        }
        $this->Application->id = $id;
        if (!$this->Application->exists()) {
            throw new NotFoundException(__('Invalid application'));
        }
        if ($this->Application->delete()) {
            $this->Session->setFlash(__('Application deleted'));
            $this->redirect(array('action' => 'index'));
        }
        $this->Session->setFlash(__('Application was not deleted'));
        $this->redirect(array('action' => 'index'));
    }
}
