<?php
App::uses('AppModel', 'Model');

class Comment extends AppModel {
	public $name = 'Comment';

	public $belongsTo = array(
		'Game' => array(
			'className' => 'Game',
			'foreignKey' => 'game_id'
		),
		'User' => array(
			'className' => 'User',
			'foreignKey' => 'user_id'
		)
	);

	public $validate = array(
		'message' => array(
			'notempty' => array(
				'rule' => array('notempty'),
				'message' => 'Empty comments don\'t make sense.'
			),
		),
	);
}
