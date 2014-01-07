<?php
App::uses('AppModel', 'Model');

class Playoff extends AppModel {
	public $name = 'Playoff';
	public $displayField = 'id';

	public $stepAssoc = array(
		1 => 'Last Sixteen',
		2 => 'Quarterfinal',
		3 => 'Semifinal',
		4 => 'Third Place',
		5 => 'Final'
	);

	public $keyAssoc = array(
		0 => array(
				'step' => 1,
				'spot' => 1
			),
		1 => array(
				'step' => 1,
				'spot' => 2
			),
		2 => array(
				'step' => 1,
				'spot' => 3
			),
		3 => array(
				'step' => 1,
				'spot' => 4
			),
		4 => array(
				'step' => 1,
				'spot' => 5
			),
		5 => array(
				'step' => 1,
				'spot' => 6
			),
		6 => array(
				'step' => 1,
				'spot' => 7
			),
		7 => array(
				'step' => 1,
				'spot' => 8
			),
		8 => array(
				'step' => 2,
				'spot' => 1
			),
		9 => array(
				'step' => 2,
				'spot' => 2
			),
		10 => array(
				'step' => 2,
				'spot' => 3
			),
		11 => array(
				'step' => 2,
				'spot' => 4
			),
		12 => array(
				'step' => 3,
				'spot' => 1
			),
		13 => array(
				'step' => 3,
				'spot' => 2
			),
		14 => array(
				'step' => 4,
				'spot' => 1
			),
		15 => array(
				'step' => 5,
				'spot' => 1
			)
	);

	/*public $hasOne = array(
		'Step' => array(
			'className'  => 'Game',
			'foreignKey' => 'playoff_id',
			'conditions' => array('Game.group_id' => 0)
		)
	); Is that necessary? */

	public $belongsTo = array(
        'Game' => array(
            'className'  => 'Game',
            'foreignKey' => 'game_id',
			'conditions' => array('Game.group_id' => 0)
        )
    );

    public function betStats($game_id) {
		$bet = $this->find('first', array(
			'conditions' => array(
				'Playoff.game_id' => $game_id
			)
		));

		$bet_h = $bet['Playoff']['bet_h'];
		$bet_a = $bet['Playoff']['bet_a'];

		if($bet_h == 0 && $bet_a == 0) {
			return array(
				'bet_h' => 50,
				'bet_a' => 50
			);
		} else {
			return array(
				'bet_h' => ($bet_h / ($bet_h + $bet_a)) * 100,
				'bet_a' => ($bet_a / ($bet_h + $bet_a)) * 100
			);
		}
	}

