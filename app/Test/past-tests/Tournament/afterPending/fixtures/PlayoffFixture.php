<?php
/**
 * PlayoffFixture
 *
 */
class PlayoffFixture extends CakeTestFixture {

/**
 * Fields
 *
 * @var array
 */
	public $fields = array(
		'id' => array('type' => 'integer', 'null' => false, 'default' => null, 'key' => 'primary'),
		'stepAssoc' => array('type' => 'string', 'null' => false, 'default' => null, 'length' => 15, 'collate' => 'utf8_bin', 'charset' => 'utf8'),
		'step' => array('type' => 'integer', 'null' => false, 'default' => null),
		'spot' => array('type' => 'integer', 'null' => false, 'default' => null),
		'bet_h' => array('type' => 'integer', 'null' => false, 'default' => null, 'length' => 6),
		'bet_a' => array('type' => 'integer', 'null' => false, 'default' => null, 'length' => 6),
		'game_id' => array('type' => 'integer', 'null' => false, 'default' => null),
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
			'stepAssoc' => 'Lorem ipsum d',
			'step' => 1,
			'spot' => 1,
			'bet_h' => 1,
			'bet_a' => 1,
			'game_id' => 1
		),
		array(
			'id' => 2,
			'stepAssoc' => 'Lorem ipsum d',
			'step' => 2,
			'spot' => 2,
			'bet_h' => 2,
			'bet_a' => 2,
			'game_id' => 2
		),
		array(
			'id' => 3,
			'stepAssoc' => 'Lorem ipsum d',
			'step' => 3,
			'spot' => 3,
			'bet_h' => 3,
			'bet_a' => 3,
			'game_id' => 3
		),
		array(
			'id' => 4,
			'stepAssoc' => 'Lorem ipsum d',
			'step' => 4,
			'spot' => 4,
			'bet_h' => 4,
			'bet_a' => 4,
			'game_id' => 4
		),
		array(
			'id' => 5,
			'stepAssoc' => 'Lorem ipsum d',
			'step' => 5,
			'spot' => 5,
			'bet_h' => 5,
			'bet_a' => 5,
			'game_id' => 5
		),
		array(
			'id' => 6,
			'stepAssoc' => 'Lorem ipsum d',
			'step' => 6,
			'spot' => 6,
			'bet_h' => 6,
			'bet_a' => 6,
			'game_id' => 6
		),
		array(
			'id' => 7,
			'stepAssoc' => 'Lorem ipsum d',
			'step' => 7,
			'spot' => 7,
			'bet_h' => 7,
			'bet_a' => 7,
			'game_id' => 7
		),
		array(
			'id' => 8,
			'stepAssoc' => 'Lorem ipsum d',
			'step' => 8,
			'spot' => 8,
			'bet_h' => 8,
			'bet_a' => 8,
			'game_id' => 8
		),
		array(
			'id' => 9,
			'stepAssoc' => 'Lorem ipsum d',
			'step' => 9,
			'spot' => 9,
			'bet_h' => 9,
			'bet_a' => 9,
			'game_id' => 9
		),
		array(
			'id' => 10,
			'stepAssoc' => 'Lorem ipsum d',
			'step' => 10,
			'spot' => 10,
			'bet_h' => 10,
			'bet_a' => 10,
			'game_id' => 10
		),
	);

}
