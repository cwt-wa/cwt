<?php
App::uses('AppModel', 'Model');

class Infoboard extends AppModel {
	public $name = 'Infoboard';
	public $displayField = 'id';

	public $belongsTo = array(
		'User' => array(
			'className' => 'User',
			'foreignKey' => 'user_id',
		)
	);

	// Checks if user is sender of message.
	public function isSender($sender) {
		if($sender == AuthComponent::user('id')) {
			return true;
		} else {
			return false;
		}
	}

	// Checks if user is recipient of message.
	public function isRecipient($msg) {
		$needle = AuthComponent::user('username');
        $currentTournament = $this->currentTournament();

		if(preg_match("/@\b$needle\b/i", $msg)) {
		    return true;
		}

		if(AuthComponent::user('stage') == 'group') {
            $Group = ClassRegistry::init('Group');
            $Group->Standing->recursive = 0;
            $group = $Group->Standing->find('first', array(
				'conditions' => array(
					'Standing.user_id' => AuthComponent::user('id'),
                    'Group.tournament_id' => $currentTournament['Tournament']['id']
			)));

			$needle = $group['Group']['label'];

			if(preg_match("/@\b$needle\b/i", $msg)) {
			    return true;
			}
		}

		if(AuthComponent::user('admin')) {
			if(preg_match("/@\badmins\b/i", $msg)) {
			    return true;
			}
		}

		return false;
	}

	public function newpost($post) {
		$trace = ClassRegistry::init('Trace')->check('infoboards', 'show', 'lastsight', 0, 'read');

		//debug($post);
		//debug($trace['created'] . ' ' . $trace['id']);

		$post = strtotime($post);
		$trace = strtotime($trace['created']);

		if($post > $trace) {
			//debug('newPost!');
			return true;
		}
		//debug('oldPost');
		return false;
	}

	// Return messages, the user should see according to the category.
	public function messages($category = '1') {
		$this->recursive = 0;

        if($category === '0') {
			$unfiltered = $this->find('all', array(
				'order' => 'Infoboard.created DESC',
				'limit' => 100
			));
			$i = 0;
			foreach($unfiltered as $msg) {
				if(!$this->isSender($msg['Infoboard']['user_id'])
				&& !$this->isRecipient($msg['Infoboard']['message'])) {
					if($msg['Infoboard']['category'] == 2) {
						unset($unfiltered[$i]);
					}
				}
				$i++;
			}
		} else {
			if($category == 6) {
				$unfiltered = $this->find('all', array(
					'order' => 'Infoboard.created DESC',
					'limit' => 100
				));
			} else {
				$unfiltered = $this->find('all', array(
					'conditions' => array(
							'Infoboard.category' => $category
					),
					'order' => 'Infoboard.created DESC',
					'limit' => 100
				));
			}
		}

		if($category === '2') {
			$i = 0;
			foreach($unfiltered as $msg) {
				if(!$this->isSender($msg['Infoboard']['user_id'])
				&& !$this->isRecipient($msg['Infoboard']['message'])) {
					unset($unfiltered[$i]);
				}
				$i++;
			}
		}

		$i = 0;
		foreach($unfiltered as $msg) {
			if($this->newpost($msg['Infoboard']['created'])) {
				$unfiltered[$i]['newpost'] = '<hr />';
				unset($unfiltered[$i-1]['newpost']);
			}

			$i++;
		}

		return $unfiltered; // Unfiltered is filtered, duh.
	}

	public function nick_suggestions($str, $count) {
		// Fill up array with names alphabetically.
		$users = $this->User->find('list');
		sort($users);
		$users[] = 'admins';

		if(AuthComponent::user('stage') == 'group') {
			$this->bindModel(array('hasMany' =>
            	array('Group' => array('className' => 'Group'))));

			$users[] = $this->User->Group->getGroup();
		}

		// Get the current input of username.
		$q = substr($str, -($count));

		// Lookup all hints from array if length of q>0.
		if(strlen($q) > 0) {
		  	$suggestions = array();

			for($i = 0; $i < count($users); $i++) {
			    if(strtolower($q) == strtolower(substr($users[$i], 0, strlen($q)))) {
			      	$suggestions[] = $users[$i];
				}
			}
		}

		// Set output to "no suggestion" if no hint were found
		// or to the correct values
		if(empty($suggestions)) {
		  	return "No such user as <b>$q</b>.";
		} else {
		  	//output the response
			return $suggestions;
		}
	}

