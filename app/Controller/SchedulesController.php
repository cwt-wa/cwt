<?php

class SchedulesController extends AppController
{
    public $name = 'Schedules';
    public $scaffold = 'admin';

    public function beforeFilter()
    {
        parent::beforeFilter();
        $this->Auth->allow('delete');
    }

    public function index()
    {
        $this->loadModel('Tournament');
        $this->loadModel('Game');
        $this->loadModel('Group');
        $this->loadModel('Playoff');
        $this->loadModel('Stream');

        $this->Schedule->recursive = 1;
        $schedules = $this->Schedule->find('all', array(
            'order' => 'Schedule.when ASC'));

        $schedules = $this->Schedule->scheduledStreams($schedules);
        $schedules = $this->Schedule->filterSchedule($schedules);

        /*
         * Getting the opponents the current user can play against.
         */

        $currentTournament = $this->Schedule->currentTournament();

        if ($currentTournament['Tournament']['status'] == Tournament::GROUP) {
            $this->Group->Standing->recursive = 0;
            $group = $this->Group->Standing->find(
                'first',
                array(
                    'conditions' => array(
                        'Group.tournament_id' => $currentTournament['Tournament']['id'],
                        'Standing.user_id' => $this->Auth->user('id')
                    )
                )
            );

            $opps = $this->Group->attendees($group['Group']['label']);
            $opps = $this->Group->allowedOpponents($opps);
        } elseif ($currentTournament['Tournament']['status'] == Tournament::PLAYOFF) {
            $currentGame = $this->Playoff->currentGame();

            if (!$currentGame) {
                $opps = null;
            } else {
                $opps = $this->Playoff->attendees();
                $opps = $this->Playoff->allowedOpponents($opps);

                if ($currentGame['Playoff']['step'] > 3)
                    $allowedResults['4'] = '4';
            }
        }

        $schedules['opponents'] = !@array_key_exists(0, $opps) ? $opps : false;

        $schedules['datetimes'] = array(
            'days' => $this->Schedule->daysLeft(mktime()),
            'times' => $this->Schedule->getTimes()
        );

        if ($this->request->is('requested')) {
            return $schedules;
        } else {
            $this->set('schedules', $schedules);
        }
    }

    public function add()
    {
        if ($this->request->isAjax()) {
            $this->Schedule->save($this->request->data);
            $this->render('/Elements/scheduler');
        }
    }

    public function delete($id = null)
    {
        $game = $this->Schedule->read(null, $id);

        if ($game['Schedule']['home_id'] == $this->Auth->user('id')) {
            $this->Schedule->delete($id);
        }

        $this->render('/Elements/scheduler');
    }
}
