<?php
App::uses('AppModel', 'Model');

/**
 * Class Group
 *
 * @property User User
 * @property Standing Standing
 * @property Game Game
 */
class Group extends AppModel
{

    public $name = 'Group';
    public $displayField = 'group';
    public $hasMany = array(
        'Game' => array(
            'className' => 'Game',
            'foreignKey' => 'group_id',
            'conditions' => array('Game.playoff_id' => 0)
        ),
        'Standing' => array(
            'className' => 'Standing',
            'foreignKey' => 'group_id'
        )
    );

    /**
     * Find the group the given user is or was in.
     *
     * @param null $userId The user's id to find the group of or the currently logged in user if the param wasn't given.
     * @return String The capital letter of the group or null if th user was not found in any group.
     */
    public function findGroup($userId = null, $tournamentId = null)
    {
        $userId = $userId ? $userId : AuthComponent::user('id');

        $this->Standing->recursive = 1;

        $conditions = array(
            'Standing.user_id' => $userId
        );

        if ($tournamentId !== null) {
            $conditions['Group.tournament_id'] = $tournamentId;
        }


        $group = $this->Standing->find('first', array(
            'conditions' => $conditions
        ));

        if (empty($group)) {
            return null;
        }

        return $group['Group']['label'];
    }

    // Assign players to the groups.

    public function start($data)
    {
        // Checking if a user has been assigned multiple times.
        $duplicates = array_count_values($data);
        for ($i = 1; $i <= 32; $i++) {
            if ($duplicates[$data['player' . $i]] > 1) {
                return false;
            }
        }

        $currentTournament = $this->currentTournament();

        $groups = array('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H');
        $playerCounter = 1;

        foreach ($groups as $group) {
            $this->create();
            $this->save(array(
                'tournament_id' => $currentTournament['Tournament']['id'],
                'label' => $group,
            ));

            for ($i = 0; $i < 4; $i++) {
                $this->Standing->create();
                $this->Standing->save(array(
                    'group_id' => $this->id,
                    'user_id' => $data['player' . $playerCounter]
                ));
                $playerCounter++;
            }
        }

        return true;
    }

    /**
     * How many users have applied for participation?
     *
     * @return integer number of users who applied.
     */
    public function numberOfApplicants()
    {
        $applicants = ClassRegistry::init('Application')->find('count');
        return $applicants;
    }

    /**
     * Get all the users the given user is allowed to report a game against.
     *
     * @param $attendees Provide it with this class's attendees() method. @TODO This method should do that itself.
     * @param int $userId The user the opponents are to be found of. If not provided it's the currently logged in user.
     * @return array What's left of $attendees after users $userId isn't allowed to play against are removed.
     */
    public function allowedOpponents($attendees, $userId = null)
    {
        $userId = $userId ? $userId : AuthComponent::user('id');
        $currentTournament = $this->currentTournament();

        foreach ($attendees as $groupmateID => $ID) {
            // Remove yourself from opponents.
            if ($groupmateID == $userId) {
                unset($attendees[$groupmateID]);
            }

            // Remove players you've already played against.
            $playedGame[1] = $this->Game->find('count', array(
                'conditions' => array(
                    'Game.home_id' => $userId,
                    'Game.away_id' => $groupmateID,
                    'Game.tournament_id' => $currentTournament['Tournament']['id']
                )
            ));
            $playedGame[2] = $this->Game->find('count', array(
                'conditions' => array(
                    'Game.home_id' => $groupmateID,
                    'Game.away_id' => $userId,
                    'Game.tournament_id' => $currentTournament['Tournament']['id']
                )
            ));
            if ($playedGame[1] || $playedGame[2]) {
                unset($attendees[$groupmateID]);
            }
        }

        return $attendees;
    }

