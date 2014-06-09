<?php
App::uses('AppModel', 'Model');

class Rule extends AppModel
{
    public $name = 'Rule';
    public $displayField = 'created';

    public $belongsTo = array(
        'User' => array(
            'className' => 'User',
            'foreignKey' => 'user_id'
        )
    );
}
