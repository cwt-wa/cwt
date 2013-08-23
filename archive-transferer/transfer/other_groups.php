<?php

$db = mysqli_connect('127.0.0.1', 'root', '', 'cwt_archive');
if (mysqli_connect_errno($db)) {
    die('Failed to connect to database: ' . mysqli_connect_error());
}

$tournamentId = 4;
$sqlRestores = "SELECT * FROM `restores` WHERE `tournament_id`=$tournamentId AND `stage` LIKE 'Group%'";
$resultRestores = mysqli_query($db, $sqlRestores);
$groups = array(
    'Group A' => array(
        'label' => 'A',
        'id' => 57
    ),
    'Group B' => array(
        'label' => 'B',
        'id' => 58
    ),
    'Group C' => array(
        'label' => 'C',
        'id' => 59
    ),
    'Group D' => array(
        'label' => 'D',
        'id' => 60
    ),
    'Group E' => array(
        'label' => 'E',
        'id' => 61
    ),
    'Group F' => array(
        'label' => 'F',
        'id' => 62
    ),
    'Group G' => array(
        'label' => 'G',
        'id' => 63
    ),
    'Group H' => array(
        'label' => 'H',
        'id' => 64
    )
);

$groupsAssoc = array('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H');
for ($i = 0; $i < 8; $i++) {
    mysqli_query($db, "INSERT INTO `groups` (`tournament_id`, `label`) VALUES ($tournamentId, '$groupsAssoc[$i]')");
}

function userAlreadyInGroup($db, $userId, $newGroupId) {
    $sqlUserAlreadyInGroup = "SELECT * FROM `standings` WHERE `group_id`=$newGroupId AND `user_id`=$userId";
    $resultUserAlreadyInGroup = mysqli_query($db, $sqlUserAlreadyInGroup);

    if (mysqli_num_rows($resultUserAlreadyInGroup) < 1) {
        return false;
    }
    $userAlreadyInGroup = mysqli_fetch_array($resultUserAlreadyInGroup);
    return $userAlreadyInGroup['group_id'];
}

function findCurrentStandings($db, $userId, $newGroupId) {
    $sqlCurrentStandings = "SELECT * FROM `standings` WHERE `user_id`=$userId AND `group_id`=$newGroupId";
    $resultCurrentStandings = mysqli_query($db, $sqlCurrentStandings);
    $currentStandings = mysqli_fetch_array($resultCurrentStandings);
    return $currentStandings;
}

while ($game = mysqli_fetch_array($resultRestores)) {
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
        case '2-0':
            $winnerPoints = 3;
            $loserPoints = 0;
            break;
        case '2-1':
            $winnerPoints = 3;
            $loserPoints = 1;
            break;
        default:
            echo 'No matching result found for game #' . $game['id'] . '.';
            continue 2;
    }

    $label = $groups[$game['stage']]['label'];
    $newGroupId = $groups[$game['stage']]['id'];

    $winnerAlreadyInGroup = userAlreadyInGroup($db, $winnerId, $newGroupId);
    $loserAlreadyInGroup = userAlreadyInGroup($db, $loserId, $newGroupId);

    if (!$winnerAlreadyInGroup || !$loserAlreadyInGroup) {
        if ($winnerAlreadyInGroup == false) {
            $sqlAddToStandings = "INSERT INTO `standings` (`group_id`, `user_id`) VALUES ('$newGroupId', '$winnerId')";
            mysqli_query($db, $sqlAddToStandings);
        }

        if ($loserAlreadyInGroup == false) {
            $sqlAddToStandings = "INSERT INTO `standings` (`group_id`, `user_id`) VALUES ('$newGroupId', '$loserId')";
            mysqli_query($db, $sqlAddToStandings);
        }
    }

    $winnerCurrentStandings = findCurrentStandings($db, $winnerId, $newGroupId);
    $loserCurrentStandings = findCurrentStandings($db, $loserId, $newGroupId);

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
    $sqlUpdateLoser = "UPDATE `standings`
        SET
            `points`=$loserNewPoints,
            `games`=$loserNewGames,
            `game_ratio`=$loserNewGameRatio,
            `round_ratio`=$loserNewRoundRatio
        WHERE
            `user_id`=$loserId";
    mysqli_query($db, $sqlUpdateLoser);

    $sqlUpdateGame = "UPDATE `games`
        SET
          `group_id`=$newGroupId
        WHERE
          `home_id`=$game[home_id] AND
          `away_id`=$game[away_id] AND
          `score_h`=$game[score_h] AND
          `score_a`=$game[score_a] AND
          `reporter_id`=$game[reporter_id] AND
          `tournament_id`=$game[tournament_id] AND
          `created`='$game[reported]'";
    mysqli_query($db, $sqlUpdateGame);
}
