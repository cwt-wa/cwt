<?php

App::uses('Restore', 'Model');

class RestoreTest extends CakeTestCase {
    
    public $fixtures = array('app.restore');

    public function setUp() {
        parent::setUp();
        $this->Restore = ClassRegistry::init('Restore');
    }

    public function testValidateTournament() {
        for ($i = -8; $i < 13; $i++) {
            if ($i > 0 && $i < 9) {
                $expected = true;
            } else {
                $expected = false;
            }
            $result = $this->Restore->validateTournament(array(
                'tournament_id' => $i
            ));

            $this->assertEqual($result, $expected);
        }
    }
    
    public function testValidatePlayers() {
        $this->User = ClassRegistry::init('User');
        $numberOfUsers = $this->User->find('count');
        
        for ($i = -8; $i <= $numberOfUsers + 10; $i++) {
            $this->Restore->data = array(
                'Restore' => array(
                    'home_id' => $i,
                    'away_id' => $i - rand(1, $numberOfUsers)
                )
            );
            
            $homeIdExists = $this->User->find('count', array(
                'conditions' => array(
                    'User.id' =>
                        $this->Restore->data['Restore']['home_id']
                )
            ));          
            $awayIdExists = $this->User->find('count', array(
                'conditions' => array(
                    'User.id' =>
                        $this->Restore->data['Restore']['away_id']
                )
            ));
            
            if ($this->Restore->data['Restore']['home_id'] ==
                    $this->Restore->data['Restore']['away_id']) {
                $expected = false;
            } elseif ($this->Restore->data['Restore']['home_id'] < 1
                    || $this->Restore->data['Restore']['away_id'] < 1) {
                $expected = false;
            } elseif ($homeIdExists < 1 || $awayIdExists < 1) {
                $expected = false;
            } else {
                $expected = true;
            }
            
            $result = $this->Restore->validatePlayers(array(
                'home_id' => $this->Restore->data['Restore']['home_id']
            ));
            
            $this->assertEqual($result, $expected,
                    'Can ' . $this->Restore->data['Restore']['home_id'] .
                    ' play against ' .
                    $this->Restore->data['Restore']['away_id'] . '?');
        }
        
        $result = $this->Restore->validatePlayers(array(
            'home_id' => $this->Restore->data['Restore']['away_id']
        ));
        
        $this->assertEqual($result, false,
                'One cant play against hismelf.');
    }
    
    public function testValidateGame() {
        // $this->Restore->data['Restore']['stage'] isn't provided.
        // In that case it should return true.
        $this->assertEqual(
                $this->Restore->validateGame(array('home_id' => 1)), true);
        
        $this->Restore->data = array(
            'Restore' => array(
                'tournament_id' => 3,
                'home_id' => 11,
                'away_id' => 10,
                'score_h' => 2,
                'score_a' => 0,
                'stage' => 'Group A',
            )
        );
        
        $result1 = $this->Restore->validateGame(array('home_id' => 11));
        $this->assertEqual($result1, false,
                '11 vs 10 has already been played twice it group A');
        
        $this->Restore->data['Restore']['away_id'] = 11;
        $result1 = $this->Restore->validateGame(array('home_id' => 10));
        $this->assertEqual($result1, false,
                'it shouldn\'t play a role who is home or away');
        
        $this->Restore->data['Restore']['tournament_id'] = 5;
        $result2 = $this->Restore->validateGame(array('home_id' => 11));
        $this->assertEqual($result2, true, 'Yes, 11 and 10 have already
            met, but not in Tournament #5');
        
        $this->Restore->data['Restore']['away_id'] = 73;
        $this->Restore->data['Restore']['stage'] = 'Group F';
        $result2 = $this->Restore->validateGame(array('home_id' => 72));
        $this->assertEqual($result2, true, 
                '73 vs 72 can be palyed one more time');
        
        $this->Restore->data['Restore']['tournament_id'] = 3;
        $this->Restore->data['Restore']['away_id'] = 101;
        $this->Restore->data['Restore']['stage'] = 'Last Sixteen';
        $result3 = $this->Restore->validateGame(array('home_id' => 100));
        $this->assertEqual($result3, false,
                'This game has already been played in the playoff');
        
        $this->Restore->data['Restore']['away_id'] = 100;
        $this->Restore->data['Restore']['stage'] = 'Last Sixteen';
        $this->Restore->data['Restore']['tournament_id'] = 3;
        $result4 = $this->Restore->validateGame(array('home_id' => 101));
        $this->assertEqual($result4, false,
                'This game has already been played in the playoff');
        
        $this->Restore->data['Restore']['away_id'] = 101;
        $this->Restore->data['Restore']['stage'] = 'Final';
        $result5 = $this->Restore->validateGame(array('home_id' => 100));
        $this->assertEqual($result5, false,
                'This game has already been played in the playoff');
        
        $this->Restore->data['Restore']['away_id'] = 56;
        $this->Restore->data['Restore']['stage'] = 'Quarterfinal';
        $result5 = $this->Restore->validateGame(array('home_id' => 94));
        $this->assertEqual($result5, true,
                'Why not? 56 and 94 haven\'t yet met in palyoff.');
    }
    
    public function testValidateResult() {
        $this->Restore->data['Restore'] = array();
        
        $this->Restore->data['Restore']['score_h'] = 10;
        $this->Restore->data['Restore']['stage'] = "Final";
        $result = $this->Restore->validateResult(array('score_a' => 11));
        $this->assertEqual($result, false,
                "10 - 11, that doesnt seem right.");
        
        $this->Restore->data['Restore']['score_h'] = 2;
        $this->Restore->data['Restore']['stage'] = "Group A";
        $result = $this->Restore->validateResult(array('score_a' => 2));
        $this->assertEqual($result, false,
                "Scores cannot be equal.");
        
        $this->Restore->data['Restore']['score_h'] = 3;
        $this->Restore->data['Restore']['stage'] = "Last Sixteen";
        $result = $this->Restore->validateResult(array('score_a' => 0));
        $this->assertEqual($result, true, "2-0 that fricking right!");
        
        $this->Restore->data['Restore']['score_h'] = 4;
        $this->Restore->data['Restore']['stage'] = "Group B";
        $result = $this->Restore->validateResult(array('score_a' => 0));
        $this->assertEqual($result, false,
                "4-0 doesnt seem right for a group stag game.");
        
        $this->Restore->data['Restore']['score_h'] = 4;
        $this->Restore->data['Restore']['stage'] = "Third Place";
        $result = $this->Restore->validateResult(array('score_a' => 0));
        $this->assertEqual($result, true,
                "4-0 is okay for Third Place.");
        
        $this->Restore->data['Restore']['stage'] = "Final";
        $result = $this->Restore->validateResult(array('score_a' => 3));
        $this->assertEqual($result, true,
                "4-3 is okay for Final");
        
        $this->Restore->data['Restore']['score_h'] = 4;
        $this->Restore->data['Restore']['stage'] = "Semifinal";
        $result = $this->Restore->validateResult(array('score_a' => 0));
        $this->assertEqual($result, false,
                "4-0 is not okay for semifainal");        
    }
    
}