	// New message sent to the Infoboard. Message by a logged in user.
	public function submit($message, $guest = false) {
		$emails = @preg_match_all('/\w+@\w+\.[a-z]{2,3}/', $message, $emailsResult);
		$atsigns = @preg_match_all('/@/', $message, $atsignsResult);

		if($atsigns - $emails > 0) { // This is a PM, top secret!
			$category = 2;
			$haystack = $message . ' ';

			while(strpos($haystack, '@') !== false) {
				$posAT = strpos($haystack, '@');
				$posSP = strpos($haystack, ' ', $posAT);

				$recipient = substr($haystack, $posAT + 1, $posSP - $posAT);

				if(preg_match("/@\b$recipient?\b/i", $haystack)) {
					$exists = $this->User->find('count', array(
						'conditions' => array(
							'username' => $recipient
						)
					));

					if(!$exists) {
						$admins = preg_match("/@\badmins?\b/i", $haystack);
					}

					if(!$admins && !$exists) {
						$groups = array(
							'A' => preg_match("/@a\b/i", $haystack),
							'B' => preg_match("/@b\b/i", $haystack),
							'C' => preg_match("/@c\b/i", $haystack),
							'D' => preg_match("/@d\b/i", $haystack),
							'E' => preg_match("/@e\b/i", $haystack),
							'F' => preg_match("/@f\b/i", $haystack),
							'G' => preg_match("/@g\b/i", $haystack),
							'H' => preg_match("/@h\b/i", $haystack),
						);
					}

					if($exists == false
					&& $admins == false
					&& in_array(1, $groups) == false
					&& !preg_match("/\b\w+\@\w+[\.\w+]+\b/", $haystack)) {
						$unknowns[] = $recipient;
					}
				}

				$haystack = substr($haystack, $posSP);
			}

			if(!empty($unknowns)) {
				return $unknowns;
			}
		} elseif($guest) {  // Dear Guest, you're being processed.
			$category = 4;
		} else { // Shouting!
			$category = 1;
		}

		$user_id = $guest ? 0 : AuthComponent::user('id');
		$guest = $guest ? $guest : 0;

		$this->save(array(
			'user_id'  => $user_id,
			'guest'	   => $guest,
			'message'  => $message,
			'category' => $category
		));

		return true;
	}

	public function newpms() {
		$pms = $this->find('all', array(
			'conditions' => array(
				'Infoboard.category' => 2
			)
		));

		$i = 0; $counter = 0;
		foreach($pms as $pm) {
			if($this->newpost($pm['Infoboard']['created'])
			&& $this->isRecipient($pm['Infoboard']['message'])) {
				$counter++;
			} else {
				unset($pms[$i]);
			}
			$i++;
		}

		if($counter == 1) {
			return array(
				'sender'  => $pm['User']['username'],
				'message' => $pm['Infoboard']['message']
 			);
		} elseif($counter > 1) {
			return $counter;
		}
		return false;
	}

	// Writing Tournament News.
	public function tournamentNews($gameId, $verb, $userId = false) {
		if(!$userId) {
			$user = AuthComponent::user();
		} else {
			$user = ClassRegistry::init('User')->read(null, $userId);
			$user = $user['User'];
		}

		$game = ClassRegistry::init('Game')->read(null, $gameId);

		$msg = '<a href="/users/view/'.$user['id'].'">'.$user['username'].'</a>';
		$msg .= " $verb ";
		$msg .= '<a href="/users/view/'.$game['Home']['id'].'">'.$game['Home']['username'].'</a>';
		$msg .= ' <a href="/games/view/'.$game['Game']['id'].'">'.$game['Game']['score_h'].'-';
		$msg .= $game['Game']['score_a'].'</a> ';
		$msg .= '<a href="/users/view/'.$game['Away']['id'].'">'.$game['Away']['username'].'</a>';


		debug($user); debug($game); debug($msg);

		$this->save(array(
			'message' => $msg,
			'category' => 3
		));
	}

	public $validate = array(
		'message' => array(
			'notempty' => array(
				'rule' => array('notempty')
			),
		),
	);
}
