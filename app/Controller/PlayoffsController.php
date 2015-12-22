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
                        'There is no tournament in playoff stage right now.',
                    'default', array('class' => 'error'));
                $this->redirect('/');
            } else {
                $this->Session->setFlash(
                        'There is no tournament in playoff stage right now.<br/>Here is the most recent tournament from the archive.',
                        'default', array('class' => 'error'));
                $this->redirect('/archive/' . $mostRecentTournament['Tournament']['year'] . '#playoffs');
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
}
