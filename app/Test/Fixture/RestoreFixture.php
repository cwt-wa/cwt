<?php

class RestoreFixture extends CakeTestFixture {
    
    public $import = 'Restore';
    public $records = array(
        array(
            'id' => 1,
            'submitter_id' => 1,
            'tournament_id' => 3,
            'home_id' => 10,
            'away_id' => 11,
            'score_h' => 3,
            'score_a' => 2,
            'stage' => 'Group A',
            'reported' => '2010-11-26',
            'created' => '2013-01-19'
        ),
        array(
            'id' => 2,
            'submitter_id' => 1,
            'tournament_id' => 3,
            'home_id' => 11,
            'away_id' => 10,
            'score_h' => 3,
            'score_a' => 2,
            'stage' => 'Group A',
            'reported' => '2010-11-26',
            'created' => '2013-01-19'
        ),
        
        array(
            'id' => 3,
            'submitter_id' => 1,
            'tournament_id' => 3,
            'home_id' => 72,
            'away_id' => 73,
            'score_h' => 3,
            'score_a' => 2,
            'stage' => 'Group F',
            'reported' => '2010-11-26',
            'created' => '2013-01-19'
        ),
        
        array(
            'id' => 4,
            'submitter_id' => 1,
            'tournament_id' => 3,
            'home_id' => 100,
            'away_id' => 101,
            'score_h' => 1,
            'score_a' => 3,
            'stage' => 'Last Sixteen',
            'reported' => '2010-11-26',
            'created' => '2013-01-19'
        )
    );
    
}
