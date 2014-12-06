<?php
/**
 * StandingFixture
 *
 */
class StandingFixture extends CakeTestFixture {

/**
 * Fields
 *
 * @var array
 */
	public $fields = array(
		'id' => array('type' => 'integer', 'null' => false, 'default' => null, 'key' => 'primary'),
		'group_id' => array('type' => 'integer', 'null' => false, 'default' => null),
		'user_id' => array('type' => 'integer', 'null' => false, 'default' => null),
		'points' => array('type' => 'integer', 'null' => false, 'default' => null),
		'games' => array('type' => 'integer', 'null' => false, 'default' => null),
		'game_ratio' => array('type' => 'integer', 'null' => false, 'default' => null),
		'round_ratio' => array('type' => 'integer', 'null' => false, 'default' => null),
		'indexes' => array(
			'PRIMARY' => array('column' => 'id', 'unique' => 1)
		),
		'tableParameters' => array('charset' => 'utf8', 'collate' => 'utf8_general_ci', 'engine' => 'MyISAM')
	);

/**
 * Standings for eight groups.
 */
	public function init() {
		$this->records = array();
		$id = 1;

		for ($i = 0; $i < 8; $i++) {
			for ($k = 0; $k < 4; $k++) {
				$this->records[] = array(
					'id' => $id,
					'group_id' => $i + 1,
					'user_id' => $id,
					'points' => 0,
					'games' => 0,
					'game_ratio' => 0,
					'round_ratio' => 0
				);

				$id++;
			}
		}

		parent::init();
	}
}
