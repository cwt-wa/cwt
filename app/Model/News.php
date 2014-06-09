<?php
App::uses('AppModel', 'Model');

class News extends AppModel
{
    public $displayField = 'id';
    public $name = 'New';

    public $belongsTo = array(
        'User' => array(
            'className' => 'User',
            'foreignKey' => 'user_id'
        )
    );
}