    // A new playoff game has been reported. Call by Game Model.
    public function updateReport($winner, $loser) {
    	$game = $this->Game->gameWL($winner, $loser);
        $this->recursive = 1;
        $this->Game->recursive = 1;
        $currentTournament = $this->currentTournament();

    	switch($game['Playoff']['step']) {
    		case '1'; // Last Sixteen
    		case '2'; // Quarterfinal
    			$spot 	 = $game['Playoff']['spot'];
		     	$newStep = $game['Playoff']['step'] + 1;

				// Little formular on advancing the winner.
		        if($spot % 2 != 0) {
				    $newSpot = ($spot + 1) / 2;
				    $winnerHA = 'home_id';
				} else {
				    $newSpot = $spot / 2;
				    $winnerHA = 'away_id';
				}

		     	$newPlayoff = array('conditions' => array(
					'Playoff.spot' => $newSpot,
					'Playoff.step' => $newStep,
                    'Game.tournament_id' => $currentTournament['Tournament']['id']
				));

				// Checking if there's already a new opponent.
				$newOpp = $this->Game->find('count', $newPlayoff);

				// If yes give IDs to update table in place of insert.
				if($newOpp) {
					// Table rows of the new game and playoff.
					$newOpp = $this->find('first', $newPlayoff);

					$this->id 		= $newOpp['Playoff']['id'];
					$this->Game->id = $newOpp['Game']['id'];
				} else {
					$this->id 		= false;
					$this->Game->id = false;

					// Only create new playoff, if there is none.
			        $this->save(array(
			        	'step' 		 => $newStep,
			        	'spot' 		 => $newSpot,
						'stepAssoc'  => $this->stepAssoc[$newStep]
			        ));
				}

		        // Insert or update new game.
				$this->Game->save(array(
					'playoff_id' => $this->id,
					$winnerHA 	 => $winner,
					'created'	 => '0000-00-00 00:00:00',
                    'tournament_id' => $currentTournament['Tournament']['id']
				));

				// Now we also got to know the new game's id.
				// If there's already a PO, it'll just overwrite it.
				$this->save(array('game_id' => $this->Game->id));

				$this->bindModel(array('hasMany' => array(
			        'User' => array('className' => 'User'))));

				// Set the loser retired again.
				$this->User->updateAll(
					array('User.stage' => "'retired'"),
					array('User.id'    => $loser)
				);

				$this->updTimeline($loser, $game['Playoff']['step'] + 1);
			break;
    		case '3': // Semifinal
    			$spot 	 = $game['Playoff']['spot'];
    			$newSpot = 1;
    			$player[4] = $loser; // Loser in SF(4)
				$player[5] = $winner; // Winner in F(5)

				// Little formular on advancing the winner.
		        $winnerHA = ($spot % 2 != 0) ? 'home_id' : 'away_id' ;

				for($newStep = 4; $newStep <= 5; $newStep++) {
					$this->create(); $this->Game->create(); // Make room.

					$newPlayoff = array('conditions' => array(
						'Playoff.spot' => $newSpot,
						'Playoff.step' => $newStep,
                        'Game.tournament_id' => $currentTournament['Tournament']['id']
					));

					// Checking if there's already a new opponent.
					$newOpp = $this->Game->find('count', $newPlayoff);

					// If yes give IDs to update table in place of insert.
					if($newOpp) {
						// Table rows of the new game and playoff.
						$newOpp = $this->find('first', $newPlayoff);

						$this->id 		= $newOpp['Playoff']['id'];
						$this->Game->id = $newOpp['Game']['id'];
						$winnerloser	= $player[$newStep];
					} else {
						$this->id 		= false;
						$this->Game->id = false;
						$winnerloser	= $player[$newStep];

						// Only create new playoff, if there is none.
				        $this->save(array(
				        	'step' 		 => $newStep,
				        	'spot' 		 => $newSpot,
							'stepAssoc'  => $this->stepAssoc[$newStep]
				        ));
					}

			        // Insert or update new game.
					$this->Game->save(array(
						'playoff_id' => $this->id,
						$winnerHA 	 => $winnerloser,
						'created'	 => '0000-00-00 00:00:00',
                        'tournament_id' => $currentTournament['Tournament']['id']
					));

					// Now we also got to know the new game's id.
					// If there's already a PO, it'll just overwrite it.
					$this->save(array('game_id' => $this->Game->id));
				}
    		break;
    		case '4'; // Third Place
    		case '5'; // Final
    			$step = $game['Playoff']['step']; // Either Final (5) or Third Place (4).

				$this->bindModel(array('hasMany' => array(
			        'Tournament' => array('className' => 'Tournament'))));

				// Award these champions!
				if($step == 4) {
					$this->Tournament->updateAll(
						array('Tournament.bronze_id' => $winner),
						array('Tournament.status' 	 => 'playoff')
					);
				} else {
					$this->Tournament->updateAll(
						array(
							'Tournament.gold_id'   => $winner,
							'Tournament.silver_id' => $loser
						),
						array('Tournament.status'  => 'playoff')
					);
				}

				// If both final games have been played, finish the whole tourney.
				// Hopefully I can still use the bound Tournament model.
				if($currentTournament['Gold']['id']	!= null
				&& $currentTournament['Silver']['id']	!= null
				&& $currentTournament['Bronze']['id']	!= null) {
					$this->Tournament->id = $currentTournament['Tournament']['id'];
					$this->Tournament->save(array('status' => 'finished'));
				}

				$this->bindModel(array('hasMany' => array(
			        'User' => array('className' => 'User'))));

				$this->User->updateAll( // Set loser to retired.
					array('User.stage' => "'retired'"),
					array('User.id'    => $loser)
				);
				$this->User->updateAll( // Set winner to retired.
					array('User.stage' => "'retired'"),
					array('User.id'    => $winner)
				);

				if($game['Playoff']['step'] == '4') { // Third Place
					$this->updTimeline($winner, 5);
					$this->updTimeline($loser, 4);
				} else { // Final
					$this->updTimeline($winner, 7);
					$this->updTimeline($loser, 6);
				}
    	}

    	return array(
			'reportedGame' => $game['Game']['id'],
			'reportedPO'   => $game['Playoff']['id'],
			'stepAssoc'    => $this->stepAssoc[$game['Playoff']['step']]
		);
    }

    // Updates user's Timeline after Playoff games
	public function updTimeline($user_id, $achievement) {
		$User = ClassRegistry::init('User');
		$User->id = $user_id;
		$timeline = $User->field('timeline');

		$User->save(array(
			'timeline' => $timeline . $achievement
		));

		return true;
	}

	// Build the playoff tree. Yes, manually for now.
	public function start($data) {
		// Checking if a user has been assigned multiple times.
        $duplicates = array_count_values($data);
        for($i = 1; $i <= 16; $i++) {
            if($duplicates[$data['player' . $i]] > 1) {
                return false;
            }
        }

        $currentTournament = $this->currentTournament();

        for($i = 1; $i <= 8; $i++) {
        	$p2 = $i * 2;
        	$p1 = $p2 - 1;

            $this->Game->create();
        	$this->Game->save(array(
        		'home_id' 	 => $data['player' . $p1],
        		'away_id' 	 => $data['player' . $p2],
        		'created' 	 => '0000-00-00 00:00:00',
        		'tournament_id' => $currentTournament['Tournament']['id']
        	));

            $this->create();
            $this->save(array(
                'stepAssoc' => $this->stepAssoc[1],
                'step' 	    => 1,
                'spot' 	    => $i,
                'game_id'   => $this->Game->id
            ));

            $this->Game->save(
                array(
                    'playoff_id' => $this->id,
                )
            );
        }

        return true;
	}

