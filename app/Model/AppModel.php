<?php
App::uses('Model', 'Model');

class AppModel extends Model {	
	public function validateId($id, $model) {
		$this->Model = ClassRegistry::init($model);
        
        if ($id < 1) {
            return false;
        }
        
        $isExistentId = $this->Model->find('count', array(
			'conditions' => array(
                $model . '.id' => $id
            )
		));
        
		return (bool) $isExistentId;
	}
	
	public function tourneyStatus() {
        $this->bindModel(array('hasMany' =>
            array('Tournament' => array('className' => 'Tournament'))));
        $status = $this->Tournament->field('status', null, 'year DESC');
        return $status;
    }

    public function tourneyStarted() {
    	if($this->tourneyStatus() == 'pending'
    	|| $this->tourneyStatus() == 'archived') {
    		return false;
    	} else {
    		return true;
    	}
    }

    // Returns whole row of the most recent tourney. DEPRECATED
    public function tourneyInfo() {    
        $row = $this->find('first', array(
            'limit' => 1,
            'order' => array('Tournament.id' => 'desc')
        ));
        return $row['Tournament'];
    }

    // Write (default) or get a trace. DEPRECATED
    /*public function trace($controller, $action, $additional = '', $write_or_get = 'write') {
        $this->bindModel(array('hasMany' =>
            array('Trace' => array('className' => 'Trace'))));

        if($write_or_get == 'write') {
            $this->Trace->save(array(
                'user_id' => AuthComponent::user('id'),
                'controller' => $controller,
                'action' => $action,
                'additional' => $additional
            ), array('validate'=>false, 'callbacks'=>false));
        } else {
            $getTrace = $this->Trace->find('first', array(
                'conditions' => array(
                    'Trace.user_id' => AuthComponent::user('id'),
                    'Trace.controller' => $controller,
                    'Trace.action' => $action,
                    'Trace.additional' => $additional
                )
            ));
        
            return $getTrace['Trace'];
        }
    }*/
}
