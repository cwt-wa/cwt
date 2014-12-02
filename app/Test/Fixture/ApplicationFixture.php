<?php
/**
 * ApplicationFixture
 *
 */
class ApplicationFixture extends CakeTestFixture {

/**
 * Fields
 *
 * @var array
 */
	public $fields = array(
		'id' => array('type' => 'integer', 'null' => false, 'default' => null, 'key' => 'primary'),
		'user_id' => array('type' => 'integer', 'null' => false, 'default' => null),
		'created' => array('type' => 'datetime', 'null' => false, 'default' => null),
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
			'created' => '2014-12-02 15:39:37'
		),
		array(
			'id' => 2,
			'user_id' => 2,
			'created' => '2014-12-02 15:39:37'
		),
		array(
			'id' => 3,
			'user_id' => 3,
			'created' => '2014-12-02 15:39:37'
		),
		array(
			'id' => 4,
			'user_id' => 4,
			'created' => '2014-12-02 15:39:37'
		),
		array(
			'id' => 5,
			'user_id' => 5,
			'created' => '2014-12-02 15:39:37'
		),
		array(
			'id' => 6,
			'user_id' => 6,
			'created' => '2014-12-02 15:39:37'
		),
		array(
			'id' => 7,
			'user_id' => 7,
			'created' => '2014-12-02 15:39:37'
		),
		array(
			'id' => 8,
			'user_id' => 8,
			'created' => '2014-12-02 15:39:37'
		),
		array(
			'id' => 9,
			'user_id' => 9,
			'created' => '2014-12-02 15:39:37'
		),
		array(
			'id' => 10,
			'user_id' => 10,
			'created' => '2014-12-02 15:39:37'
		),
	);

}