    /**
     * Get the users of a group as CakePHP list.
     * @TODO Misleading method name.
     *
     * @param $groupString
     * @return array
     */
    public function attendees($groupString)
    {
        $currentTournament = $this->currentTournament();

        $attendees = array();

        $users = $this->Standing->find(
            'all',
            array(
                'conditions' => array(
                    'Group.tournament_id' => $currentTournament['Tournament']['id'],
                    'Group.label' => $groupString
                )
            )
        );

        foreach ($users as $user) {
            $attendees[$user['User']['id']] = $user['User']['username'];
        }

        return $attendees;
    }

    /**
     * Replaces an inactive player by an active player.
     * Games of the inactive player will be voided and the table refreshed accordingly.
     *
     * @param $inactiveUserId
     * @param $activeUserId
     * @return bool True on success, false otherwise.
     */
    public function replacePlayer($inactiveUserId, $activeUserId)
    {
        $User = ClassRegistry::init('User');
        $User->recursive = 2;
        $inactiveUser = $User->findById($inactiveUserId);
        $currentTournamentStanding = $this->Standing->getStandingForTournament($inactiveUser['Standing']);

        if (!$currentTournamentStanding) {
            return false;
        }

        $currentTournament = $this->currentTournament();
        $gamesToVoid = $this->Game->find('list', array(
            'conditions' => array(
                'tournament_id' => $currentTournament['Tournament']['id'],
                'group_id !=' => 0,
                'playoff_id' => 0,
                'OR' => array(
                    'Game.home_id' => $inactiveUserId,
                    'Game.away_id' => $inactiveUserId
                )
            )
        ));

        foreach ($gamesToVoid as $gameToVoid) {
            $this->Game->delete($gameToVoid);
        }

        App::uses('ArchiveShell', 'Console/Command');
        $ArchiveShell = new ArchiveShell();

        $nullifyStandings = $ArchiveShell->nullifyStandings($currentTournamentStanding['group_id']);
        if (!$nullifyStandings) {
            return false;
        }

        $this->recursive = 1;
        $group = $this->findById($currentTournamentStanding['group_id']);
        $ArchiveShell->reportGames($group['Game'], $group['Standing']);

        $stageUpdate = $User->saveMany(array(
            array(
                'id' => $inactiveUserId,
                'stage' => 'retired'
            ),
            array(
                'id' => $activeUserId,
                'stage' => 'group'
            )
        ));

        if ($stageUpdate) {
            return $this->Standing->save(array(
                'id' => $currentTournamentStanding['id'],
                'user_id' => $activeUserId
            ));
        } else {
            return false;
        }
    }

    // A new group game has been reported. Call by GameModel.

    public function updateReport($oldwinner, $oldloser, $score_w, $score_l)
    {
        $result = $score_w . '-' . $score_l;

        switch ($result) {
            case '3-0':
                $newWinnerPoints = $oldwinner['points'] + 3;
                $newLoserPoints = $oldloser['points'];
                break;
            case '3-1':
                $newWinnerPoints = $oldwinner['points'] + 3;
                $newLoserPoints = $oldloser['points'];
                break;
            case '3-2':
                $newWinnerPoints = $oldwinner['points'] + 3;
                $newLoserPoints = $oldloser['points'] + 1;
        }

        $newWinner = array(
            'id' => $oldwinner['id'],
            'user_id' => $oldwinner['user_id'],
            'points' => $newWinnerPoints,
            'games' => $oldwinner['games'] + 1,
            'game_ratio' => $oldwinner['game_ratio'] + 1,
            'round_ratio' => $oldwinner['round_ratio'] - $score_l + $score_w
        );
        $newLoser = array(
            'id' => $oldloser['id'],
            'user_id' => $oldloser['user_id'],
            'points' => $newLoserPoints,
            'games' => $oldloser['games'] + 1,
            'game_ratio' => $oldloser['game_ratio'] - 1,
            'round_ratio' => $oldloser['round_ratio'] - $score_w + $score_l
        );

        $this->Standing->save($newWinner);
        $this->Standing->save($newLoser);
    }

