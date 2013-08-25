<?php

$db = mysqli_connect('127.0.0.1', 'root', '', 'cwt_archive');
if (mysqli_connect_errno($db)) {
    die('Failed to connect to database: ' . mysqli_connect_error());
}

$tournamentId = 1; // id 10 = year 2011
$sqlRestores = "SELECT * FROM `restores` WHERE `tournament_id`=$tournamentId AND `stage` NOT LIKE 'Group%'";
$resultRestores = mysqli_query($db, $sqlRestores);

while ($restore = mysqli_fetch_array($resultRestores)) {
    switch ($restore['stage']) {
        case 'Last Sixteen':
            $step = 1;
            break;
        case 'Quarterfinal':
            $step = 2;
            break;
        case 'Semifinal':
            $step = 3;
            break;
        case 'Third Place':
            $step = 4;
            break;
        case 'Final':
            $step = 5;
            break;
        default:
            echo "No matching stage for restored game #$restore[id] with stage $restore[stage].\n";
            continue 2; // Next restored game. Breaks switch and continues while loop.
    }

    $sqlGame = "SELECT *
                FROM `games`
                WHERE `tournament_id`=$tournamentId
                AND `group_id`=0
                AND `home_id`=$restore[home_id]
                AND `away_id`=$restore[away_id]
                AND `score_h`=$restore[score_h]
                AND `score_a`=$restore[score_a]
                AND `techwin`=$restore[tech_win]
                AND `created`='$restore[reported]'
                AND `reporter_id`=$restore[reporter_id]";
    $resultGame = mysqli_query($db, $sqlGame);
    $numberOfFoundGames = mysqli_num_rows($resultGame);

    if ($numberOfFoundGames > 1) {
        echo "More than one corresponding game for restored game #$restore[id] found.\n";
        continue;
    } elseif ($numberOfFoundGames < 1) {
        echo "No corresponding game for restored game #$restore[id] found.\n";
        continue;
    }

    $game = mysqli_fetch_array($resultGame);

    $insertedPlayoffResult = mysqli_query($db,
        "INSERT INTO `playoffs` (`stepAssoc`, `step`, `game_id`) VALUES ('$restore[stage]', $step, $game[id])");

    if (!$insertedPlayoffResult) {
        echo "Restored game #$restore[id] failed to add to playoffs.\n";
        continue;
    }

    $insertedPlayoffId = mysqli_insert_id($db);

    $updatedGameResult = mysqli_query($db,
        "UPDATE `games` SET `playoff_id`=$insertedPlayoffId WHERE `id`=$game[id]");

    if (!$updatedGameResult) {
        echo "Restored game #$restore[id] with playoff id $insertedPlayoffId[id] failed to update corresponding game.\n";
        continue;
    }
}
