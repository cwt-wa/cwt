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
            'limit' => 15,
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
}
