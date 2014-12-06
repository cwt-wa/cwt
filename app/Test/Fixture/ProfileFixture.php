<?php
/**
 * ProfileFixture
 *
 */
class ProfileFixture extends CakeTestFixture {

/**
 * Fields
 *
 * @var array
 */
	public $fields = array(
		'id' => array('type' => 'integer', 'null' => false, 'default' => null, 'key' => 'primary'),
		'user_id' => array('type' => 'integer', 'null' => false, 'default' => null),
		'modified' => array('type' => 'datetime', 'null' => false, 'default' => null),
		'country' => array('type' => 'string', 'null' => false, 'default' => 'Unknown', 'length' => 100, 'collate' => 'utf8_bin', 'charset' => 'utf8'),
		'clan' => array('type' => 'string', 'null' => false, 'default' => null, 'length' => 4, 'collate' => 'utf8_bin', 'charset' => 'utf8'),
		'email' => array('type' => 'string', 'null' => false, 'default' => null, 'length' => 100, 'collate' => 'utf8_bin', 'charset' => 'utf8'),
		'skype' => array('type' => 'string', 'null' => false, 'default' => null, 'length' => 100, 'collate' => 'utf8_bin', 'charset' => 'utf8'),
		'icq' => array('type' => 'string', 'null' => false, 'default' => null, 'length' => 20, 'collate' => 'utf8_bin', 'charset' => 'utf8'),
		'facebook' => array('type' => 'string', 'null' => false, 'default' => null, 'length' => 200, 'collate' => 'utf8_bin', 'charset' => 'utf8'),
		'googlep' => array('type' => 'string', 'null' => false, 'default' => null, 'length' => 200, 'collate' => 'utf8_bin', 'charset' => 'utf8'),
		'twitter' => array('type' => 'string', 'null' => false, 'default' => null, 'length' => 200, 'collate' => 'utf8_bin', 'charset' => 'utf8'),
		'about' => array('type' => 'text', 'null' => false, 'default' => null, 'collate' => 'utf8_bin', 'charset' => 'utf8'),
		'hideProfile' => array('type' => 'boolean', 'null' => false, 'default' => null),
		'hideEmail' => array('type' => 'boolean', 'null' => false, 'default' => null),
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
			'modified' => '2014-12-06 16:34:47',
			'country' => 'Lorem ipsum dolor sit amet',
			'clan' => 'Lo',
			'email' => 'Lorem ipsum dolor sit amet',
			'skype' => 'Lorem ipsum dolor sit amet',
			'icq' => 'Lorem ipsum dolor ',
			'facebook' => 'Lorem ipsum dolor sit amet',
			'googlep' => 'Lorem ipsum dolor sit amet',
			'twitter' => 'Lorem ipsum dolor sit amet',
			'about' => 'Lorem ipsum dolor sit amet, aliquet feugiat. Convallis morbi fringilla gravida, phasellus feugiat dapibus velit nunc, pulvinar eget sollicitudin venenatis cum nullam, vivamus ut a sed, mollitia lectus. Nulla vestibulum massa neque ut et, id hendrerit sit, feugiat in taciti enim proin nibh, tempor dignissim, rhoncus duis vestibulum nunc mattis convallis.',
			'hideProfile' => 1,
			'hideEmail' => 1
		),
		array(
			'id' => 2,
			'user_id' => 2,
			'modified' => '2014-12-06 16:34:47',
			'country' => 'Lorem ipsum dolor sit amet',
			'clan' => 'Lo',
			'email' => 'Lorem ipsum dolor sit amet',
			'skype' => 'Lorem ipsum dolor sit amet',
			'icq' => 'Lorem ipsum dolor ',
			'facebook' => 'Lorem ipsum dolor sit amet',
			'googlep' => 'Lorem ipsum dolor sit amet',
			'twitter' => 'Lorem ipsum dolor sit amet',
			'about' => 'Lorem ipsum dolor sit amet, aliquet feugiat. Convallis morbi fringilla gravida, phasellus feugiat dapibus velit nunc, pulvinar eget sollicitudin venenatis cum nullam, vivamus ut a sed, mollitia lectus. Nulla vestibulum massa neque ut et, id hendrerit sit, feugiat in taciti enim proin nibh, tempor dignissim, rhoncus duis vestibulum nunc mattis convallis.',
			'hideProfile' => 1,
			'hideEmail' => 1
		),
		array(
			'id' => 3,
			'user_id' => 3,
			'modified' => '2014-12-06 16:34:47',
			'country' => 'Lorem ipsum dolor sit amet',
			'clan' => 'Lo',
			'email' => 'Lorem ipsum dolor sit amet',
			'skype' => 'Lorem ipsum dolor sit amet',
			'icq' => 'Lorem ipsum dolor ',
			'facebook' => 'Lorem ipsum dolor sit amet',
			'googlep' => 'Lorem ipsum dolor sit amet',
			'twitter' => 'Lorem ipsum dolor sit amet',
			'about' => 'Lorem ipsum dolor sit amet, aliquet feugiat. Convallis morbi fringilla gravida, phasellus feugiat dapibus velit nunc, pulvinar eget sollicitudin venenatis cum nullam, vivamus ut a sed, mollitia lectus. Nulla vestibulum massa neque ut et, id hendrerit sit, feugiat in taciti enim proin nibh, tempor dignissim, rhoncus duis vestibulum nunc mattis convallis.',
			'hideProfile' => 1,
			'hideEmail' => 1
		),
		array(
			'id' => 4,
			'user_id' => 4,
			'modified' => '2014-12-06 16:34:47',
			'country' => 'Lorem ipsum dolor sit amet',
			'clan' => 'Lo',
			'email' => 'Lorem ipsum dolor sit amet',
			'skype' => 'Lorem ipsum dolor sit amet',
			'icq' => 'Lorem ipsum dolor ',
			'facebook' => 'Lorem ipsum dolor sit amet',
			'googlep' => 'Lorem ipsum dolor sit amet',
			'twitter' => 'Lorem ipsum dolor sit amet',
			'about' => 'Lorem ipsum dolor sit amet, aliquet feugiat. Convallis morbi fringilla gravida, phasellus feugiat dapibus velit nunc, pulvinar eget sollicitudin venenatis cum nullam, vivamus ut a sed, mollitia lectus. Nulla vestibulum massa neque ut et, id hendrerit sit, feugiat in taciti enim proin nibh, tempor dignissim, rhoncus duis vestibulum nunc mattis convallis.',
			'hideProfile' => 1,
			'hideEmail' => 1
		),
		array(
			'id' => 5,
			'user_id' => 5,
			'modified' => '2014-12-06 16:34:47',
			'country' => 'Lorem ipsum dolor sit amet',
			'clan' => 'Lo',
			'email' => 'Lorem ipsum dolor sit amet',
			'skype' => 'Lorem ipsum dolor sit amet',
			'icq' => 'Lorem ipsum dolor ',
			'facebook' => 'Lorem ipsum dolor sit amet',
			'googlep' => 'Lorem ipsum dolor sit amet',
			'twitter' => 'Lorem ipsum dolor sit amet',
			'about' => 'Lorem ipsum dolor sit amet, aliquet feugiat. Convallis morbi fringilla gravida, phasellus feugiat dapibus velit nunc, pulvinar eget sollicitudin venenatis cum nullam, vivamus ut a sed, mollitia lectus. Nulla vestibulum massa neque ut et, id hendrerit sit, feugiat in taciti enim proin nibh, tempor dignissim, rhoncus duis vestibulum nunc mattis convallis.',
			'hideProfile' => 1,
			'hideEmail' => 1
		),
		array(
			'id' => 6,
			'user_id' => 6,
			'modified' => '2014-12-06 16:34:47',
			'country' => 'Lorem ipsum dolor sit amet',
			'clan' => 'Lo',
			'email' => 'Lorem ipsum dolor sit amet',
			'skype' => 'Lorem ipsum dolor sit amet',
			'icq' => 'Lorem ipsum dolor ',
			'facebook' => 'Lorem ipsum dolor sit amet',
			'googlep' => 'Lorem ipsum dolor sit amet',
			'twitter' => 'Lorem ipsum dolor sit amet',
			'about' => 'Lorem ipsum dolor sit amet, aliquet feugiat. Convallis morbi fringilla gravida, phasellus feugiat dapibus velit nunc, pulvinar eget sollicitudin venenatis cum nullam, vivamus ut a sed, mollitia lectus. Nulla vestibulum massa neque ut et, id hendrerit sit, feugiat in taciti enim proin nibh, tempor dignissim, rhoncus duis vestibulum nunc mattis convallis.',
			'hideProfile' => 1,
			'hideEmail' => 1
		),
		array(
			'id' => 7,
			'user_id' => 7,
			'modified' => '2014-12-06 16:34:47',
			'country' => 'Lorem ipsum dolor sit amet',
			'clan' => 'Lo',
			'email' => 'Lorem ipsum dolor sit amet',
			'skype' => 'Lorem ipsum dolor sit amet',
			'icq' => 'Lorem ipsum dolor ',
			'facebook' => 'Lorem ipsum dolor sit amet',
			'googlep' => 'Lorem ipsum dolor sit amet',
			'twitter' => 'Lorem ipsum dolor sit amet',
			'about' => 'Lorem ipsum dolor sit amet, aliquet feugiat. Convallis morbi fringilla gravida, phasellus feugiat dapibus velit nunc, pulvinar eget sollicitudin venenatis cum nullam, vivamus ut a sed, mollitia lectus. Nulla vestibulum massa neque ut et, id hendrerit sit, feugiat in taciti enim proin nibh, tempor dignissim, rhoncus duis vestibulum nunc mattis convallis.',
			'hideProfile' => 1,
			'hideEmail' => 1
		),
		array(
			'id' => 8,
			'user_id' => 8,
			'modified' => '2014-12-06 16:34:47',
			'country' => 'Lorem ipsum dolor sit amet',
			'clan' => 'Lo',
			'email' => 'Lorem ipsum dolor sit amet',
			'skype' => 'Lorem ipsum dolor sit amet',
			'icq' => 'Lorem ipsum dolor ',
			'facebook' => 'Lorem ipsum dolor sit amet',
			'googlep' => 'Lorem ipsum dolor sit amet',
			'twitter' => 'Lorem ipsum dolor sit amet',
			'about' => 'Lorem ipsum dolor sit amet, aliquet feugiat. Convallis morbi fringilla gravida, phasellus feugiat dapibus velit nunc, pulvinar eget sollicitudin venenatis cum nullam, vivamus ut a sed, mollitia lectus. Nulla vestibulum massa neque ut et, id hendrerit sit, feugiat in taciti enim proin nibh, tempor dignissim, rhoncus duis vestibulum nunc mattis convallis.',
			'hideProfile' => 1,
			'hideEmail' => 1
		),
		array(
			'id' => 9,
			'user_id' => 9,
			'modified' => '2014-12-06 16:34:47',
			'country' => 'Lorem ipsum dolor sit amet',
			'clan' => 'Lo',
			'email' => 'Lorem ipsum dolor sit amet',
			'skype' => 'Lorem ipsum dolor sit amet',
			'icq' => 'Lorem ipsum dolor ',
			'facebook' => 'Lorem ipsum dolor sit amet',
			'googlep' => 'Lorem ipsum dolor sit amet',
			'twitter' => 'Lorem ipsum dolor sit amet',
			'about' => 'Lorem ipsum dolor sit amet, aliquet feugiat. Convallis morbi fringilla gravida, phasellus feugiat dapibus velit nunc, pulvinar eget sollicitudin venenatis cum nullam, vivamus ut a sed, mollitia lectus. Nulla vestibulum massa neque ut et, id hendrerit sit, feugiat in taciti enim proin nibh, tempor dignissim, rhoncus duis vestibulum nunc mattis convallis.',
			'hideProfile' => 1,
			'hideEmail' => 1
		),
		array(
			'id' => 10,
			'user_id' => 10,
			'modified' => '2014-12-06 16:34:47',
			'country' => 'Lorem ipsum dolor sit amet',
			'clan' => 'Lo',
			'email' => 'Lorem ipsum dolor sit amet',
			'skype' => 'Lorem ipsum dolor sit amet',
			'icq' => 'Lorem ipsum dolor ',
			'facebook' => 'Lorem ipsum dolor sit amet',
			'googlep' => 'Lorem ipsum dolor sit amet',
			'twitter' => 'Lorem ipsum dolor sit amet',
			'about' => 'Lorem ipsum dolor sit amet, aliquet feugiat. Convallis morbi fringilla gravida, phasellus feugiat dapibus velit nunc, pulvinar eget sollicitudin venenatis cum nullam, vivamus ut a sed, mollitia lectus. Nulla vestibulum massa neque ut et, id hendrerit sit, feugiat in taciti enim proin nibh, tempor dignissim, rhoncus duis vestibulum nunc mattis convallis.',
			'hideProfile' => 1,
			'hideEmail' => 1
		),
	);

}
