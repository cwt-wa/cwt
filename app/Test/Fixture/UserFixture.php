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
		'stage' => array('type' => 'string', 'null' => false, 'default' => null, 'length' => 16, 'collate' => 'utf8_general_ci', 'charset' => 'utf8'),
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
 * Inserting 50 users.
 */
	public function init() {
		$this->records = array();

		for ($i = 0; $i < 50; $i++) {
			if ($i < 16) {
				$stage = 'group';
			} else if ($i < 32) {
				$stage = 'playoff';
			} else {
				$stage = 'retired';
			}

			$this->records[] = array(
				'id' => $i + 1,
				'username' => 'Player' . $i + 1,
				'password' => '',
				'md5password' => 'e77989ed21758e78331b20e477fc5582',
				'admin' => false,
				'stage' => $stage,
				'timeline' => '00000000000000',
				'participations' => 0,
				'achievements' => 0,
				'created' => '2014-12-03 15:00:00',
			);
		}

		parent::init();
	}
}
