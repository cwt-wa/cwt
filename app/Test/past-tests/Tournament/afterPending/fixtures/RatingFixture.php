<?php
/**
 * RatingFixture
 *
 */
class RatingFixture extends CakeTestFixture {

/**
 * Fields
 *
 * @var array
 */
	public $fields = array(
		'id' => array('type' => 'integer', 'null' => false, 'default' => null, 'key' => 'primary'),
		'game_id' => array('type' => 'integer', 'null' => false, 'default' => null),
		'likes' => array('type' => 'integer', 'null' => false, 'default' => null, 'length' => 4),
		'dislikes' => array('type' => 'integer', 'null' => false, 'default' => null, 'length' => 4),
		'lightside' => array('type' => 'integer', 'null' => false, 'default' => null, 'length' => 4),
		'darkside' => array('type' => 'integer', 'null' => false, 'default' => null, 'length' => 4),
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
			'game_id' => 1,
			'likes' => 1,
			'dislikes' => 1,
			'lightside' => 1,
			'darkside' => 1
		),
		array(
			'id' => 2,
			'game_id' => 2,
			'likes' => 2,
			'dislikes' => 2,
			'lightside' => 2,
			'darkside' => 2
		),
		array(
			'id' => 3,
			'game_id' => 3,
			'likes' => 3,
			'dislikes' => 3,
			'lightside' => 3,
			'darkside' => 3
		),
		array(
			'id' => 4,
			'game_id' => 4,
			'likes' => 4,
			'dislikes' => 4,
			'lightside' => 4,
			'darkside' => 4
		),
		array(
			'id' => 5,
			'game_id' => 5,
			'likes' => 5,
			'dislikes' => 5,
			'lightside' => 5,
			'darkside' => 5
		),
		array(
			'id' => 6,
			'game_id' => 6,
			'likes' => 6,
			'dislikes' => 6,
			'lightside' => 6,
			'darkside' => 6
		),
		array(
			'id' => 7,
			'game_id' => 7,
			'likes' => 7,
			'dislikes' => 7,
			'lightside' => 7,
			'darkside' => 7
		),
		array(
			'id' => 8,
			'game_id' => 8,
			'likes' => 8,
			'dislikes' => 8,
			'lightside' => 8,
			'darkside' => 8
		),
		array(
			'id' => 9,
			'game_id' => 9,
			'likes' => 9,
			'dislikes' => 9,
			'lightside' => 9,
			'darkside' => 9
		),
		array(
			'id' => 10,
			'game_id' => 10,
			'likes' => 10,
			'dislikes' => 10,
			'lightside' => 10,
			'darkside' => 10
		),
	);

}
