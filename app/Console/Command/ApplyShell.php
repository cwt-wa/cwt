<?php

class ApplyShell extends AppShell {

    public $uses = array('User', 'Application');

    public function main() {
        $this->populateApplications();
    }

    private function populateApplications() {
        // Starting off with 2, because I am already in.
        for ($i = 2; $i <= 32; $i++) {
            $this->Application->User->save(
                array(
                    'id' => $i,
                    'stage' => 'applied'
                )
            );

            $this->Application->save(
                array(
                    'user_id' => $i,
                )
            );
        }
    }
}
