<?php
/**
 * ScheduleFixture
 *
 */
class ScheduleFixture extends CakeTestFixture {

/**
 * Fields
 *
 * @var array
 */
	public $fields = array(
		'id' => array('type' => 'integer', 'null' => false, 'default' => null, 'key' => 'primary'),
		'home_id' => array('type' => 'integer', 'null' => false, 'default' => null),
		'away_id' => array('type' => 'integer', 'null' => false, 'default' => null),
		'stream_id' => array('type' => 'string', 'null' => false, 'default' => null, 'length' => 20, 'collate' => 'utf8_general_ci', 'charset' => 'utf8'),
		'when' => array('type' => 'datetime', 'null' => false, 'default' => null),
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
			'home_id' => 1,
			'away_id' => 1,
			'stream_id' => 'Lorem ipsum dolor ',
			'when' => '2014-12-02 15:39:38',
			'created' => '2014-12-02 15:39:38'
		),
		array(
			'id' => 2,
			'home_id' => 2,
			'away_id' => 2,
			'stream_id' => 'Lorem ipsum dolor ',
			'when' => '2014-12-02 15:39:38',
			'created' => '2014-12-02 15:39:38'
		),
		array(
			'id' => 3,
			'home_id' => 3,
			'away_id' => 3,
			'stream_id' => 'Lorem ipsum dolor ',
			'when' => '2014-12-02 15:39:38',
			'created' => '2014-12-02 15:39:38'
		),
		array(
			'id' => 4,
			'home_id' => 4,
			'away_id' => 4,
			'stream_id' => 'Lorem ipsum dolor ',
			'when' => '2014-12-02 15:39:38',
			'created' => '2014-12-02 15:39:38'
		),
		array(
			'id' => 5,
			'home_id' => 5,
			'away_id' => 5,
			'stream_id' => 'Lorem ipsum dolor ',
			'when' => '2014-12-02 15:39:38',
			'created' => '2014-12-02 15:39:38'
		),
		array(
			'id' => 6,
			'home_id' => 6,
			'away_id' => 6,
			'stream_id' => 'Lorem ipsum dolor ',
			'when' => '2014-12-02 15:39:38',
			'created' => '2014-12-02 15:39:38'
		),
		array(
			'id' => 7,
			'home_id' => 7,
			'away_id' => 7,
			'stream_id' => 'Lorem ipsum dolor ',
			'when' => '2014-12-02 15:39:38',
			'created' => '2014-12-02 15:39:38'
		),
		array(
			'id' => 8,
			'home_id' => 8,
			'away_id' => 8,
			'stream_id' => 'Lorem ipsum dolor ',
			'when' => '2014-12-02 15:39:38',
			'created' => '2014-12-02 15:39:38'
		),
		array(
			'id' => 9,
			'home_id' => 9,
			'away_id' => 9,
			'stream_id' => 'Lorem ipsum dolor ',
			'when' => '2014-12-02 15:39:38',
			'created' => '2014-12-02 15:39:38'
		),
		array(
			'id' => 10,
			'home_id' => 10,
			'away_id' => 10,
			'stream_id' => 'Lorem ipsum dolor ',
			'when' => '2014-12-02 15:39:38',
			'created' => '2014-12-02 15:39:38'
		),
	);

}
