<?php

App::uses('AppModel', 'Model');

/**
 * Standings for group tables.
 */
class Standing extends AppModel
{

    public $name = 'Standing';
    public $displayField = 'id';
    public $belongsTo = array(
        'Group' => array(
            'className' => 'Group',
            'foreignKey' => 'group_id',
        ),
        'User' => array(
            'className' => 'User',
            'foreignKey' => 'user_id',
        )
    );
    public $validate = array(
        'group_id' => array(
            'numeric' => array(
                'rule' => array('numeric'),
            ),
        ),
        'points' => array(
            'numeric' => array(
                'rule' => array('numeric'),
            ),
        ),
        'games' => array(
            'numeric' => array(
                'rule' => array('numeric'),
            ),
        ),
        'game_ratio' => array(
            'numeric' => array(
                'rule' => array('numeric'),
            ),
        ),
        'round_ratio' => array(
            'numeric' => array(
                'rule' => array('numeric'),
            ),
        ),
    );
}
