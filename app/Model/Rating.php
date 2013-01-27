<?php
App::uses('AppModel', 'Model');

class Rating extends AppModel {
	public $name = 'Rating';

	public $belongsTo = array(
		'Game' => array(
			'className' => 'Game',
			'foreignKey' => 'game_id'
		)
	);

	public function ratingStats($game_id) {
		$rating = $this->find('first', array(
			'conditions' => array(
				'Rating.game_id' => $game_id
			)
		));
		$rating = $rating['Rating'];
		
		if($rating == null) {
			$rating['p']['likes'] = 50;
			$rating['p']['dislikes'] = 50;

			$rating['p']['lightside'] = 50;
			$rating['p']['darkside'] = 50;

			$rating['likes'] = 0;
			$rating['dislikes'] = 0;

			$rating['lightside'] = 0;
			$rating['darkside'] = 0;
		} else {
			if($rating['likes'] + $rating['dislikes'] == 0) {
				$rating['p']['likes'] = 50;
				$rating['p']['dislikes'] = 50;
			} else {
				$likes = $rating['likes'];
				$dislikes = $rating['dislikes'];
				
				@$rating['p']['likes'] = ($likes / ($likes + $dislikes)) * 100;
				@$rating['p']['dislikes'] = ($dislikes / ($likes + $dislikes)) * 100;
			}

			if($rating['lightside'] + $rating['darkside'] == 0) {
				$rating['p']['lightside'] = 50;
				$rating['p']['darkside'] = 50;
			} else {
				$lightside = $rating['lightside'];
				$darkside = $rating['darkside'];
				
				@$rating['p']['lightside'] = ($lightside / ($lightside + $darkside)) * 100;
				@$rating['p']['darkside'] = ($darkside / ($lightside + $darkside)) * 100;
			}	
		}

		return $rating;
	}

	public function submit($data) {
		// Checking whether the game has already been rated at all.
		$areThereRatings = $this->field('id', array(
			'game_id' => $data['game_id']
		));
		
		if($areThereRatings) { // Game's already been rated by somebody - just edit.
			$oldData = $this->read(null, $id);
			$newSum = $oldData['Rating']['sum'] + $data['rating'];
			$newRatings = $oldData['Rating']['ratings'] + 1;
			$newRating = $newSum / $newRatings;
			$savings = array(
				'sum' 	  => $newSum,
				'ratings' => $newRatings,
				'rating'  => $newRating
			);

			$this->save($savings);
			return true;
		} else { // Game's not been rated so far. Let's save a new rating.
			$newRating = $this->saveMany(array(
				array(
					'game_id' => $data['game_id'],
					'rating'  => $data['rating'],
					'ratings' => '1',
					'sum' 	  => $data['rating']
				)
			));
			
			$this->trace('Ratings', 'add', $data['game_id']);
			return true;
		}
	}

	// Checks if a game has already been rated by the logged in user.
	public function alreadyRated($gameID) {
		$this->bindModel(
		    array('hasMany' => array('Trace' => array('className' => 'Trace'))));
		
		$alreadyRated = $this->Trace->find('count',
			array('conditions' => array(
				'user_id' => AuthComponent::user('id'),
				'controller' => 'Ratings',
				'action' => 'add',
				'additional' => $gameID
			))
		);

		return (bool)$alreadyRated;
	}
}
