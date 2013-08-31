<?php

$dbRestore = mysqli_connect('127.0.0.1', 'root', '', 'cwt_archive');
if (mysqli_connect_errno($dbRestore)) {
    die('Failed to connect to database: ' . mysqli_connect_error());
}

$sql = 'SELECT * FROM `restores`';
$result = mysqli_query($dbRestore, $sql); echo mysqli_error($dbRestore);

while ($restore = mysqli_fetch_array($result)) {
    $transferGame = mysqli_query(
        $dbRestore,
        "INSERT INTO `games`
        (`tournament_id`, `home_id`, `away_id`, `score_h`, `score_a`, `techwin`, `created`, `reporter_id`) VALUES
        ('$restore[tournament_id]', '$restore[home_id]', '$restore[away_id]', '$restore[score_h]', '$restore[score_a]',
        '$restore[tech_win]', '$restore[reported]', '$restore[reporter_id]')");

    if (!$transferGame) {
        echo "Error restoring #$restore[id]: " . mysqli_error($dbRestore) . "\n";
    }
}
