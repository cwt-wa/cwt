<?php

/**
 * Updates User.timeline accurately.
 */
class TimelineShell extends AppShell
{

    public $uses = array('User', 'Tournament', 'Group', 'Standing', 'Game', 'Playoff');

    public function main()
    {
        $users = $this->User->find('list');
        $tournaments = $this->Tournament->find('list');
        $timelineOfCurrentUser = '';
        foreach ($users as $userId => $userUsername) {
            foreach ($tournaments as $tournamentId => $tournamentYear) {
                if (!$this->userParticipatedInTournament($userId, $tournamentId)) {
                    $timelineOfCurrentUser .= 0;
                    continue;
                }

                $timelineStepFromPlayoff = $this->timelineStepFromPlayoff($userId, $tournamentId);

                if ($timelineStepFromPlayoff == 0) {
                    $timelineOfCurrentUser .= 1;
                } else {
                    $timelineOfCurrentUser .= $timelineStepFromPlayoff;
                }
            }
            $this->User->save(
                array(
                    'id' => $userId,
                    'timeline' => $timelineOfCurrentUser
                ),
                false,
                array('timeline')
            );
            $timelineOfCurrentUser = '';
        }
    }

    private function userParticipatedInTournament($userId, $tournamentId)
    {
        $groups = $this->Group->find('list', array(
            'conditions' => array(
                'Group.tournament_id' => $tournamentId
            ),
            'fields' => array('Group.tournament_id')
        ));
        foreach ($groups as $groupId => $groupLabel) {
            $isInGroup = (bool)$this->Standing->find('count', array(
                'conditions' => array(
                    'Standing.group_id' => $groupId,
                    'Standing.user_id' => $userId
                )
            ));

            if ($isInGroup) {
                return true;
            }
        }
        return false;
    }

    private function timelineStepFromPlayoff($userId, $tournamentId)
    {
        $this->Game->recursive = 1;
        $playoffGamesOfUser = $this->Game->find('all', array(
            'conditions' => array(
                'Game.tournament_id' => $tournamentId,
                'Game.playoff_id !=' => 0,
                array(
                    'OR' => array(
                        'Game.home_id' => $userId,
                        'Game.away_id' => $userId
                    )
                )
            )
        ));

        if (empty($playoffGamesOfUser)) {
            return 0;
        }

        $reachedStep = 0;

        foreach ($playoffGamesOfUser as $playoffGameOfUser) {
            switch ($playoffGameOfUser['Playoff']['step']) {
                case 1:
                    if ($reachedStep < 2) {
                        $reachedStep = 2;
                    }
                    break;
                case 2:
                    if ($reachedStep < 3) {
                        $reachedStep = 3;
                    }
                    break;
                case 3:
                    if ($reachedStep < 4) {
                        $reachedStep = 4;
                    }
                    break;
                case 4:
                    if ($this->userWonGame($playoffGameOfUser['Game'], $userId)) {
                        $reachedStep = 5;
                    } else {
                        $reachedStep = 4;
                    }
                    break;
                case 5:
                    if ($this->userWonGame($playoffGameOfUser['Game'], $userId)) {
                        $reachedStep = 7;
                    } else {
                        $reachedStep = 6;
                    }
                    break;
                default:
                    $this->out('------------------- error with user #' . $userId . ' -------------------', 1, Shell::QUIET);
            }
        }
        return $reachedStep;
    }

    private function userWonGame($game, $userId)
    {
        if ($game['home_id'] == $userId) {
            if ($game['score_h'] > $game['score_a']) {
                return true;
            }
            return false;
        } else {
            if ($game['score_h'] > $game['score_a']) {
                return false;
            }
            return true;
        }
    }
}
