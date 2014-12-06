<?php
/**
 * TraceFixture
 *
 */
class TraceFixture extends CakeTestFixture {

/**
 * Fields
 *
 * @var array
 */
	public $fields = array(
		'id' => array('type' => 'integer', 'null' => false, 'default' => null, 'key' => 'primary'),
		'user_id' => array('type' => 'integer', 'null' => false, 'default' => null),
		'controller' => array('type' => 'string', 'null' => false, 'default' => null, 'length' => 30, 'collate' => 'utf8_bin', 'charset' => 'utf8'),
		'action' => array('type' => 'string', 'null' => false, 'default' => null, 'length' => 20, 'collate' => 'utf8_bin', 'charset' => 'utf8'),
		'additional' => array('type' => 'string', 'null' => false, 'default' => null, 'length' => 100, 'collate' => 'utf8_bin', 'charset' => 'utf8'),
		'created' => array('type' => 'datetime', 'null' => false, 'default' => null),
		'on' => array('type' => 'integer', 'null' => false, 'default' => null),
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
			'user_id' => 1,
			'controller' => 'Lorem ipsum dolor sit amet',
			'action' => 'Lorem ipsum dolor ',
			'additional' => 'Lorem ipsum dolor sit amet',
			'created' => '2014-12-06 16:34:48',
			'on' => 1
		),
		array(
			'id' => 2,
			'user_id' => 2,
			'controller' => 'Lorem ipsum dolor sit amet',
			'action' => 'Lorem ipsum dolor ',
			'additional' => 'Lorem ipsum dolor sit amet',
			'created' => '2014-12-06 16:34:48',
			'on' => 2
		),
		array(
			'id' => 3,
			'user_id' => 3,
			'controller' => 'Lorem ipsum dolor sit amet',
			'action' => 'Lorem ipsum dolor ',
			'additional' => 'Lorem ipsum dolor sit amet',
			'created' => '2014-12-06 16:34:48',
			'on' => 3
		),
		array(
			'id' => 4,
			'user_id' => 4,
			'controller' => 'Lorem ipsum dolor sit amet',
			'action' => 'Lorem ipsum dolor ',
			'additional' => 'Lorem ipsum dolor sit amet',
			'created' => '2014-12-06 16:34:48',
			'on' => 4
		),
		array(
			'id' => 5,
			'user_id' => 5,
			'controller' => 'Lorem ipsum dolor sit amet',
			'action' => 'Lorem ipsum dolor ',
			'additional' => 'Lorem ipsum dolor sit amet',
			'created' => '2014-12-06 16:34:48',
			'on' => 5
		),
		array(
			'id' => 6,
			'user_id' => 6,
			'controller' => 'Lorem ipsum dolor sit amet',
			'action' => 'Lorem ipsum dolor ',
			'additional' => 'Lorem ipsum dolor sit amet',
			'created' => '2014-12-06 16:34:48',
			'on' => 6
		),
		array(
			'id' => 7,
			'user_id' => 7,
			'controller' => 'Lorem ipsum dolor sit amet',
			'action' => 'Lorem ipsum dolor ',
			'additional' => 'Lorem ipsum dolor sit amet',
			'created' => '2014-12-06 16:34:48',
			'on' => 7
		),
		array(
			'id' => 8,
			'user_id' => 8,
			'controller' => 'Lorem ipsum dolor sit amet',
			'action' => 'Lorem ipsum dolor ',
			'additional' => 'Lorem ipsum dolor sit amet',
			'created' => '2014-12-06 16:34:48',
			'on' => 8
		),
		array(
			'id' => 9,
			'user_id' => 9,
			'controller' => 'Lorem ipsum dolor sit amet',
			'action' => 'Lorem ipsum dolor ',
			'additional' => 'Lorem ipsum dolor sit amet',
			'created' => '2014-12-06 16:34:48',
			'on' => 9
		),
		array(
			'id' => 10,
			'user_id' => 10,
			'controller' => 'Lorem ipsum dolor sit amet',
			'action' => 'Lorem ipsum dolor ',
			'additional' => 'Lorem ipsum dolor sit amet',
			'created' => '2014-12-06 16:34:48',
			'on' => 10
		),
	);

}
