<?php

App::uses('AppShell', 'Console/Command');

/**
 * Generate Standings from Games. Updates, overwrites, doesn't generate new records. Important one.
 *
 * @property Standing Standing
 * @property Group Group
 */
class ArchiveShell extends AppShell
{

    public $uses = array('User', 'Tournament', 'Group', 'Standing', 'Game', 'Playoff');

    public function main()
    {
        $this->nullifyStandings();
        $this->Group->recursive = 1;
        $groups = $this->Group->find('all');
        foreach ($groups as $key => $group) {
            $this->reportGames($group['Game'], $group['Standing']);
        }
    }

    public function nullifyStandings($groupId = null)
    {
        if ($groupId === null) {
            return $this->Standing->query('UPDATE `standings` SET `points`=0, `games`=0, `game_ratio`=0, `round_ratio`=0');
        }

        $standings = $this->Standing->find('all', array(
            'conditions' => array(
                'group_id' => $groupId
            )
        ));
        $updatedStandings = array();

        foreach ($standings as $standingsKey => $standing) {
            $updatedStandings[] = array(
                'id' => $standing['Standing']['id'],
                'points' => 0,
                'games' => 0,
                'game_ratio' => 0,
                'round_ratio' => 0
            );
        }

        return $this->Standing->saveMany($updatedStandings);
    }

    public function reportGames($games, $standings)
    {
        foreach ($games as $key => $game) {
            $winnerAndLoser = $this->winnerAndLoser($game, $standings);

            switch ($game['tournament_id']) {
                case 1: // tournament of 2002
                case 2: // tournament of 2003
                case 3: // tournament of 2004
                case 4: // tournament of 2005
                case 5: // tournament of 2006
                case 6: // tournament of 2007
                case 7: // tournament of 2008
                case 8: // tournament of 2009
                case 9: // tournament of 2010
                    switch ($winnerAndLoser['score']) {
                        case '2-0':
                            $winnerAndLoser['winner']['points'] += 3;
                            $winnerAndLoser['loser']['points'] += 0;
                            break;
                        case '2-1':
                            $winnerAndLoser['winner']['points'] += 3;
                            $winnerAndLoser['loser']['points'] += 1;
                            break;
                    }
                    break;
                case 10: // tournament of 2011
                    switch ($winnerAndLoser['score']) {
                        case '3-0':
                            $winnerAndLoser['winner']['points'] += 4;
                            $winnerAndLoser['loser']['points'] += 0;
                            break;
                        case '3-1':
                            $winnerAndLoser['winner']['points'] += 3;
                            $winnerAndLoser['loser']['points'] += 1;
                            break;
                        case '3-2':
                            $winnerAndLoser['winner']['points'] += 3;
                            $winnerAndLoser['loser']['points'] += 2;
                            break;
                    }
                    break;
                default: // tournament of 2012, 2013, 2014 and later
                    switch ($winnerAndLoser['score']) {
                        case '3-0':
                        case '3-1':
                            $winnerAndLoser['winner']['points'] += 3;
                            $winnerAndLoser['loser']['points'] += 0;
                            break;
                        case '3-2':
                            $winnerAndLoser['winner']['points'] += 3;
                            $winnerAndLoser['loser']['points'] += 1;
                            break;
                    }
                    break;
            }

            $winnerAndLoser['winner']['games'] = $winnerAndLoser['winner']['games'] + 1;
            $winnerAndLoser['winner']['game_ratio'] = $winnerAndLoser['winner']['game_ratio'] + 1;
            $winnerAndLoser['winner']['round_ratio'] = $winnerAndLoser['winner']['round_ratio']
                - $winnerAndLoser['score'][2] + $winnerAndLoser['score'][0];

            $winnerAndLoser['loser']['games'] = $winnerAndLoser['loser']['games'] + 1;
            $winnerAndLoser['loser']['game_ratio'] = $winnerAndLoser['loser']['game_ratio'] - 1;
            $winnerAndLoser['loser']['round_ratio'] = $winnerAndLoser['loser']['round_ratio']
                - $winnerAndLoser['score'][0] + $winnerAndLoser['score'][2];

            $this->Standing->saveMany(array($winnerAndLoser['winner'], $winnerAndLoser['loser']));
        }
    }

    private function winnerAndLoser($game, $standings)
    {
        $winnerAndLoser = array();
        if ($game['score_h'] > $game['score_a'] ) {
            $winnerAndLoser['winner'] = $this->standingOfUser($game['home_id'], $standings);
            $winnerAndLoser['loser'] = $this->standingOfUser($game['away_id'], $standings);
            $winnerAndLoser['score'] = $game['score_h'] . '-' . $game['score_a'];
        } else {
            $winnerAndLoser['winner'] = $this->standingOfUser($game['away_id'], $standings);
            $winnerAndLoser['loser'] = $this->standingOfUser($game['home_id'], $standings);
            $winnerAndLoser['score'] = $game['score_a'] . '-' . $game['score_h'];
        }
        return $winnerAndLoser;
    }

    private function standingOfUser($userId, $standings)
    {
        foreach ($standings as $key => $standing) {
            if ($standing['user_id'] == $userId) {
                $freshlyFetchedStanding = $this->Standing->findById($standing['id']);
                return $freshlyFetchedStanding['Standing'];
            }
        }
    }
}
