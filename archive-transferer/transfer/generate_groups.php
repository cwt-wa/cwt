<?php

$dbRestore = mysqli_connect('127.0.0.1', 'root', '', 'cwt_archive');
if (mysqli_connect_errno($dbRestore)) {
    die('Failed to connect to database: ' . mysqli_connect_error());
}

$allGroups = array('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H');

$sql = 'SELECT * FROM `restores` WHERE `tournament_id`!=11';
$result = mysqli_query($dbRestore, $sql); echo mysqli_error($dbRestore);
while ($game = mysqli_fetch_array($result)) {
    $group = substr($game['stage'], -1);
    if (!in_array($group, $allGroups)) {
        continue;
    }

    $sqlHomeIdAlreadyInGroup = "SELECT * FROM `groups` WHERE
            `tournament_id`='$game[tournament_id]' AND `group`='$group' AND `user_id`='$game[home_id]'";
    $resultHomeIdAlreadyInGroup = mysqli_query($dbRestore, $sqlHomeIdAlreadyInGroup);
    $homeIdAlreadyInGroup = (bool) mysqli_num_rows($resultHomeIdAlreadyInGroup);

    if (!$homeIdAlreadyInGroup) {
        mysqli_query($dbRestore,
            "INSERT INTO `groups` (`tournament_id`, `group`, `user_id`) VALUES
        ('$game[tournament_id]', '$group', '$game[home_id]')");
    }

    $sqlAwayIdAlreadyInGroup = "SELECT * FROM `groups` WHERE
            `tournament_id`='$game[tournament_id]' AND `group`='$group' AND `user_id`='$game[away_id]'";
    $resultAwayIdAlreadyInGroup = mysqli_query($dbRestore, $sqlAwayIdAlreadyInGroup);
    $awayIdAlreadyInGroup = mysqli_num_rows($resultAwayIdAlreadyInGroup);

    if (!$awayIdAlreadyInGroup) {
        mysqli_query($dbRestore,
            "INSERT INTO `groups` (`tournament_id`, `group`, `user_id`) VALUES
        ('$game[tournament_id]', '$group', '$game[away_id]')");
    }
}
