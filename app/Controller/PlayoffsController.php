<?php
App::uses('AppController', 'Controller');

class PlayoffsController extends AppController
{
    public $name = 'Playoffs';
    public $scaffold = 'admin';

    public function beforeFilter()
    {
        parent::beforeFilter();

        if ($this->Auth->user('admin') && $this->request->params['action'] == 'admin_add') {
            $this->Session->setFlash(
                'Please build the playoff tree.');
        }
    }

    public function index($bet = false, $tournamentId = null)
    {
        $currentTournament = $this->Playoff->currentTournament();

        if ($tournamentId == null) {
            $tournamentId = $currentTournament['Tournament']['id'];
        }

        if ($currentTournament['Tournament']['id'] == $tournamentId
            && $currentTournament['Tournament']['status'] != Tournament::PLAYOFF
        ) {
            $this->loadModel('Tournament');
            $mostRecentTournament = $this->Tournament->find('first', array(
                'conditions' => array(
                    'status' => Tournament::ARCHIVED
                ),
                'order' => 'Tournament.year DESC'
            ));

            if (empty($mostRecentTournament)) {
                $this->Session->setFlash(
                        'There is no tournament in playoff stage right now.');              
                $this->redirect('/'); 
            } else {
                $this->Session->setFlash(
                        'There is no tournament in playoff stage right now. Here is the most recent tournament from the archive.');
                $this->redirect('/archive/' . $mostRecentTournament['Tournament']['year']); 
            }
        }

        $this->Playoff->unbindModel(array('hasMany' => array('Tournament')));
        $this->loadModel('Rating');
        $this->loadModel('Trace');

        // User has bet on a game.
        if ($bet) {
            $bet = ($this->request->data['h_or_a'] == 'home') ? 'bet_h' : 'bet_a';

            if ($this->Trace->check('Bet', 'add', $bet, $this->request->data['game_id'], 'read') != null) {
                return false; // Already placed bet.
            }

            $game = $this->Playoff->find('first', array(
                'conditions' => array(
                    'Playoff.game_id' => $this->request->data['game_id']
                )
            ));

            $calcBets = $game['Playoff'][$bet] + 1;

            $this->Playoff->save(array(
                'id' => $game['Playoff']['id'],
                $bet => $calcBets
            ));

            $this->Trace->check('Bet', 'add', $bet, $this->request->data['game_id'], 'write');

            unset($game);
        }

        $playoff = $this->Playoff->findForPlayoffsPage($tournamentId);
        $this->set('playoff', $playoff);
    }


    public function view($id = null)
    {
        $this->Playoff->id = $id;
        if (!$this->Playoff->exists()) {
            throw new NotFoundException(__('Invalid playoff'));
        }
        $this->set('playoff', $this->Playoff->read(null, $id));
    }


    public function add()
    {
        if ($this->request->is('post')) {
            $this->Playoff->create();
            if ($this->Playoff->save($this->request->data)) {
                $this->Session->setFlash(__('The playoff has been saved'));
                $this->redirect(array('action' => 'index'));
            } else {
                $this->Session->setFlash(__('The playoff could not be saved. Please, try again.'));
            }
        }
        $games = $this->Playoff->Game->find('list');
        $this->set(compact('games'));
    }


    public function edit($id = null)
    {
        $this->Playoff->id = $id;
        if (!$this->Playoff->exists()) {
            throw new NotFoundException(__('Invalid playoff'));
        }
        if ($this->request->is('post') || $this->request->is('put')) {
            if ($this->Playoff->save($this->request->data)) {
                $this->Session->setFlash(__('The playoff has been saved'));
                $this->redirect(array('action' => 'index'));
            } else {
                $this->Session->setFlash(__('The playoff could not be saved. Please, try again.'));
            }
        } else {
            $this->request->data = $this->Playoff->read(null, $id);
        }
        $games = $this->Playoff->Game->find('list');
        $this->set(compact('games'));
    }


    public function delete($id = null)
    {
        if (!$this->request->is('post')) {
            throw new MethodNotAllowedException();
        }
        $this->Playoff->id = $id;
        if (!$this->Playoff->exists()) {
            throw new NotFoundException(__('Invalid playoff'));
        }
        if ($this->Playoff->delete()) {
            $this->Session->setFlash(__('Playoff deleted'));
            $this->redirect(array('action' => 'index'));
        }
        $this->Session->setFlash(__('Playoff was not deleted'));
        $this->redirect(array('action' => 'index'));
    }


    public function admin_index()
    {
        $this->Playoff->recursive = 0;
        $this->set('playoffs', $this->paginate());
    }


    public function admin_view($id = null)
    {
        $this->Playoff->id = $id;
        if (!$this->Playoff->exists()) {
            throw new NotFoundException(__('Invalid playoff'));
        }
        $this->set('playoff', $this->Playoff->read(null, $id));
    }


    // Building the playoff tree.
    public function admin_add()
    {
        if ($this->request->is('post')) {
            $this->Playoff->create();

            if ($this->Playoff->start($this->request->data['Playoff'])) {
                $this->Session->setFlash('The playoff tree has been built');
                $this->redirect(array('action' => 'index'));
            } else {
                $this->Session->setFlash('At least one user has been assigned multiple times.', 'default', array('class' => 'error'));
            }
        }

        $players = $this->Playoff->attendees();
        $this->set('players', $players);
    }


    public function admin_edit($id = null)
    {
        $this->Playoff->id = $id;
        if (!$this->Playoff->exists()) {
            throw new NotFoundException(__('Invalid playoff'));
        }
        if ($this->request->is('post') || $this->request->is('put')) {
            if ($this->Playoff->save($this->request->data)) {
                $this->Session->setFlash(__('The playoff has been saved'));
                $this->redirect(array('action' => 'index'));
            } else {
                $this->Session->setFlash(__('The playoff could not be saved. Please, try again.'));
            }
        } else {
            $this->request->data = $this->Playoff->read(null, $id);
        }
        $games = $this->Playoff->Game->find('list');
        $this->set(compact('games'));
    }


    public function admin_delete($id = null)
    {
        if (!$this->request->is('post')) {
            throw new MethodNotAllowedException();
        }
        $this->Playoff->id = $id;
        if (!$this->Playoff->exists()) {
            throw new NotFoundException(__('Invalid playoff'));
        }
        if ($this->Playoff->delete()) {
            $this->Session->setFlash(__('Playoff deleted'));
            $this->redirect(array('action' => 'index'));
        }
        $this->Session->setFlash(__('Playoff was not deleted'));
        $this->redirect(array('action' => 'index'));
    }
}
