<?php
/**
 * UserFixture
 *
 */
class UserFixture extends CakeTestFixture {

/**
 * Fields
 *
 * @var array
 */
	public $fields = array(
		'id' => array('type' => 'integer', 'null' => false, 'default' => null, 'key' => 'primary'),
		'username' => array('type' => 'string', 'null' => false, 'default' => null, 'length' => 16, 'collate' => 'utf8_general_ci', 'charset' => 'utf8'),
		'password' => array('type' => 'string', 'null' => false, 'default' => null, 'length' => 40, 'collate' => 'utf8_bin', 'charset' => 'utf8'),
		'md5password' => array('type' => 'string', 'null' => false, 'default' => null, 'length' => 100, 'collate' => 'utf8_bin', 'charset' => 'utf8'),
		'admin' => array('type' => 'boolean', 'null' => false, 'default' => null),
		'timeline' => array('type' => 'string', 'null' => false, 'default' => null, 'length' => 50, 'collate' => 'utf8_bin', 'charset' => 'utf8'),
		'participations' => array('type' => 'integer', 'null' => false, 'default' => null),
		'achievements' => array('type' => 'integer', 'null' => false, 'default' => null),
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
			'username' => 'Lorem ipsum do',
			'password' => 'Lorem ipsum dolor sit amet',
			'md5password' => 'Lorem ipsum dolor sit amet',
			'admin' => 1,
			'timeline' => 'Lorem ipsum dolor sit amet',
			'participations' => 1,
			'achievements' => 1,
			'created' => '2014-12-02 15:39:38'
		),
		array(
			'id' => 2,
			'username' => 'Lorem ipsum do',
			'password' => 'Lorem ipsum dolor sit amet',
			'md5password' => 'Lorem ipsum dolor sit amet',
			'admin' => 1,
			'timeline' => 'Lorem ipsum dolor sit amet',
			'participations' => 2,
			'achievements' => 2,
			'created' => '2014-12-02 15:39:38'
		),
		array(
			'id' => 3,
			'username' => 'Lorem ipsum do',
			'password' => 'Lorem ipsum dolor sit amet',
			'md5password' => 'Lorem ipsum dolor sit amet',
			'admin' => 1,
			'timeline' => 'Lorem ipsum dolor sit amet',
			'participations' => 3,
			'achievements' => 3,
			'created' => '2014-12-02 15:39:38'
		),
		array(
			'id' => 4,
			'username' => 'Lorem ipsum do',
			'password' => 'Lorem ipsum dolor sit amet',
			'md5password' => 'Lorem ipsum dolor sit amet',
			'admin' => 1,
			'timeline' => 'Lorem ipsum dolor sit amet',
			'participations' => 4,
			'achievements' => 4,
			'created' => '2014-12-02 15:39:38'
		),
		array(
			'id' => 5,
			'username' => 'Lorem ipsum do',
			'password' => 'Lorem ipsum dolor sit amet',
			'md5password' => 'Lorem ipsum dolor sit amet',
			'admin' => 1,
			'timeline' => 'Lorem ipsum dolor sit amet',
			'participations' => 5,
			'achievements' => 5,
			'created' => '2014-12-02 15:39:38'
		),
		array(
			'id' => 6,
			'username' => 'Lorem ipsum do',
			'password' => 'Lorem ipsum dolor sit amet',
			'md5password' => 'Lorem ipsum dolor sit amet',
			'admin' => 1,
			'timeline' => 'Lorem ipsum dolor sit amet',
			'participations' => 6,
			'achievements' => 6,
			'created' => '2014-12-02 15:39:38'
		),
		array(
			'id' => 7,
			'username' => 'Lorem ipsum do',
			'password' => 'Lorem ipsum dolor sit amet',
			'md5password' => 'Lorem ipsum dolor sit amet',
			'admin' => 1,
			'timeline' => 'Lorem ipsum dolor sit amet',
			'participations' => 7,
			'achievements' => 7,
			'created' => '2014-12-02 15:39:38'
		),
		array(
			'id' => 8,
			'username' => 'Lorem ipsum do',
			'password' => 'Lorem ipsum dolor sit amet',
			'md5password' => 'Lorem ipsum dolor sit amet',
			'admin' => 1,
			'timeline' => 'Lorem ipsum dolor sit amet',
			'participations' => 8,
			'achievements' => 8,
			'created' => '2014-12-02 15:39:38'
		),
		array(
			'id' => 9,
			'username' => 'Lorem ipsum do',
			'password' => 'Lorem ipsum dolor sit amet',
			'md5password' => 'Lorem ipsum dolor sit amet',
			'admin' => 1,
			'timeline' => 'Lorem ipsum dolor sit amet',
			'participations' => 9,
			'achievements' => 9,
			'created' => '2014-12-02 15:39:38'
		),
		array(
			'id' => 10,
			'username' => 'Lorem ipsum do',
			'password' => 'Lorem ipsum dolor sit amet',
			'md5password' => 'Lorem ipsum dolor sit amet',
			'admin' => 1,
			'timeline' => 'Lorem ipsum dolor sit amet',
			'participations' => 10,
			'achievements' => 10,
			'created' => '2014-12-02 15:39:38'
		),
	);

}
