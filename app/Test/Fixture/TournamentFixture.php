<?php
/**
 * TournamentFixture
 *
 */
class TournamentFixture extends CakeTestFixture {

/**
 * Fields
 *
 * @var array
 */
	public $fields = array(
		'id' => array('type' => 'integer', 'null' => false, 'default' => null, 'length' => 4, 'key' => 'primary'),
		'year' => array('type' => 'integer', 'null' => false, 'default' => null, 'length' => 4),
		'status' => array('type' => 'integer', 'null' => false, 'default' => null),
		'gold_id' => array('type' => 'integer', 'null' => false, 'default' => null),
		'silver_id' => array('type' => 'integer', 'null' => false, 'default' => null),
		'bronze_id' => array('type' => 'integer', 'null' => false, 'default' => null),
		'review' => array('type' => 'text', 'null' => false, 'default' => null, 'collate' => 'utf8_bin', 'charset' => 'utf8'),
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
			'year' => 2014,
			'status' => 1,
			'gold_id' => 0,
			'silver_id' => 0,
			'bronze_id' => 0,
			'review' => 'Lorem ipsum dolor sit amet, aliquet feugiat. Convallis morbi fringilla gravida, phasellus feugiat dapibus velit nunc, pulvinar eget sollicitudin venenatis cum nullam, vivamus ut a sed, mollitia lectus. Nulla vestibulum massa neque ut et, id hendrerit sit, feugiat in taciti enim proin nibh, tempor dignissim, rhoncus duis vestibulum nunc mattis convallis.'
		)
	);

}
