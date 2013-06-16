<?php

class TournamentFixture extends CakeTestFixture {

    public $useDbConfig = 'test';
    public $import = array('model' => 'Tournament', 'records' => false);
    public $records = array(
        array(
            'id'        => '2',
            'year'      => '2013',
            'status'    => '5',
            'gold_id'   => '0',
            'silver_id' => '0',
            'bronze_id' => '0',
            'host_id'   => '0'
        ),
        array(
            'id'        => '1',
            'year'      => '2012',
            'status'    => '4',
            'gold_id'   => '0',
            'silver_id' => '0',
            'bronze_id' => '0',
            'host_id'   => '0'
        )
    );
}
