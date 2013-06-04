<?php 
/* App schema generated on: 2012-01-24 19:42:51 : 1327430571*/
class AppSchema extends CakeSchema {
	function before($event = array()) {
		return true;
	}

	function after($event = array()) {
	}

	var $games = array(
		'id' => array('type' => 'integer', 'null' => false, 'default' => NULL, 'key' => 'primary', 'collate' => NULL, 'comment' => ''),
		'stage' => array('type' => 'text', 'null' => false, 'default' => NULL, 'length' => 7, 'collate' => 'utf8_bin', 'comment' => '', 'charset' => 'utf8'),
		'home' => array('type' => 'string', 'null' => false, 'default' => NULL, 'length' => 16, 'collate' => 'utf8_bin', 'comment' => '', 'charset' => 'utf8'),
		'away' => array('type' => 'string', 'null' => false, 'default' => NULL, 'length' => 16, 'collate' => 'utf8_bin', 'comment' => '', 'charset' => 'utf8'),
		'score_h' => array('type' => 'integer', 'null' => false, 'default' => NULL, 'length' => 4, 'collate' => NULL, 'comment' => ''),
		'score_a' => array('type' => 'integer', 'null' => false, 'default' => NULL, 'length' => 4, 'collate' => NULL, 'comment' => ''),
		'created' => array('type' => 'datetime', 'null' => false, 'default' => NULL, 'collate' => NULL, 'comment' => ''),
		'reported_by' => array('type' => 'string', 'null' => false, 'default' => NULL, 'length' => 16, 'collate' => 'utf8_bin', 'comment' => '', 'charset' => 'utf8'),
		'indexes' => array('PRIMARY' => array('column' => 'id', 'unique' => 1)),
		'tableParameters' => array('charset' => 'utf8', 'collate' => 'utf8_bin', 'engine' => 'InnoDB')
	);
	var $playoffs = array(
		'id' => array('type' => 'integer', 'null' => false, 'default' => NULL, 'key' => 'primary', 'collate' => NULL, 'comment' => ''),
		'stage' => array('type' => 'integer', 'null' => false, 'default' => NULL, 'collate' => NULL, 'comment' => ''),
		'spot' => array('type' => 'integer', 'null' => false, 'default' => NULL, 'collate' => NULL, 'comment' => ''),
		'game_id' => array('type' => 'integer', 'null' => false, 'default' => NULL, 'collate' => NULL, 'comment' => ''),
		'indexes' => array('PRIMARY' => array('column' => 'id', 'unique' => 1)),
		'tableParameters' => array('charset' => 'utf8', 'collate' => 'utf8_bin', 'engine' => 'InnoDB')
	);
	var $profiles = array(
		'id' => array('type' => 'integer', 'null' => false, 'default' => NULL, 'key' => 'primary', 'collate' => NULL, 'comment' => ''),
		'country' => array('type' => 'string', 'null' => false, 'default' => NULL, 'length' => 20, 'collate' => 'utf8_bin', 'comment' => '', 'charset' => 'utf8'),
		'clan' => array('type' => 'string', 'null' => false, 'default' => NULL, 'length' => 4, 'collate' => 'utf8_bin', 'comment' => '', 'charset' => 'utf8'),
		'modified' => array('type' => 'datetime', 'null' => false, 'default' => NULL, 'collate' => NULL, 'comment' => ''),
		'user_id' => array('type' => 'integer', 'null' => false, 'default' => NULL, 'collate' => NULL, 'comment' => ''),
		'indexes' => array('PRIMARY' => array('column' => 'id', 'unique' => 1)),
		'tableParameters' => array('charset' => 'utf8', 'collate' => 'utf8_bin', 'engine' => 'InnoDB')
	);
	var $tournaments = array(
		'id' => array('type' => 'integer', 'null' => false, 'default' => NULL, 'length' => 4, 'key' => 'primary', 'collate' => NULL, 'comment' => ''),
		'year' => array('type' => 'integer', 'null' => false, 'default' => NULL, 'length' => 4, 'collate' => NULL, 'comment' => ''),
		'gold' => array('type' => 'string', 'null' => false, 'default' => NULL, 'length' => 16, 'collate' => 'utf8_bin', 'comment' => '', 'charset' => 'utf8'),
		'silver' => array('type' => 'string', 'null' => false, 'default' => NULL, 'length' => 16, 'collate' => 'utf8_bin', 'comment' => '', 'charset' => 'utf8'),
		'bronze' => array('type' => 'string', 'null' => false, 'default' => NULL, 'length' => 16, 'collate' => 'utf8_bin', 'comment' => '', 'charset' => 'utf8'),
		'organizers' => array('type' => 'string', 'null' => false, 'default' => NULL, 'length' => 100, 'collate' => 'utf8_bin', 'comment' => '', 'charset' => 'utf8'),
		'usdollars' => array('type' => 'integer', 'null' => false, 'default' => NULL, 'length' => 6, 'collate' => NULL, 'comment' => ''),
		'donors' => array('type' => 'integer', 'null' => false, 'default' => NULL, 'length' => 4, 'collate' => NULL, 'comment' => ''),
		'indexes' => array('PRIMARY' => array('column' => 'id', 'unique' => 1)),
		'tableParameters' => array('charset' => 'utf8', 'collate' => 'utf8_bin', 'engine' => 'InnoDB')
	);
	var $users = array(
		'id' => array('type' => 'integer', 'null' => false, 'default' => NULL, 'key' => 'primary', 'collate' => NULL, 'comment' => ''),
		'username' => array('type' => 'string', 'null' => false, 'default' => NULL, 'length' => 16, 'collate' => 'utf8_bin', 'comment' => '', 'charset' => 'utf8'),
		'password' => array('type' => 'string', 'null' => false, 'default' => NULL, 'length' => 40, 'collate' => 'utf8_bin', 'comment' => '', 'charset' => 'utf8'),
		'created' => array('type' => 'datetime', 'null' => false, 'default' => NULL, 'collate' => NULL, 'comment' => ''),
		'indexes' => array('PRIMARY' => array('column' => 'id', 'unique' => 1)),
		'tableParameters' => array('charset' => 'utf8', 'collate' => 'utf8_bin', 'engine' => 'InnoDB')
	);
}
?>