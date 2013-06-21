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
     * Prepends $message with user's IP address and (if logged in) username and Id.
     *
     * @param String $message The message that should be prepended with the user information.
     * @param Boolean $loggedIn Indicating whether the user is logged in or not.
     * @return String The final message prepended with user information.
     */
    public function prependUserInfo($message, $loggedIn) {
        if ($loggedIn) {
            $user = ' #' . AuthComponent::user('id')
                    . ' ' . AuthComponent::user('username');
        } else {
            $user = '';
        }

        return $this->getVisitorIp() . $user . ': ' . $message;
    }

    /**
     * Get visitor's IP address securely by dodging proxy servers.
     *
     * @return String The visitor's IP address.
     */
    public function getVisitorIp() {
        if (!empty($_SERVER['HTTP_CLIENT_IP'])) {
            return $_SERVER['HTTP_CLIENT_IP'];
        } elseif (!empty($_SERVER['HTTP_X_FORWARDED_FOR'])) {
            return $_SERVER['HTTP_X_FORWARDED_FOR'];
        } else {
            return $_SERVER['REMOTE_ADDR'];
        }
    }

    /**
     * Generates a string for the footer of the page with the patter "Crespo's Worms Tournament [year] by [mods]".
     *
     * @return String The string to be shown in the footer.
     */
    public function genCopyrightString() {
        $string = '<b>Crespo&apos;s Worms Tournament</b>';
        $Tournament = ClassRegistry::init('Tournament');
        $currentTournament = $Tournament->currentTournament();

        if ($currentTournament == null) {
            return $string;
        }

        $string .= ' <b>' . $currentTournament['Tournament']['year'] . '</b> by ';

        $moderators = array();
        foreach ($currentTournament['Moderators'] as $moderator) {
            $moderators[] = $moderator['username'];
        }

        App::uses('String', 'Utility');
        $string .= String::toList($moderators);
        return $string;
    }

    /**
     * Tells you if currently there's any user who can report games.
     * Basically games can be reported in the group and playoffs stage and this method checks for these stages.
     *
     * @return bool True if the games can be reported, false otherwise.
     */
    public function gamesCanBeReported() {
        $Tournament = ClassRegistry::init('Tournament');

        $currentTournament = $Tournament->currentTournament();

        if ($currentTournament['Tournament']['status'] == Tournament::GROUP
                || $currentTournament['Tournament']['status'] == Tournament::PLAYOFF) {
            return true;
        }

        return false;
    }
}
