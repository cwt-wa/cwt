<?php

App::uses('TournamentsController', 'Controller');

class TournamentsControllerTest extends ControllerTestCase {

    public $fixtures = array('app.User', 'app.Tournament', 'app.TournamentsModerator');

    public function testGenCopyrightString() {
        debug($this->testAction('/tournaments/genCopyrightString'));
    }
}
