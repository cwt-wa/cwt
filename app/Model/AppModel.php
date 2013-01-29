<?php
App::uses('Model', 'Model');

class AppModel extends Model {	
	/**
     * Validate a Model's Id.
     * 
     * @param int $id Id to be validated.  
     * @param str $model The Model the Id belongs to.
     * @return boolean False, if the Id is less than one or doesn't exist
     * in the given Model's database table, true otherwise.
     */
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
    
    /**
     * Properly formats input that came from the three FormHelper's methods
     * FormHelper::year(), FormHelper::month() and FormHelper::day().
     * This method should be called from the Controller, so that the date can
     * then be correctly validated and inserted into the database. This method's
     * return value should be assigned to the the corresponding
     * Controller::request->data array element that will go into
     * validation/database.
     * 
     * @param array $dateArray array('year' => '', 'month' => '', 'day' => '')
     * @return string The formatted date YYYY-MM-DD
     */
    public function formatDate($dateArray) {
        if (empty($dateArray['year'])) {
            $dateArray['year'] = '0000';
        }
        
        if (empty($dateArray['month'])) {
            $dateArray['month'] = '00';
        }
        
        if (empty($dateArray['day'])) {
            $dateArray['day'] = '00';
        }
        
        $formattedDate = 
            $dateArray['year'] . '-'
            . $dateArray['month'] . '-'
            . $dateArray['day'];
        
        return $formattedDate;
    }

     /**
     * DEPRECATED - Use the Tournament Model instead.
     */
	public function tourneyStatus() {
        $this->bindModel(array('hasMany' =>
            array('Tournament' => array('className' => 'Tournament'))));
        $status = $this->Tournament->field('status', null, 'year DESC');
        return $status;
    }

    /**
     * DEPRECATED - Use the Tournament Model instead.
     */
	public function tourneyStarted() {
    	if($this->tourneyStatus() == 'pending'
    	|| $this->tourneyStatus() == 'archived') {
    		return false;
    	} else {
    		return true;
    	}
    }

    /**
     * DEPRECATED - Use the Tournament Model instead.
     */
	public function tourneyInfo() {    
        $row = $this->find('first', array(
            'limit' => 1,
            'order' => array('Tournament.id' => 'desc')
        ));
        return $row['Tournament'];
    }
}
