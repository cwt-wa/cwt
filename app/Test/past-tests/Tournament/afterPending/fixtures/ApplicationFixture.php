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
 * Insert the first 35 users.
 */
	public function init() {
		$this->records = array();

		for ($i = 0; $i < 35; $i++) {
			$this->records[] = array(
				'id' => $i + 1,
				'user_id' => $i + 1,
				'created' => '2014-12-03 15:00:00',
			);
		}

		parent::init();
	}

}
