<?php
/**
 * InfoboardFixture
 *
 */
class InfoboardFixture extends CakeTestFixture {

/**
 * Fields
 *
 * @var array
 */
	public $fields = array(
		'id' => array('type' => 'integer', 'null' => false, 'default' => null, 'key' => 'primary'),
		'message' => array('type' => 'string', 'null' => false, 'default' => null, 'length' => 500, 'collate' => 'utf8_bin', 'charset' => 'utf8'),
		'user_id' => array('type' => 'integer', 'null' => false, 'default' => null),
		'guest' => array('type' => 'string', 'null' => false, 'default' => null, 'length' => 16, 'collate' => 'utf8_bin', 'charset' => 'utf8'),
		'created' => array('type' => 'datetime', 'null' => false, 'default' => null),
		'category' => array('type' => 'integer', 'null' => false, 'default' => null, 'length' => 4),
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
			'message' => 'Lorem ipsum dolor sit amet',
			'user_id' => 1,
			'guest' => 'Lorem ipsum do',
			'created' => '2014-12-02 15:39:37',
			'category' => 1
		),
		array(
			'id' => 2,
			'message' => 'Lorem ipsum dolor sit amet',
			'user_id' => 2,
			'guest' => 'Lorem ipsum do',
			'created' => '2014-12-02 15:39:37',
			'category' => 2
		),
		array(
			'id' => 3,
			'message' => 'Lorem ipsum dolor sit amet',
			'user_id' => 3,
			'guest' => 'Lorem ipsum do',
			'created' => '2014-12-02 15:39:37',
			'category' => 3
		),
		array(
			'id' => 4,
			'message' => 'Lorem ipsum dolor sit amet',
			'user_id' => 4,
			'guest' => 'Lorem ipsum do',
			'created' => '2014-12-02 15:39:37',
			'category' => 4
		),
		array(
			'id' => 5,
			'message' => 'Lorem ipsum dolor sit amet',
			'user_id' => 5,
			'guest' => 'Lorem ipsum do',
			'created' => '2014-12-02 15:39:37',
			'category' => 5
		),
		array(
			'id' => 6,
			'message' => 'Lorem ipsum dolor sit amet',
			'user_id' => 6,
			'guest' => 'Lorem ipsum do',
			'created' => '2014-12-02 15:39:37',
			'category' => 6
		),
		array(
			'id' => 7,
			'message' => 'Lorem ipsum dolor sit amet',
			'user_id' => 7,
			'guest' => 'Lorem ipsum do',
			'created' => '2014-12-02 15:39:37',
			'category' => 7
		),
		array(
			'id' => 8,
			'message' => 'Lorem ipsum dolor sit amet',
			'user_id' => 8,
			'guest' => 'Lorem ipsum do',
			'created' => '2014-12-02 15:39:37',
			'category' => 8
		),
		array(
			'id' => 9,
			'message' => 'Lorem ipsum dolor sit amet',
			'user_id' => 9,
			'guest' => 'Lorem ipsum do',
			'created' => '2014-12-02 15:39:37',
			'category' => 9
		),
		array(
			'id' => 10,
			'message' => 'Lorem ipsum dolor sit amet',
			'user_id' => 10,
			'guest' => 'Lorem ipsum do',
			'created' => '2014-12-02 15:39:37',
			'category' => 10
		),
	);

}