    /**
     * Collects information and summarizes them for the groups page to display.
     *
     * @param int $tournamentId The tournament to find the groups of.
     * @return array The data needed for the view.
     */
    public function findForGroupsPage($tournamentId = null)
    {
        if ($tournamentId == null) {
            $currentTournament = $this->currentTournament();
            $tournamentId = $currentTournament['Tournament']['id'];
        }

        $Rating = ClassRegistry::init('Rating');
        $groupArray = array('*', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H');
        $groups = $this->find('list', array(
            'conditions' => array(
                'tournament_id' => $tournamentId
            ),
            'fields' => array('Group.id')
        ));
        $group = array();
        $games = $this->Game->recursive = 1;
        $games = $this->Game->find('all', array(
            'conditions' => array(
                'Game.tournament_id' => $tournamentId,
                'Game.playoff_id' => 0, // Why do I need to specify this, I've already done that on the model association?
                'Game.group_id !=' => 0
            ),
            'order' => 'Game.created DESC'
        ));

        for ($i = 1; $i <= 8; $i++) {
            $currentlyLoopedGroupId = array_slice($groups, $i - 1, $i);
            $currentlyLoopedGroupId = $currentlyLoopedGroupId[0];
            $group[$i]['group'] = $groupArray[$i];

            $this->Standing->recursive = 0;
            $groupAll = $this->Standing->find('all', array(
                'conditions' => array(
                    'Group.tournament_id' => $tournamentId,
                    'Group.label' => $groupArray[$i]
                ),
                'order' => 'Standing.points DESC, Standing.game_ratio DESC, Standing.round_ratio DESC'
            ));

            $orderedStanding = $this->orderByHeadToHeadRecord($groupAll);

            for ($i2 = 0; $i2 < count($orderedStanding); $i2++) {
                $group[$i][$i2 + 1] = array(
                    'User' => $orderedStanding[$i2]['User'],
                    'Group' => $orderedStanding[$i2]['Group'],
                    'Standing' => $orderedStanding[$i2]['Standing']
                );
                $group[$i][$i2 + 1]['User']['flag'] = $this->Standing->User->Profile->displayCountry($orderedStanding[$i2]['User']['id']);
            }

            $cGames = 1;
            foreach ($games as $game) {
                if ($currentlyLoopedGroupId == $game['Group']['id']) {
                    $group[$i]['Game'][$cGames] = $game;
                    $group[$i]['Game'][$cGames]['Rating'][0] = $Rating->ratingStats($game['Game']['id']);
                    $cGames++;
                }
            }
        }
        return $group;
    }

    public function orderByHeadToHeadRecord($standing)
    {
        $currentTournament = $this->currentTournament();
        $newStanding = $standing;

        $standingCount = count($standing);
        for ($i = 0; $i < $standingCount; $i++) {
            if (!array_key_exists($i + 1, $standing)) {
                continue;
            }

            $nextStanding = $standing[$i + 1];

            if (!($standing[$i]['Standing']['points'] === $nextStanding['Standing']['points'] &&
                $standing[$i]['Standing']['game_ratio'] === $nextStanding['Standing']['game_ratio'] &&
                $standing[$i]['Standing']['round_ratio'] === $nextStanding['Standing']['round_ratio'])) {
                continue;
            }

            if ($standing[$i]['Standing']['games'] === '0' && $nextStanding['Standing']['games'] === '0') {
                continue;
            }

            $groupStageGameOfUsers = $this->Game->findGroupStageGameOfUsers(
                $standing[$i]['User']['id'], $nextStanding['User']['id'], $currentTournament['Tournament']['id']);

            if (!$groupStageGameOfUsers) {
                break;
            }

            $winnerId = $this->Game->getWinnerIdOfGame($groupStageGameOfUsers);

            if ($winnerId !== $standing[$i]['User']['id']) {
                $newStanding[$i] = $standing[$i + 1];
                $newStanding[$i + 1] = $standing[$i];
                return $this->orderByHeadToHeadRecord($newStanding);
            }
        }

        return $standing;
    }
}
