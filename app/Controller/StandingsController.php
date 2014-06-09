<?php
App::uses('AppController', 'Controller');

/**
 * Standings Controller
 *
 * @property Standing $Standing
 */
class StandingsController extends AppController
{

    /**
     * index method
     *
     * @return void
     */
    public function index()
    {
        $this->Standing->recursive = 0;
        $this->set('standings', $this->paginate());
    }

    /**
     * view method
     *
     * @throws NotFoundException
     * @param string $id
     * @return void
     */
    public function view($id = null)
    {
        $this->Standing->id = $id;
        if (!$this->Standing->exists()) {
            throw new NotFoundException(__('Invalid standing'));
        }
        $this->set('standing', $this->Standing->read(null, $id));
    }

    /**
     * add method
     *
     * @return void
     */
    public function add()
    {
        if ($this->request->is('post')) {
            $this->Standing->create();
            if ($this->Standing->save($this->request->data)) {
                $this->Session->setFlash(__('The standing has been saved'));
                $this->redirect(array('action' => 'index'));
            } else {
                $this->Session->setFlash(__('The standing could not be saved. Please, try again.'));
            }
        }
        $groups = $this->Standing->Group->find('list');
        $this->set(compact('groups'));
    }

    /**
     * edit method
     *
     * @throws NotFoundException
     * @param string $id
     * @return void
     */
    public function edit($id = null)
    {
        $this->Standing->id = $id;
        if (!$this->Standing->exists()) {
            throw new NotFoundException(__('Invalid standing'));
        }
        if ($this->request->is('post') || $this->request->is('put')) {
            if ($this->Standing->save($this->request->data)) {
                $this->Session->setFlash(__('The standing has been saved'));
                $this->redirect(array('action' => 'index'));
            } else {
                $this->Session->setFlash(__('The standing could not be saved. Please, try again.'));
            }
        } else {
            $this->request->data = $this->Standing->read(null, $id);
        }
        $groups = $this->Standing->Group->find('list');
        $this->set(compact('groups'));
    }

    /**
     * delete method
     *
     * @throws MethodNotAllowedException
     * @throws NotFoundException
     * @param string $id
     * @return void
     */
    public function delete($id = null)
    {
        if (!$this->request->is('post')) {
            throw new MethodNotAllowedException();
        }
        $this->Standing->id = $id;
        if (!$this->Standing->exists()) {
            throw new NotFoundException(__('Invalid standing'));
        }
        if ($this->Standing->delete()) {
            $this->Session->setFlash(__('Standing deleted'));
            $this->redirect(array('action' => 'index'));
        }
        $this->Session->setFlash(__('Standing was not deleted'));
        $this->redirect(array('action' => 'index'));
    }
}
