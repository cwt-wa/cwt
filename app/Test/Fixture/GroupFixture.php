<?php
/**
 * GroupFixture
 *
 */
class GroupFixture extends CakeTestFixture {

/**
 * Fields
 *
 * @var array
 */
	public $fields = array(
		'id' => array('type' => 'integer', 'null' => false, 'default' => null, 'key' => 'primary'),
		'tournament_id' => array('type' => 'integer', 'null' => false, 'default' => null),
		'label' => array('type' => 'text', 'null' => false, 'default' => null, 'length' => 1, 'collate' => 'utf8_bin', 'charset' => 'utf8'),
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
			'tournament_id' => 1,
			'label' => 'A'
		),
		array(
			'id' => 2,
			'tournament_id' => 1,
			'label' => 'B'
		),
		array(
			'id' => 3,
			'tournament_id' => 1,
			'label' => 'C'
		),
		array(
			'id' => 4,
			'tournament_id' => 1,
			'label' => 'D'
		),
		array(
			'id' => 5,
			'tournament_id' => 1,
			'label' => 'E'
		),
		array(
			'id' => 6,
			'tournament_id' => 1,
			'label' => 'F'
		),
		array(
			'id' => 7,
			'tournament_id' => 1,
			'label' => 'G'
		),
		array(
			'id' => 8,
			'tournament_id' => 1,
			'label' => 'H'
		)
	);

}
