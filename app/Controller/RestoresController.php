<?php

App::uses('AppController', 'Controller');

class RestoresController extends AppController
{

    public function beforeFilter()
    {
        parent::beforeFilter();

        if (!$this->Auth->loggedIn()) {
            CakeLog::write('restores',
                $this->Restore->prependUserInfo('Login halt.',
                    $this->Auth->loggedIn()));
            $this->Auth->flash($this->Auth->authError);
            $this->redirect('/users/login?referer=/restore');
        }
    }

    public function add()
    {
        CakeLog::write('restores',
            $this->Restore->prependUserInfo('Accessing the page.',
                $this->Auth->loggedIn()));

        if ($this->request->is('post')) {
            $this->Restore->create();
            $this->loadModel('User');

            $this->request->data['Restore']['reported'] =
                $this->Restore->formatDate(
                    $this->request->data['Restore']['reported']);

            if ($this->Restore->save($this->request->data)) {
                CakeLog::write('restores',
                    $this->Restore->prependUserInfo(
                        'Restored ' . json_encode($this->request->data),
                        $this->Auth->loggedIn()));

                if ($this->request->is('ajax')) {
                    $this->set('response', 'Successfully added. Thank you!');
                    $this->render('/Pages/response', 'ajax');
                } else {
                    $this->Session->setFlash(
                        'The game has been added to the list
                        of games to be restored. Thank you!');
                }
            } else {
                CakeLog::write('restores',
                    $this->Restore->prependUserInfo(
                        'Failure ' . json_encode($this->Restore->validationErrors),
                        $this->Auth->loggedIn()));
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
            'tournaments', 'homes', 'aways', 'stages', 'scores', 'numberOfAddedGames', 'restores'));
    }

    public function index()
    {
        $this->set('restores', $restores = $this->paginate());
    }

    public function add_user()
    {
        $this->loadModel('User');

        $this->User->save(array(
            'username' => $this->request->data["username"],
            'stage' => 'retired',
            'participations' => '1'
        ));

        $this->set('response', $this->User->id);
        $this->render('/Pages/response_empty', 'ajax');
    }

}
