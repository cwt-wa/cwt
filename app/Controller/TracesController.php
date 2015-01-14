<?php
App::uses('AppController', 'Controller');

/**
 * Traces Controller
 *
 * @property Trace $Trace
 */
class TracesController extends AppController
{
    public $paginate = array(
        'limit' => 20,
        'order' => array(
            'Trace.created' => 'desc'
        )
    );

    /**
     * index method
     *
     * @return void
     */
    public function index()
    {
        $this->Paginator->settings = array(
            'order' => array(
                'Trace.created' => 'desc'
            )
        );

        $title_for_layout = 'Ratings and Bets';
        $conditions = array();
        if (isset($_GET['user_id'])) {
            $conditions['user_id'] = $_GET['user_id'];
            $this->Paginator->settings['user_id'] = $_GET['user_id'];
            $this->loadModel('User');
            $user = $this->User->findById($_GET['user_id']);
            $this->set('user', $user);
            $title_for_layout .= ' by ' . $user['User']['username'];
        }
        if (isset($_GET['game_id'])) {
            $conditions['on'] = $_GET['game_id'];
            $this->Paginator->settings['game_id'] = $_GET['game_id'];
            $this->loadModel('Game');
            $game = $this->Game->findById($_GET['game_id']);
            $this->set('game', $game);
            $title_for_layout .= ' of Game';
        }

        $this->Trace->recursive = 1;
        $traces = $this->Paginator->paginate(null, $conditions);
        $tracesCount = count($traces);

        for ($i = 0; $i < $tracesCount; $i++) {
            $traces[$i]['Game']['Home'] = $this->Trace->User->findById($traces[$i]['Game']['home_id']);
            $traces[$i]['Game']['Away'] = $this->Trace->User->findById($traces[$i]['Game']['away_id']);
        }

        $this->set('title_for_layout', $title_for_layout);
        $this->set('traces', $traces);
    }

    /**
     * view method
     *
     * @param string $id
     * @return void
     */
    public function view($id = null)
    {
        $this->Trace->id = $id;
        if (!$this->Trace->exists()) {
            throw new NotFoundException(__('Invalid trace'));
        }
        $this->set('trace', $this->Trace->read(null, $id));
    }

    /**
     * add method
     *
     * @return void
     */
    public function add()
    {
        if ($this->request->is('post')) {
            $this->Trace->create();
            if ($this->Trace->save($this->request->data)) {
                $this->Session->setFlash(__('The trace has been saved'));
                $this->redirect(array('action' => 'index'));
            } else {
                $this->Session->setFlash(__('The trace could not be saved. Please, try again.'));
            }
        }
        $users = $this->Trace->User->find('list');
        $this->set(compact('users'));
    }

    /**
     * edit method
     *
     * @param string $id
     * @return void
     */
    public function edit($id = null)
    {
        $this->Trace->id = $id;
        if (!$this->Trace->exists()) {
            throw new NotFoundException(__('Invalid trace'));
        }
        if ($this->request->is('post') || $this->request->is('put')) {
            if ($this->Trace->save($this->request->data)) {
                $this->Session->setFlash(__('The trace has been saved'));
                $this->redirect(array('action' => 'index'));
            } else {
                $this->Session->setFlash(__('The trace could not be saved. Please, try again.'));
            }
        } else {
            $this->request->data = $this->Trace->read(null, $id);
        }
        $users = $this->Trace->User->find('list');
        $this->set(compact('users'));
    }

    /**
     * delete method
     *
     * @param string $id
     * @return void
     */
    public function delete($id = null)
    {
        if (!$this->request->is('post')) {
            throw new MethodNotAllowedException();
        }
        $this->Trace->id = $id;
        if (!$this->Trace->exists()) {
            throw new NotFoundException(__('Invalid trace'));
        }
        if ($this->Trace->delete()) {
            $this->Session->setFlash(__('Trace deleted'));
            $this->redirect(array('action' => 'index'));
        }
        $this->Session->setFlash(__('Trace was not deleted'));
        $this->redirect(array('action' => 'index'));
    }

    /**
     * admin_index method
     *
     * @return void
     */
    public function admin_index()
    {
        $this->Trace->recursive = 0;
        $this->set('traces', $this->paginate());
    }

    /**
     * admin_view method
     *
     * @param string $id
     * @return void
     */
    public function admin_view($id = null)
    {
        $this->Trace->id = $id;
        if (!$this->Trace->exists()) {
            throw new NotFoundException(__('Invalid trace'));
        }
        $this->set('trace', $this->Trace->read(null, $id));
    }

    /**
     * admin_add method
     *
     * @return void
     */
    public function admin_add()
    {
        if ($this->request->is('post')) {
            $this->Trace->create();
            if ($this->Trace->save($this->request->data)) {
                $this->Session->setFlash(__('The trace has been saved'));
                $this->redirect(array('action' => 'index'));
            } else {
                $this->Session->setFlash(__('The trace could not be saved. Please, try again.'));
            }
        }
        $users = $this->Trace->User->find('list');
        $this->set(compact('users'));
    }

    /**
     * admin_edit method
     *
     * @param string $id
     * @return void
     */
    public function admin_edit($id = null)
    {
        $this->Trace->id = $id;
        if (!$this->Trace->exists()) {
            throw new NotFoundException(__('Invalid trace'));
        }
        if ($this->request->is('post') || $this->request->is('put')) {
            if ($this->Trace->save($this->request->data)) {
                $this->Session->setFlash(__('The trace has been saved'));
                $this->redirect(array('action' => 'index'));
            } else {
                $this->Session->setFlash(__('The trace could not be saved. Please, try again.'));
            }
        } else {
            $this->request->data = $this->Trace->read(null, $id);
        }
        $users = $this->Trace->User->find('list');
        $this->set(compact('users'));
    }

    /**
     * admin_delete method
     *
     * @param string $id
     * @return void
     */
    public function admin_delete($id = null)
    {
        if (!$this->request->is('post')) {
            throw new MethodNotAllowedException();
        }
        $this->Trace->id = $id;
        if (!$this->Trace->exists()) {
            throw new NotFoundException(__('Invalid trace'));
        }
        if ($this->Trace->delete()) {
            $this->Session->setFlash(__('Trace deleted'));
            $this->redirect(array('action' => 'index'));
        }
        $this->Session->setFlash(__('Trace was not deleted'));
        $this->redirect(array('action' => 'index'));
    }
}
