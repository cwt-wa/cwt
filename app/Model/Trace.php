<?php
App::uses('AppModel', 'Model');

class Trace extends AppModel {
	public $name = 'Trace';
	public $displayField = 'id';

	public $belongsTo = array(
		'User' => array(
			'className' => 'User',
			'foreignKey' => 'user_id'
		)
	);

	public function check($controller, $action = '', $additional = '', $on = 0, $write_or_read = 'write') {
	    if($write_or_read == 'write') {
	        $this->save(array(
	            'user_id' 	 => AuthComponent::user('id'),
	            'controller' => $controller,
	            'action' 	 => $action,
	            'additional' => $additional,
	            'on' 		 => $on
	        ));
	    } else {
	        $getTrace = $this->find('first', array(
	            'conditions' => array(
	                'Trace.user_id' 	 => AuthComponent::user('id'),
	                'Trace.controller' => $controller,
	                'Trace.action' 	 => $action,
	                'Trace.additional' => $additional,
	                'Trace.on'		 => $on
	            ), 'order' => 'Trace.id DESC'
	        ));
	    
	        return $getTrace['Trace'];
	    }
	}
}
