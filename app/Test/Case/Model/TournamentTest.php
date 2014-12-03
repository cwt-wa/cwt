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
		debug($this->Tournament->find('all'));
		debug($this->Application->find('all'));
		debug($this->User->find('all'));
		debug($this->Group->find('all'));
		debug($this->Standing->find('all'));
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
