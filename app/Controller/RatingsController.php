<?php
App::uses('AppController', 'Controller');

class RatingsController extends AppController
{
    public $name = 'Ratings';
    public $scaffold = 'admin';


    public function view($game_id)
    {
        $rating = $this->Rating->ratingStats($game_id);

        $this->loadModel('Trace');
        $possibleTraces = array('likes', 'dislikes', 'lightside', 'darkside');

        foreach ($possibleTraces as $possibleTrace) {
            if ($this->Trace->check('Rating', 'add', $possibleTrace, $game_id, 'read') != null) {
                $rating['trace'][$possibleTrace] = true;
            } else {
                $rating['trace'][$possibleTrace] = false;
            }
        }

        if ($this->request->is('requested')) {
            return $rating;
        } else {
            $this->set('rating', $rating);
        }

        $this->set('rating', $rating);
        $this->set('gameId', $game_id);
    }


    public function add()
    {
        if ($this->request->is('post')) {
            $this->loadModel('Trace');

            switch ($this->request->data['rating']) {
                case 'like':
                    $do = 'likes';
                    break;
                case 'dislike':
                    $do = 'dislikes';
                    break;
                case 'lightside':
                    $do = 'lightside';
                    break;
                case 'darkside':
                    $do = 'darkside';
            }

            if ($this->Trace->check('Rating', 'add', $do, $this->request->data['gameId'], 'read') != null) {
                return false; // Has already rated the game.
            }

            $exists = $this->Rating->find('first', array(
                'conditions' => array(
                    'Rating.game_id' => $this->request->data['gameId']
                )
            ));

            if (!$exists || $exists == null) {
                $this->Rating->save(array(
                    $do => 1,
                    'game_id' => $this->request->data['gameId']
                ));
            } else {
                $this->Rating->id = $exists['Rating']['id'];
                $this->Rating->save(array(
                    $do => $this->Rating->field($do) + 1,
                ));
            }

            // Tournament News!
            if ($do == 'likes' || $do == 'dislikes') {
                $this->loadModel('Infoboard');
                $this->Infoboard->tournamentNews(
                    $this->request->data['gameId'], $do);
            }

            $this->Trace->check('Rating', 'add', $do, $this->request->data['gameId'], 'write');
        }
    }
}
