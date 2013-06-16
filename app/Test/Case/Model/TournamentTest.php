<?php

App::uses('Tournament', 'Model');

class TournamentTest extends CakeTestCase {

    public $fixtures = array('app.User', 'app.Tournament', 'app.TournamentsModerator');

    public function setUp() {
        parent::setUp();
        $this->Tournament = ClassRegistry::init('Tournament');
    }

    public function testCurrentTournament() {
        debug($this->Tournament->genCopyrightString());
    }
}
