<?php
App::uses('AppModel', 'Model');

class Application extends AppModel
{
    public $name = 'Application';
    public $displayField = 'id';

    public $belongsTo = array(
        'User' => array(
            'className' => 'User',
            'foreignKey' => 'user_id',
        )
    );
}
