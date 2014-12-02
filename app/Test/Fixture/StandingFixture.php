<?php
/**
 * StandingFixture
 *
 */
class StandingFixture extends CakeTestFixture {

/**
 * Fields
 *
 * @var array
 */
	public $fields = array(
		'id' => array('type' => 'integer', 'null' => false, 'default' => null, 'key' => 'primary'),
		'group_id' => array('type' => 'integer', 'null' => false, 'default' => null),
		'user_id' => array('type' => 'integer', 'null' => false, 'default' => null),
		'points' => array('type' => 'integer', 'null' => false, 'default' => null),
		'games' => array('type' => 'integer', 'null' => false, 'default' => null),
		'game_ratio' => array('type' => 'integer', 'null' => false, 'default' => null),
		'round_ratio' => array('type' => 'integer', 'null' => false, 'default' => null),
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
			'group_id' => 1,
			'user_id' => 1,
			'points' => 1,
			'games' => 1,
			'game_ratio' => 1,
			'round_ratio' => 1
		),
		array(
			'id' => 2,
			'group_id' => 2,
			'user_id' => 2,
			'points' => 2,
			'games' => 2,
			'game_ratio' => 2,
			'round_ratio' => 2
		),
		array(
			'id' => 3,
			'group_id' => 3,
			'user_id' => 3,
			'points' => 3,
			'games' => 3,
			'game_ratio' => 3,
			'round_ratio' => 3
		),
		array(
			'id' => 4,
			'group_id' => 4,
			'user_id' => 4,
			'points' => 4,
			'games' => 4,
			'game_ratio' => 4,
			'round_ratio' => 4
		),
		array(
			'id' => 5,
			'group_id' => 5,
			'user_id' => 5,
			'points' => 5,
			'games' => 5,
			'game_ratio' => 5,
			'round_ratio' => 5
		),
		array(
			'id' => 6,
			'group_id' => 6,
			'user_id' => 6,
			'points' => 6,
			'games' => 6,
			'game_ratio' => 6,
			'round_ratio' => 6
		),
		array(
			'id' => 7,
			'group_id' => 7,
			'user_id' => 7,
			'points' => 7,
			'games' => 7,
			'game_ratio' => 7,
			'round_ratio' => 7
		),
		array(
			'id' => 8,
			'group_id' => 8,
			'user_id' => 8,
			'points' => 8,
			'games' => 8,
			'game_ratio' => 8,
			'round_ratio' => 8
		),
		array(
			'id' => 9,
			'group_id' => 9,
			'user_id' => 9,
			'points' => 9,
			'games' => 9,
			'game_ratio' => 9,
			'round_ratio' => 9
		),
		array(
			'id' => 10,
			'group_id' => 10,
			'user_id' => 10,
			'points' => 10,
			'games' => 10,
			'game_ratio' => 10,
			'round_ratio' => 10
		),
	);

}
