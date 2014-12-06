<?php
App::uses('Tournament', 'Model');

/**
 * Tournament Test Case
 *
 */
class TournamentTest extends CakeTestCase {

/**
 * Fixtures
 *
 * @var array
 */
	public $fixtures = array(
		'app.tournament',
		'app.application',
		'app.user',
		'app.tournaments_moderator',
		'app.profile',
		'app.stream',
		'app.news',
		'app.group',
		'app.standing',
		'app.game',
		'app.playoff',
		'app.comment',
		'app.rating',
	);

/**
 * setUp method
 *
 * @return void
 */
	public function setUp() {
		parent::setUp();
		$this->Tournament = ClassRegistry::init('Tournament');
		$this->Application = ClassRegistry::init('Application');
		$this->User = ClassRegistry::init('User');
	}

/**
 * tearDown method
 *
 * @return void
 */
	public function tearDown() {
		unset($this->Tournament);
		unset($this->Application);
		unset($this->User);
		parent::tearDown();
	}

/**
 * testAfterPlayoff method
 *
 * @return void
 */
	public function testAfterPlayoff() {
		$currentTournament = $this->Tournament->currentTournament();
		$this->Tournament->afterPlayoff($currentTournament);
		$this->Tournament->id = $currentTournament['Tournament']['id'];
		$tournamentStatus = $this->Tournament->field('status');

		$this->assertEqual($tournamentStatus, Tournament::ARCHIVED,
				'Thet tournament status was not set to ARCHIVED (5).');

		$users = $this->User->find('all');

		foreach ($users as $user) {
			$this->assertEqual($user['User']['stage'], 'retired',
					"Stage for $user[User][username] #$user[User][id] should be retired."); 
		}

		debug('The TimelineSshell which is called by the method is not being tested.');
	}

}
