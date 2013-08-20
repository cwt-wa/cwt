<?php

$db = mysqli_connect('127.0.0.1', 'root', '', 'cwt_archive');
if (mysqli_connect_errno($db)) {
    die('Failed to connect to database: ' . mysqli_connect_error());
}

$tournamentId = 11;
$sqlStandings = "SELECT * FROM `games` WHERE `tournament_id`=$tournamentId AND `playoff_id`=0";
$resultStandings = mysqli_query($db, $sqlStandings);
$groups = array(
    'A' => array(1, 2, 3, 4),
    'B' => array(5, 6, 7, 8),
    'C' => array(9, 10, 11, 12),
    'D' => array(13, 14, 15, 16),
    'E' => array(17, 18, 19, 20),
    'F' => array(21, 22, 23, 24),
    'G' => array(25, 26, 27, 28),
    'H' => array(29, 30, 31, 32)
);

function findLabel($groupId, $groups) {
    foreach ($groups as $key => $val) {
        if (in_array($groupId, $groups[$key])) {
            return $key;
        }
    }

    return false;
}

function findNewGroupId($currentGroupId, $groups) {
    $groupsAssoc = array('*', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H');

    foreach ($groups as $key => $val) {
        if (in_array($currentGroupId, $groups[$key])) {
            return array_search($key, $groupsAssoc);
        }
    }

    return false;
}

function userAlreadyInGroup($db, $label, $game, $userId, $newGroupId) {
    $sqlUserAlreadyInGroup = "SELECT * FROM `standings` WHERE `group_id`=$newGroupId AND `user_id`=$userId";
    $resultUserAlreadyInGroup = mysqli_query($db, $sqlUserAlreadyInGroup);

    if (mysqli_num_rows($resultUserAlreadyInGroup) < 1) {
        return false;
    }
    $userAlreadyInGroup = mysqli_fetch_array($resultUserAlreadyInGroup);
    return $userAlreadyInGroup['group_id'];
}

function findCurrentStandings($db, $userId) {
    $sqlCurrentStandings = "SELECT * FROM `standings` WHERE `user_id`=$userId";
    $resultCurrentStandings = mysqli_query($db, $sqlCurrentStandings);
    $currentStandings = mysqli_fetch_array($resultCurrentStandings);
    return $currentStandings;
}

while ($game = mysqli_fetch_array($resultStandings)) {
    $newGroupId = findNewGroupId($game['group_id'], $groups);
    $sqlUpdateGame = "UPDATE `games`
        SET
          `group_id`=$newGroupId
        WHERE
          `id`=$game[id]";
    mysqli_query($db, $sqlUpdateGame);
}
