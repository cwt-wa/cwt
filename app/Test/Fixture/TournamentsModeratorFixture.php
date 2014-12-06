<?php
/**
 * TournamentsModeratorFixture
 *
 */
class TournamentsModeratorFixture extends CakeTestFixture {

/**
 * Fields
 *
 * @var array
 */
	public $fields = array(
		'id' => array('type' => 'integer', 'null' => false, 'default' => null, 'key' => 'primary'),
		'tournament_id' => array('type' => 'integer', 'null' => false, 'default' => null),
		'moderator_id' => array('type' => 'integer', 'null' => false, 'default' => null),
		'indexes' => array(
			'PRIMARY' => array('column' => 'id', 'unique' => 1)
		),
		'tableParameters' => array('charset' => 'utf8', 'collate' => 'utf8_general_ci', 'engine' => 'MyISAM')
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
			'moderator_id' => 1
		),
		array(
			'id' => 2,
			'tournament_id' => 2,
			'moderator_id' => 2
		),
		array(
			'id' => 3,
			'tournament_id' => 3,
			'moderator_id' => 3
		),
		array(
			'id' => 4,
			'tournament_id' => 4,
			'moderator_id' => 4
		),
		array(
			'id' => 5,
			'tournament_id' => 5,
			'moderator_id' => 5
		),
		array(
			'id' => 6,
			'tournament_id' => 6,
			'moderator_id' => 6
		),
		array(
			'id' => 7,
			'tournament_id' => 7,
			'moderator_id' => 7
		),
		array(
			'id' => 8,
			'tournament_id' => 8,
			'moderator_id' => 8
		),
		array(
			'id' => 9,
			'tournament_id' => 9,
			'moderator_id' => 9
		),
		array(
			'id' => 10,
			'tournament_id' => 10,
			'moderator_id' => 10
		),
	);

}
