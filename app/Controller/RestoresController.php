<?php

App::uses('AppController', 'Controller');

class RestoresController extends AppController {

    public function beforeFilter() {
        parent::beforeFilter();

        if (!$this->Auth->loggedIn()) {
            $this->loadModel('User');
            CakeLog::write('restore', $this->User->realIP() . ' Login halt.');
            $this->Auth->flash($this->Auth->authError);
            $this->redirect('/users/login?referer=/restore');
        }
    }

    public function add() {
        $logMsgBeginning = '#' . $this->Auth->user('id') . ' '
                . $this->Auth->user('username')
                . ' (' . $this->User->realIP() . '): ';
        CakeLog::write('restore', $logMsgBeginning . 'Accessing the page.');

        if ($this->request->is('post')) {
            $this->Restore->create();
            $this->loadModel('User');

            $this->request->data['Restore']['reported'] =
                    $this->Restore->formatDate(
                    $this->request->data['Restore']['reported']);

            if ($this->Restore->save($this->request->data)) {
                CakeLog::write('restore', $logMsgBeginning
                        . 'Restored ' . json_encode($this->request->data));

                if ($this->request->is('ajax')) {
                    $this->set('response', 'Successfully added. Thank you!');
                    $this->render('/Pages/response', 'ajax');
                } else {
                    $this->Session->setFlash(
                            'The game has been added to the list 
                            of games to be restored. Thank you!');
                }
            } else {
                CakeLog::write('restore', $logMsgBeginning
                        . 'Failure ' . json_encode($this->Restore->validationErrors));
                $errors = array_values($this->Restore->validationErrors);

                if ($this->request->is('ajax')) {
                    $this->set('failed', true);
                    $this->set('response', $errors[0][0]);
                    $this->render('/Pages/response', 'ajax');
                } else {
                    $this->Session->setFlash($errors[0][0], 'default', array('class' => 'error'));
                }
            }
        }
        $tournaments = $this->Restore->Tournament->find('list', array(
            'conditions' => array(
                'Tournament.year <' => 2010
            )
                ));
        $orderNicknames = array('order' => 'username ASC');
        $homes = $this->Restore->Home->find('list', $orderNicknames);
        $aways = $this->Restore->Away->find('list', $orderNicknames);
        $stages = $this->Restore->validStages;
        $scores = $this->Restore->validScores;
        $numberOfAddedGames = $this->Restore->numberOfAddedGames();
        $this->set('restores', $restores = $this->paginate());
        $this->set(compact(
            'tournaments', 'homes', 'aways', 'stages', 'scores',
            'numberOfAddedGames', 'restores'));
    }
    
    public function index() {
        $this->set('restores', $restores = $this->paginate());
    }

}
