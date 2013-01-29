<?php

App::uses('AppModel', 'Model');

class Restore extends AppModel {

    public $name = 'Restore';
    public $belongsTo = array(
        'Tournament' => array(
            'className' => 'Tournament',
            'foreignKey' => 'tournament_id',
        ),
        'Home' => array(
            'className' => 'User',
            'foreignKey' => 'home_id',
        ),
        'Away' => array(
            'className' => 'User',
            'foreignKey' => 'away_id',
        ),
        'Submitter' => array(
            'className' => 'User',
            'foreignKey' => 'submitter_id',
        )
    );
    public $validStages = array(
        'Group A' => 'Group A',
        'Group B' => 'Group B',
        'Group C' => 'Group C',
        'Group D' => 'Group D',
        'Group E' => 'Group E',
        'Group F' => 'Group F',
        'Group G' => 'Group G',
        'Group H' => 'Group H',
        'Last Sixteen' => 'Last Sixteen',
        'Quarterfinal' => 'Quarterfinal',
        'Semifinal' => 'Semifinal',
        'Third Place' => 'Third Place',
        'Final' => 'Final'
    );
    public $validScores = array(
        '0' => '0',
        '1' => '1',
        '2' => '2',
        '3' => '3',
        '4' => '4'
    );
    public $validate = array(
        'reported' => array(
            'validateMonthDay' => array(
                'rule' => array('validateMonthDay'),
                'message' => 'Invalid date.',
                'allowEmpty' => true
            )
        ),
        'tournament_id' => array(
            'notEmpty' => array(
                'rule' => 'notEmpty',
                'message' => 'Year is required.',
                'required' => true,
                'allowEmpty' => false
            ),
            'validateTournament' => array(
                'rule' => array('validateTournament'),
                'message' => 'Has to be a year from 2002 to 2009.'
            )
        ),
        'stage' => array(
            'notEmpty' => array(
                'rule' => 'notEmpty',
                'message' => 'Stage is required.',
                'required' => true,
                'allowEmpty' => false
            ),
            'validateStage' => array(
                'rule' => array('validateStage'),
                'message' => 'Invalid stage.'
            )
        ),
        'home_id' => array(
            'notEmpty' => array(
                'rule' => 'notEmpty',
                'message' => 'Requires home player.',
                'required' => true,
                'allowEmpty' => false
            ),
            'validatePlayers' => array(
                'rule' => array('validatePlayers'),
                'message' => 'One player can\'t play against himself.'
            ),
            'validateGame' => array(
                'rule' => array('validateGame'),
                'message' => 'Game has already been added.'
            )
        ),
        'away_id' => array(
            'notEmpty' => array(
                'rule' => 'notEmpty',
                'message' => 'Requires away player.',
                'required' => true,
                'allowEmpty' => false
            )
        ),
        'score_a' => array(
            'validateResult' => array(
                'rule' => array('validateResult'),
                'message' => 'Invalid result.'
            )
        )
    );

    public function numberOfAddedGames() {
        $numberOfAddedGames = array();
        $year = 2001;

        for ($tournament_id = 1; $tournament_id < 9; $tournament_id++) {
            $numberOfAddedGames[$tournament_id + $year] = $this->find('count', array(
                'conditions' => array(
                    'Restore.tournament_id' => $tournament_id
                )
            ));
        }

        return $numberOfAddedGames;
    }

    public function beforeValidate($options = array()) {
        $this->data['Restore']['tournament_id'] =
                substr($this->data['Restore']['reported'], 0, 4) - 2001;

        return true;
    }

    public function beforeSave($options = array()) {
        $this->data['Restore']['submitter_id'] = AuthComponent::user('id');
        return true;
    }
    
    public function validateMonthDay($field) {
        $month = substr($field['reported'], 5, 2);
        $day   = substr($field['reported'], 8);
        
        if ($month == '00' && $day == '00') {
            return true;
        }
       
        if ($month < 1 || $day < 1) {
            return false;
        }
        
        if ($month > 12 || $day > 31) {
            return false;
        }
        
        return true;
    }

    public function validateTournament($field) {
        if (!$this->validateId($field['tournament_id'], 'Tournament')) {
            return false;
        }

        $this->Tournament->id = $field['tournament_id'];
        $tournamentYear = $this->Tournament->field('Tournament.year');

        if ($tournamentYear > 2009) {
            return false;
        }

        return true;
    }

    public function validateStage($field) {
        if (!in_array($field['stage'], $this->validStages)) {
            return false;
        }

        return true;
    }

    public function validatePlayers($field) {
        if (!$this->validateId($field['home_id'], 'User')) {
            return false;
        }

        if (!$this->validateId($this->data['Restore']['away_id'], 'User')) {
            return false;
        }

        if ($field['home_id'] == $this->data['Restore']['away_id']) {
            return false;
        }

        return true;
    }

    public function validateGame($field) {
        // This validation can't be done, when stage isn't provided.
        // Validation will generally fail because of this later on.
        if (empty($this->data['Restore']['stage'])) {
            return true;
        }

        $conditions = array(
            'conditions' => array(
                'OR' => array(
                    array(
                        'Restore.home_id' => $field['home_id'],
                        'Restore.away_id' =>
                        $this->data['Restore']['away_id'],
                    ),
                    array(
                        'Restore.away_id' => $field['home_id'],
                        'Restore.home_id' =>
                        $this->data['Restore']['away_id'],
                    )
                ),
                'Restore.tournament_id' =>
                $this->data['Restore']['tournament_id']
            )
        );

        // If submitted game is in group stage.
        if (substr($this->data['Restore']['stage'], 0, 5) == 'Group') {
            $conditions['conditions']['Restore.stage'] =
                    $this->data['Restore']['stage'];
            $gamesPlayed = $this->find('count', $conditions);

            if ($gamesPlayed >= 2) {
                return false;
            }
        } else {
            $allGamesPlayed = $this->find('all', $conditions);
            $playoffGamesPlayed = 0;
            $playoffStages = array('Last Sixteen', 'Quarterfinal',
                'Semifinal', 'Third Place', 'Final');

            foreach ($allGamesPlayed as $gamePlayed) {
                if (in_array($gamePlayed['Restore']['stage'], $playoffStages)) {
                    $playoffGamesPlayed++;
                }
            }

            if ($playoffGamesPlayed >= 1) {
                return false;
            }
        }

        return true;
    }

    public function validateResult($field) {
        if (!in_array($field['score_a'], $this->validScores)
                || !in_array($this->data['Restore']['score_h'], $this->validScores)) {
            return false;
        }



        if ($field['score_a'] == '4'
                || $this->data['Restore']['score_h'] == '4') {
            if ($this->data['Restore']['stage'] != 'Third Place'
                    && $this->data['Restore']['stage'] != 'Final') {
                return false;
            }
        }

        if ($field['score_a'] == $this->data['Restore']['score_h']) {
            return false;
        }

        return true;
    }

}
