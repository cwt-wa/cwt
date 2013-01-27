<?php

App::uses('AppController', 'Controller');

class RestoresController extends AppController {

    public function add() {
        if ($this->request->is('post')) {
            $this->Restore->create();
            $this->loadModel('User');

            $logMsgBeginning = '#' . $this->Auth->user('id') . ' '
                    . $this->Auth->user('username')
                    . ' (' . $this->User->realIP() . '): ';

            if ($this->request->is('ajax')) {
                if ($this->Restore->save($this->request->data)) {
                    CakeLog::write('restore', $logMsgBeginning
                            . json_encode($this->request->data));
                } else {
                    CakeLog::write('restore', $logMsgBeginning
                            . json_encode($this->Restore->validationErrors));
                }
                
                Configure::write('debug', 0);
                $this->set('json', $this->Restore->validationErrors);
                $this->render('/Pages/json', 'ajax');
            } else {
                if ($this->Restore->save($this->request->data)) {
                    CakeLog::write('restore', $logMsgBeginning
                            . json_encode($this->request->data));

                    $this->Session->setFlash(
                            'The game has been added to the list 
                            of games to be restored. Thank you!');
                } else {
                    debug($this->Restore->validationErrors);

                    CakeLog::write('restore', $logMsgBeginning
                            . json_encode($this->Restore->validationErrors));

                    $errors = array_values($this->Restore->validationErrors);
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
        $this->set(compact('tournaments', 'homes', 'aways', 'stages', 'scores', 'numberOfAddedGames'));
    }

}
