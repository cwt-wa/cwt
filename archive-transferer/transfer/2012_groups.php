<?php

$db = mysqli_connect('127.0.0.1', 'root', '', 'cwt_archive');
if (mysqli_connect_errno($db)) {
    die('Failed to connect to database: ' . mysqli_connect_error());
}

$tournamentId = 11;
$sqlGames = "SELECT * FROM `games` WHERE `tournament_id`=$tournamentId";
$resultGames = mysqli_query($db, $sqlGames);
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

while ($game = mysqli_fetch_array($resultGames)) {
    if ($game['techwin']) {
        echo 'Who won technically decided game #' . $game['id'] . '?';
        continue;
    }

    if ($game['score_h'] > $game['score_a']) {
        $winnerId = $game['home_id'];
        $loserId = $game['away_id'];
        $winnerScore = $game['score_h'];
        $loserScore = $game['score_a'];
    } else {
        $winnerId = $game['away_id'];
        $loserId = $game['home_id'];
        $winnerScore = $game['score_a'];
        $loserScore = $game['score_h'];
    }

    $gameResult = $winnerScore . '-' . $loserScore;
    $winnerPoints = 0;
    $loserPoints = 0;
    switch ($gameResult) {
        case '3-0':
            $winnerPoints = 3;
            $loserPoints = 0;
            break;
        case '3-1':
            $winnerPoints = 3;
            $loserPoints = 0;
            break;
        case '3-2':
            $winnerPoints = 3;
            $loserPoints = 1;
            break;
        default:
            echo 'No matching result found for game #' . $game['id'] . '.';
            continue;
    }

    $label = findLabel($game['group_id'], $groups);

    if ($label == false) {
        echo "A label could not be found for game #$game[id] with group id $game[group_id].";
        continue;
    }

    $newGroupId = findNewGroupId($game['group_id'], $groups);

    $winnerAlreadyInGroup = userAlreadyInGroup($db, $label, $game, $winnerId, $newGroupId);
    $loserAlreadyInGroup = userAlreadyInGroup($db, $label, $game, $loserId, $newGroupId);

    if (!$winnerAlreadyInGroup || !$loserAlreadyInGroup) {
        if ($newGroupId == false) {
            echo "Couldn't find new group id for game #$game[id].";
            continue;
        }

        if ($winnerAlreadyInGroup == false) {
            $sqlAddToStandings = "INSERT INTO `standings` (`group_id`, `user_id`) VALUES ('$newGroupId', '$winnerId')";
            mysqli_query($db, $sqlAddToStandings);
        }

        if ($loserAlreadyInGroup == false) {
            $sqlAddToStandings = "INSERT INTO `standings` (`group_id`, `user_id`) VALUES ('$newGroupId', '$loserId')";
            mysqli_query($db, $sqlAddToStandings);
        }
    }

    $winnerCurrentStandings = findCurrentStandings($db, $winnerId);
    $loserCurrentStandings = findCurrentStandings($db, $loserId);

    $winnerNewPoints = $winnerCurrentStandings['points'] + $winnerPoints;
    $winnerNewGames = $winnerCurrentStandings['games'] + 1;
    $winnerNewGameRatio = $winnerCurrentStandings['game_ratio'] + 1;
    $winnerNewRoundRatio = $winnerCurrentStandings['round_ratio'] + $winnerScore - $loserScore;

    $loserNewPoints = $loserCurrentStandings['points'] + $loserPoints;
    $loserNewGames = $loserCurrentStandings['games'] + 1;
    $loserNewGameRatio = $loserCurrentStandings['game_ratio'] - 1;
    $loserNewRoundRatio = $loserCurrentStandings['round_ratio'] + $loserScore - $winnerScore;

    $sqlUpdateWinner = "UPDATE `standings`
        SET
            `points`=$winnerNewPoints,
            `games`=$winnerNewGames,
            `game_ratio`=$winnerNewGameRatio,
            `round_ratio`=$winnerNewRoundRatio
        WHERE
            `user_id`=$winnerId";
    mysqli_query($db, $sqlUpdateWinner);
    $sqlUpdateLoser = "UPDATE `standings`s
        SET
            `points`=$loserNewPoints,
            `games`=$loserNewGames,
            `game_ratio`=$loserNewGameRatio,
            `round_ratio`=$loserNewRoundRatio
        WHERE
            `user_id`=$loserId";
    mysqli_query($db, $sqlUpdateLoser);
}
