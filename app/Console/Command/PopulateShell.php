<?php

/**
 * This shell is supposed to populate the database with some test records.
 * @TODO This shell.
 */
class PopulateShell extends AppShell {

    public $uses = array('User');

    public function main() {
        $this->populateUsers();
    }

    public function populateUsers() {
    }

    public function populateGames() {

    }
}
