<?php

App::uses('Component', 'Auth');

/**
 * This shell is supposed to populate the database with some test records.
 * @TODO Make it fucking work.
 */
class PopulateShell extends AppShell {

    public $uses = array('User', 'Tournament', 'Application');

    public function main() {
        $this->populateApplications();
        //$this->populateUsers();
    }

    public function populateUsers() {
        App::import('Component', 'Auth');

        for ($i = 1; $i <= 32; $i++) {
            $this->User->create();
            $this->User->save(array(
                'username' => 'Player' . $i,
                'password' => 'player' . $i
            ), false);
        }
    }

    private function populateApplications() {
        for ($i = 1; $i <= 32; $i++) {
            $this->Application->create();
            $this->Application->save(array(
                'user_id' => $i
            ), false);
        }
    }
}