	// Return playoff attendees for drop-down.
	public function attendees() {

        return ClassRegistry::init('User')->findAllUsersInGroupStage();
	}

    /**
     * @param $attendees @TODO Apparently this parameter is not even needed.
     * @param int $userId The user the opponents are to be found of. If not provided it's the currently logged in user.
     * @return array What's left of $attendees after users $userId isn't allowed to play against are removed.
     */
    public function allowedOpponents($attendees, $userId = null) {
		$userId = $userId === null ? AuthComponent::user('id') : $userId;
		$Game = ClassRegistry::init('Game');

		$Game->unbindModel(
	        array('hasMany' => array('Tournament'))
	    );

        $currentTournament = $this->currentTournament();

		$nextGame = $Game->find('first', array(
			'conditions' => array(
				'OR' => array(
					'Game.home_id' => $userId,
					'Game.away_id' => $userId
				),
				'Game.group_id' => 0,
				'Game.reporter_id' => 0,
                'Game.tournament_id' => $currentTournament['Tournament']['id']
			)
		));

		if($nextGame['Game']['home_id'] == $userId)
			$oppId = $nextGame['Game']['away_id'];
		else
			$oppId = $nextGame['Game']['home_id'];

		$oppName = ClassRegistry::init('User')->field(
			'username', array('User.id' => $oppId));

		return array($oppId => $oppName);
	}

	// Returns the game $user has to play.
	public function currentGame($user = false) {
		$user = $user ? $user : AuthComponent::user('id');

		$opps = $this->attendees();
		$opps = $this->allowedOpponents($opps, $user);

        $this->Game->Playoff->recursive = 1;
		return $this->Game->Playoff->find('first', array(
			'conditions' => array(
				'Game.home_id' => array($user, key($opps)),
				'Game.away_id' => array($user, key($opps)),
			)
		));
	}

    /**
     * Collects information and summarizes them for the groups page to display.
     *
     * @param $tournamentId The tournament to find the groups of.
     * @internal param bool $bet True if user has triggered a bet on a game, false otherwise.
     * @return array The data needed for the view.
     */
    public function findForPlayoffsPage($tournamentId = null) {
        if ($tournamentId == null) {
            $currentTournament = $this->currentTournament();
            $tournamentId = $currentTournament['Tournament']['id'];
        }

        $this->unbindModel(array('hasMany' => array('Tournament')));

        $this->Game->recursive = 1;

        $playoff = $this->Game->find('all', array(
            'conditions' => array(
                'Game.playoff_id !=' => 0,
                'Game.group_id' => 0,
                'Game.tournament_id' => $tournamentId
            )
        ));

        foreach($playoff as $key => $val) {
            $needle = array(
                'step' => $val['Playoff']['step'],
                'spot' => $val['Playoff']['spot']
            );

            $newKey = array_search($needle, $this->keyAssoc);
            $game[$newKey] = $val;
        }

        $playoff = $game;

        $Rating = ClassRegistry::init('Rating');
        $Trace = ClassRegistry::init('Trace');

        foreach($playoff as $key => $val) {
            $playoff[$key]['Rating'][0] = $Rating->ratingStats($playoff[$key]['Game']['id']);

            $bet_h = $Trace->check('Bet', 'add', 'bet_h', $playoff[$key]['Playoff']['game_id'], 'read');
            $bet_a = $Trace->check('Bet', 'add', 'bet_a', $playoff[$key]['Playoff']['game_id'], 'read');

            $playoff[$key]['Playoff']['bet_h_traced'] = ($bet_h == null) ? false : true;
            $playoff[$key]['Playoff']['bet_a_traced'] = ($bet_a == null) ? false : true;

            $playoff[$key]['Playoff']['bets'] = $this->betStats($playoff[$key]['Playoff']['game_id']);
        }

        // If there was no third place match.
        if (empty($playoff[15])) {
            $playoff[15] = $playoff[14];
            $playoff[14] = array();
        }

        return $playoff;
    }

    public function isPaired($tournamentId = false) {
        if ($tournamentId === false) {
            $currentTournament = $this->currentTournament();
            $tournamentId = $currentTournament['Tournament']['id'];
        }

        $numberOfPlayoffGamesOfTournament = $this->Game->find(
            'count',
            array(
                'conditions' => array(
                    'Game.tournament_id' => $tournamentId,
                    'Game.playoff_id !=' => 0
                )
            )
        );

        return (bool) $numberOfPlayoffGamesOfTournament;
    }
}
