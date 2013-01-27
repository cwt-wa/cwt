<?php
App::uses('AppModel', 'Model');
class Schedule extends AppModel {
	public $belongsTo = array(
		'Scheduler' => array(
			'className' => 'User',
			'foreignKey' => 'home_id'
		),
		'Scheduled' => array(
			'className' => 'User',
			'foreignKey' => 'away_id'
		),
		'Stream' => array(
			'className' => 'Stream',
			'foreignKey' => 'stream_id'
		),
	);

	// Filters games that have already been played.
	// Highlights games that are past the scheduled time but not yet reported,
	// so they're currently going and maybe being streamed.
	// It will also set Streams online and offline.
	public function filterSchedule($schedules) {
		foreach($schedules as $key => $val) {        	
    		$when = strtotime($val['Schedule']['when']);
        	$now = strtotime(gmdate('Y-m-d H:i:s'));

        	if($now >= $when) {
        		if(ClassRegistry::init('Game')->isReported(
        		$val['Schedule']['home_id'], $val['Schedule']['away_id'])) {
        			$this->delete($val['Schedule']['id']);
        			$online = false;
        		} else {
        			$online = true;
        			$tourney = ClassRegistry::init('Tournament')->info();
        			
        			if($tourney['status'] == 'group') {
        				$stage = ClassRegistry::init('Group')->getGroup(
        					$val['Schedule']['home_id']);
        			} elseif($tourney['status'] == 'playoff') {
        				$stage = ClassRegistry::init('Playoff')->currentGame(
        					$val['Schedule']['home_id']);

        				$stage = $stage['Playoff']['step'];
        			}

        			$streaming = implode(',', array(
        					$stage, $val['Scheduler']['id'],
        					$val['Scheduled']['id']));
        		}

        		if($val['Stream']) {
        			foreach($val['Stream'] as $key => $val) {
	        			$Stream = ClassRegistry::init('Stream');
	        			$Stream->id = $val['id'];
	        			$Stream->save(array(
	        				'online' => $online,
	        				'streaming' => $streaming
	        			),
	        			false, // No validation.
	        			array( // Fields to update.
	        				'online', 'streaming'
	        			));
	        		}
        		}
        	}        	
        }

        $schedules = $this->find('all', array(
			'order' => 'Schedule.when ASC'));
		return $this->scheduledStreams($schedules);        
	}

	// Returns the streams that scheduled a stream for a certain game.
	public function scheduledStreams($schedules) {
		foreach($schedules as $key => $val) {
	        $schedules[$key]['Stream'] = array();

        	if($val['Schedule']['stream_id']) {
        		$streams = $val['Schedule']['stream_id'];
	        	$streams = explode(',', $streams);
	        	$numberOfStreams = count($streams) - 1;

	        	for($i = 0; $i <= $numberOfStreams; $i++) {
	        		$stream = $this->Stream->read(null, $streams[$i]);
	        		$schedules[$key]['Stream'][$i] = $stream['Stream'];
	        	}
        	}
        }

        return $schedules;
	}

	// Returns all days left from today till end of year.
	public function daysLeft() {
		$daysLeft = array();
		$daysLeft[gmdate('Y-m-d')] = gmdate('M j');
		$skipDays = 1;

		while(true) {
			$daysLeft[gmdate('Y-m-d', strtotime("+$skipDays day"))] =
				gmdate('M j', strtotime("+$skipDays day"));

			if(gmdate('Y-m-d', strtotime("+$skipDays day")) == '2012-12-31') {
				return $daysLeft;
			}

			$skipDays++;
		}
	}

	// Returns valid times to submit. Half an hour rhythm.
	public function getTimes() {
		$times = array();
		$now = 0000000000;
		$skipTime = 30;

		while(true) {
			$times[gmdate('H:i:s', strtotime("+$skipTime minute", $now))] = 
				gmdate('H:i', strtotime("+$skipTime minute", $now));

			if(gmdate('H:i', strtotime("+$skipTime minute", $now)) == '23:30') {
				return $times;
			}

			$skipTime += 30;
		}
	}
}
