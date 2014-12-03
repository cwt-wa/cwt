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
		'app.group',
		'app.standing',
		'app.tournaments_moderator',
		'app.profile',
		'app.stream',
		'app.news',
		'app.game',
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
		$this->Group = ClassRegistry::init('Group');
		$this->Standing = ClassRegistry::init('Standing');
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
		unset($this->Group);
		unset($this->Standing);

		parent::tearDown();
	}

/**
 * testStart method
 *
 * @return void
 */
	public function testStart() {
	}

/**
 * testNiceStaff method
 *
 * @return void
 */
	public function testNiceStaff() {
	}

/**
 * testNext method
 *
 * @return void
 */
	public function testNext() {
	}

/**
 * testAfterPending method
 *
 * @return void
 */
	public function testAfterPending() {
		$this->Tournament->afterPending($this->Tournament->currentTournament());

		$users = $this->User->find('all');

		for ($i = 0; $i < count($users); $i++) {
			if ($i < 32) {
				$this->assertEqual($users[$i]['User']['stage'], 'group',
					"User #$users[$i][User][id] should be in group.");
			} else {
				$this->assertEqual($users[$i]['User']['stage'], 'retired',
					"User #$users[$i][User][id] should be retired.");
			}
		}
	}

/**
 * testFindUsersInCurrentGroupStage method
 *
 * @return void
 */
	public function testFindUsersInCurrentGroupStage() {
	}

/**
 * testAfterGroup method
 *
 * @return void
 */
	public function testAfterGroup() {
	}

}
