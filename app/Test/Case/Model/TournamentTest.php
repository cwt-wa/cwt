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
		'app.user',
		'app.profile',
		'app.application',
		'app.stream',
		'app.news',
		'app.standing',
		'app.group',
		'app.game',
		'app.playoff',
		'app.comment',
		'app.rating',
		'app.infoboard',
		'app.rule',
		'app.trace',
		'app.schedule',
		'app.restore',
		'app.tournaments_moderator'
	);

/**
 * setUp method
 *
 * @return void
 */
	public function setUp() {
		parent::setUp();
		$this->Tournament = ClassRegistry::init('Tournament');
	}

/**
 * tearDown method
 *
 * @return void
 */
	public function tearDown() {
		unset($this->Tournament);

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
