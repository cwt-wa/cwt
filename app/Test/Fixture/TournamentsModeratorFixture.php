<?php

class TournamentsModeratorFixture extends CakeTestFixture {

    public $import = array('table' => 'tournaments_moderators', 'records' => false);
    public $records = array(
        array(
            'tournament_id'    => 1,
            'moderator_id'     => 1,
        ),
        array(
            'tournament_id'    => 1,
            'moderator_id'     => 2,
        ),
    );

}
