<?php
/**
 * GameFixture
 *
 */
class GameFixture extends CakeTestFixture {

/**
 * Fields
 *
 * @var array
 */
	public $fields = array(
		'id' => array('type' => 'integer', 'null' => false, 'default' => null, 'key' => 'primary'),
		'tournament_id' => array('type' => 'integer', 'null' => false, 'default' => null),
		'group_id' => array('type' => 'integer', 'null' => false, 'default' => '0'),
		'playoff_id' => array('type' => 'integer', 'null' => false, 'default' => '0'),
		'home_id' => array('type' => 'integer', 'null' => false, 'default' => null),
		'away_id' => array('type' => 'integer', 'null' => false, 'default' => null),
		'score_h' => array('type' => 'integer', 'null' => false, 'default' => null, 'length' => 4),
		'score_a' => array('type' => 'integer', 'null' => false, 'default' => null, 'length' => 4),
		'techwin' => array('type' => 'boolean', 'null' => false, 'default' => '0'),
		'downloads' => array('type' => 'integer', 'null' => false, 'default' => null, 'length' => 6),
		'created' => array('type' => 'datetime', 'null' => false, 'default' => null),
		'reporter_id' => array('type' => 'integer', 'null' => false, 'default' => null),
		'indexes' => array(
			'PRIMARY' => array('column' => 'id', 'unique' => 1)
		),
		'tableParameters' => array('charset' => 'utf8', 'collate' => 'utf8_bin', 'engine' => 'InnoDB')
	);

/**
 * Records
 *
 * @var array
 */
	public $records = array(
		array(
			'id' => 1,
			'tournament_id' => 1,
			'group_id' => 1,
			'playoff_id' => 1,
			'home_id' => 1,
			'away_id' => 1,
			'score_h' => 1,
			'score_a' => 1,
			'techwin' => 1,
			'downloads' => 1,
			'created' => '2014-12-02 15:39:37',
			'reporter_id' => 1
		),
		array(
			'id' => 2,
			'tournament_id' => 2,
			'group_id' => 2,
			'playoff_id' => 2,
			'home_id' => 2,
			'away_id' => 2,
			'score_h' => 2,
			'score_a' => 2,
			'techwin' => 1,
			'downloads' => 2,
			'created' => '2014-12-02 15:39:37',
			'reporter_id' => 2
		),
		array(
			'id' => 3,
			'tournament_id' => 3,
			'group_id' => 3,
			'playoff_id' => 3,
			'home_id' => 3,
			'away_id' => 3,
			'score_h' => 3,
			'score_a' => 3,
			'techwin' => 1,
			'downloads' => 3,
			'created' => '2014-12-02 15:39:37',
			'reporter_id' => 3
		),
		array(
			'id' => 4,
			'tournament_id' => 4,
			'group_id' => 4,
			'playoff_id' => 4,
			'home_id' => 4,
			'away_id' => 4,
			'score_h' => 4,
			'score_a' => 4,
			'techwin' => 1,
			'downloads' => 4,
			'created' => '2014-12-02 15:39:37',
			'reporter_id' => 4
		),
		array(
			'id' => 5,
			'tournament_id' => 5,
			'group_id' => 5,
			'playoff_id' => 5,
			'home_id' => 5,
			'away_id' => 5,
			'score_h' => 5,
			'score_a' => 5,
			'techwin' => 1,
			'downloads' => 5,
			'created' => '2014-12-02 15:39:37',
			'reporter_id' => 5
		),
		array(
			'id' => 6,
			'tournament_id' => 6,
			'group_id' => 6,
			'playoff_id' => 6,
			'home_id' => 6,
			'away_id' => 6,
			'score_h' => 6,
			'score_a' => 6,
			'techwin' => 1,
			'downloads' => 6,
			'created' => '2014-12-02 15:39:37',
			'reporter_id' => 6
		),
		array(
			'id' => 7,
			'tournament_id' => 7,
			'group_id' => 7,
			'playoff_id' => 7,
			'home_id' => 7,
			'away_id' => 7,
			'score_h' => 7,
			'score_a' => 7,
			'techwin' => 1,
			'downloads' => 7,
			'created' => '2014-12-02 15:39:37',
			'reporter_id' => 7
		),
		array(
			'id' => 8,
			'tournament_id' => 8,
			'group_id' => 8,
			'playoff_id' => 8,
			'home_id' => 8,
			'away_id' => 8,
			'score_h' => 8,
			'score_a' => 8,
			'techwin' => 1,
			'downloads' => 8,
			'created' => '2014-12-02 15:39:37',
			'reporter_id' => 8
		),
		array(
			'id' => 9,
			'tournament_id' => 9,
			'group_id' => 9,
			'playoff_id' => 9,
			'home_id' => 9,
			'away_id' => 9,
			'score_h' => 9,
			'score_a' => 9,
			'techwin' => 1,
			'downloads' => 9,
			'created' => '2014-12-02 15:39:37',
			'reporter_id' => 9
		),
		array(
			'id' => 10,
			'tournament_id' => 10,
			'group_id' => 10,
			'playoff_id' => 10,
			'home_id' => 10,
			'away_id' => 10,
			'score_h' => 10,
			'score_a' => 10,
			'techwin' => 1,
			'downloads' => 10,
			'created' => '2014-12-02 15:39:37',
			'reporter_id' => 10
		),
	);

}
