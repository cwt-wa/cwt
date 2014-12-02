<?php
/**
 * NewsFixture
 *
 */
class NewsFixture extends CakeTestFixture {

/**
 * Fields
 *
 * @var array
 */
	public $fields = array(
		'id' => array('type' => 'integer', 'null' => false, 'default' => null, 'key' => 'primary'),
		'user_id' => array('type' => 'integer', 'null' => false, 'default' => null),
		'text' => array('type' => 'binary', 'null' => false, 'default' => null),
		'preview' => array('type' => 'binary', 'null' => false, 'default' => null),
		'created' => array('type' => 'datetime', 'null' => false, 'default' => null),
		'modified' => array('type' => 'datetime', 'null' => false, 'default' => null),
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
			'user_id' => 1,
			'text' => 'Lorem ipsum dolor sit amet',
			'preview' => 'Lorem ipsum dolor sit amet',
			'created' => '2014-12-02 15:39:37',
			'modified' => '2014-12-02 15:39:37'
		),
		array(
			'id' => 2,
			'user_id' => 2,
			'text' => 'Lorem ipsum dolor sit amet',
			'preview' => 'Lorem ipsum dolor sit amet',
			'created' => '2014-12-02 15:39:37',
			'modified' => '2014-12-02 15:39:37'
		),
		array(
			'id' => 3,
			'user_id' => 3,
			'text' => 'Lorem ipsum dolor sit amet',
			'preview' => 'Lorem ipsum dolor sit amet',
			'created' => '2014-12-02 15:39:37',
			'modified' => '2014-12-02 15:39:37'
		),
		array(
			'id' => 4,
			'user_id' => 4,
			'text' => 'Lorem ipsum dolor sit amet',
			'preview' => 'Lorem ipsum dolor sit amet',
			'created' => '2014-12-02 15:39:37',
			'modified' => '2014-12-02 15:39:37'
		),
		array(
			'id' => 5,
			'user_id' => 5,
			'text' => 'Lorem ipsum dolor sit amet',
			'preview' => 'Lorem ipsum dolor sit amet',
			'created' => '2014-12-02 15:39:37',
			'modified' => '2014-12-02 15:39:37'
		),
		array(
			'id' => 6,
			'user_id' => 6,
			'text' => 'Lorem ipsum dolor sit amet',
			'preview' => 'Lorem ipsum dolor sit amet',
			'created' => '2014-12-02 15:39:37',
			'modified' => '2014-12-02 15:39:37'
		),
		array(
			'id' => 7,
			'user_id' => 7,
			'text' => 'Lorem ipsum dolor sit amet',
			'preview' => 'Lorem ipsum dolor sit amet',
			'created' => '2014-12-02 15:39:37',
			'modified' => '2014-12-02 15:39:37'
		),
		array(
			'id' => 8,
			'user_id' => 8,
			'text' => 'Lorem ipsum dolor sit amet',
			'preview' => 'Lorem ipsum dolor sit amet',
			'created' => '2014-12-02 15:39:37',
			'modified' => '2014-12-02 15:39:37'
		),
		array(
			'id' => 9,
			'user_id' => 9,
			'text' => 'Lorem ipsum dolor sit amet',
			'preview' => 'Lorem ipsum dolor sit amet',
			'created' => '2014-12-02 15:39:37',
			'modified' => '2014-12-02 15:39:37'
		),
		array(
			'id' => 10,
			'user_id' => 10,
			'text' => 'Lorem ipsum dolor sit amet',
			'preview' => 'Lorem ipsum dolor sit amet',
			'created' => '2014-12-02 15:39:37',
			'modified' => '2014-12-02 15:39:37'
		),
	);

}
