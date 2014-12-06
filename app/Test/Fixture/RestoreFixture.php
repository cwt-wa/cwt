<?php
/**
 * RestoreFixture
 *
 */
class RestoreFixture extends CakeTestFixture {

/**
 * Fields
 *
 * @var array
 */
	public $fields = array(
		'id' => array('type' => 'integer', 'null' => false, 'default' => null, 'key' => 'primary'),
		'submitter_id' => array('type' => 'integer', 'null' => false, 'default' => null),
		'tournament_id' => array('type' => 'integer', 'null' => false, 'default' => null),
		'home_id' => array('type' => 'integer', 'null' => false, 'default' => null),
		'away_id' => array('type' => 'integer', 'null' => false, 'default' => null),
		'score_h' => array('type' => 'integer', 'null' => false, 'default' => null, 'length' => 4),
		'score_a' => array('type' => 'integer', 'null' => false, 'default' => null, 'length' => 4),
		'tech_win' => array('type' => 'boolean', 'null' => false, 'default' => '0'),
		'stage' => array('type' => 'string', 'null' => false, 'default' => null, 'length' => 20, 'collate' => 'utf8_general_ci', 'charset' => 'utf8'),
		'reported' => array('type' => 'datetime', 'null' => false, 'default' => null),
		'reporter_id' => array('type' => 'integer', 'null' => false, 'default' => null),
		'created' => array('type' => 'datetime', 'null' => false, 'default' => null),
		'indexes' => array(
			'PRIMARY' => array('column' => 'id', 'unique' => 1)
		),
		'tableParameters' => array('charset' => 'utf8', 'collate' => 'utf8_general_ci', 'engine' => 'InnoDB')
	);

/**
 * Records
 *
 * @var array
 */
	public $records = array(
		array(
			'id' => 1,
			'submitter_id' => 1,
			'tournament_id' => 1,
			'home_id' => 1,
			'away_id' => 1,
			'score_h' => 1,
			'score_a' => 1,
			'tech_win' => 1,
			'stage' => 'Lorem ipsum dolor ',
			'reported' => '2014-12-06 16:34:47',
			'reporter_id' => 1,
			'created' => '2014-12-06 16:34:47'
		),
		array(
			'id' => 2,
			'submitter_id' => 2,
			'tournament_id' => 2,
			'home_id' => 2,
			'away_id' => 2,
			'score_h' => 2,
			'score_a' => 2,
			'tech_win' => 1,
			'stage' => 'Lorem ipsum dolor ',
			'reported' => '2014-12-06 16:34:47',
			'reporter_id' => 2,
			'created' => '2014-12-06 16:34:47'
		),
		array(
			'id' => 3,
			'submitter_id' => 3,
			'tournament_id' => 3,
			'home_id' => 3,
			'away_id' => 3,
			'score_h' => 3,
			'score_a' => 3,
			'tech_win' => 1,
			'stage' => 'Lorem ipsum dolor ',
			'reported' => '2014-12-06 16:34:47',
			'reporter_id' => 3,
			'created' => '2014-12-06 16:34:47'
		),
		array(
			'id' => 4,
			'submitter_id' => 4,
			'tournament_id' => 4,
			'home_id' => 4,
			'away_id' => 4,
			'score_h' => 4,
			'score_a' => 4,
			'tech_win' => 1,
			'stage' => 'Lorem ipsum dolor ',
			'reported' => '2014-12-06 16:34:47',
			'reporter_id' => 4,
			'created' => '2014-12-06 16:34:47'
		),
		array(
			'id' => 5,
			'submitter_id' => 5,
			'tournament_id' => 5,
			'home_id' => 5,
			'away_id' => 5,
			'score_h' => 5,
			'score_a' => 5,
			'tech_win' => 1,
			'stage' => 'Lorem ipsum dolor ',
			'reported' => '2014-12-06 16:34:47',
			'reporter_id' => 5,
			'created' => '2014-12-06 16:34:47'
		),
		array(
			'id' => 6,
			'submitter_id' => 6,
			'tournament_id' => 6,
			'home_id' => 6,
			'away_id' => 6,
			'score_h' => 6,
			'score_a' => 6,
			'tech_win' => 1,
			'stage' => 'Lorem ipsum dolor ',
			'reported' => '2014-12-06 16:34:47',
			'reporter_id' => 6,
			'created' => '2014-12-06 16:34:47'
		),
		array(
			'id' => 7,
			'submitter_id' => 7,
			'tournament_id' => 7,
			'home_id' => 7,
			'away_id' => 7,
			'score_h' => 7,
			'score_a' => 7,
			'tech_win' => 1,
			'stage' => 'Lorem ipsum dolor ',
			'reported' => '2014-12-06 16:34:47',
			'reporter_id' => 7,
			'created' => '2014-12-06 16:34:47'
		),
		array(
			'id' => 8,
			'submitter_id' => 8,
			'tournament_id' => 8,
			'home_id' => 8,
			'away_id' => 8,
			'score_h' => 8,
			'score_a' => 8,
			'tech_win' => 1,
			'stage' => 'Lorem ipsum dolor ',
			'reported' => '2014-12-06 16:34:47',
			'reporter_id' => 8,
			'created' => '2014-12-06 16:34:47'
		),
		array(
			'id' => 9,
			'submitter_id' => 9,
			'tournament_id' => 9,
			'home_id' => 9,
			'away_id' => 9,
			'score_h' => 9,
			'score_a' => 9,
			'tech_win' => 1,
			'stage' => 'Lorem ipsum dolor ',
			'reported' => '2014-12-06 16:34:47',
			'reporter_id' => 9,
			'created' => '2014-12-06 16:34:47'
		),
		array(
			'id' => 10,
			'submitter_id' => 10,
			'tournament_id' => 10,
			'home_id' => 10,
			'away_id' => 10,
			'score_h' => 10,
			'score_a' => 10,
			'tech_win' => 1,
			'stage' => 'Lorem ipsum dolor ',
			'reported' => '2014-12-06 16:34:47',
			'reporter_id' => 10,
			'created' => '2014-12-06 16:34:47'
		),
	);

}
