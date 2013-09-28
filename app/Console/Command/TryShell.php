<?php

App::uses('Component', 'Auth');

class TryShell extends AppShell {

    public $uses = array('User', 'Tournament', 'Group', 'Playoff', 'Standing');

    public function main() {
        $userId = 2;
        $tournamentId = 11;

        $standings = $this->Standing->find(
            'all',
            array(
                'conditions' => array(
                    'Standing.user_id' => $userId,
                    'Group.tournament_id' => $tournamentId
                )
            )
        );

        print_r($standings);
    }
}
