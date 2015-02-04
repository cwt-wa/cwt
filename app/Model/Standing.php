<?php

App::uses('AppModel', 'Model');

/**
 * Standings for group tables.
 */
class Standing extends AppModel
{

    public $name = 'Standing';
    public $displayField = 'id';
    public $belongsTo = array(
        'Group' => array(
            'className' => 'Group',
            'foreignKey' => 'group_id',
        ),
        'User' => array(
            'className' => 'User',
            'foreignKey' => 'user_id',
        )
    );
    public $validate = array(
        'group_id' => array(
            'numeric' => array(
                'rule' => array('numeric'),
            ),
        ),
        'points' => array(
            'numeric' => array(
                'rule' => array('numeric'),
            ),
        ),
        'games' => array(
            'numeric' => array(
                'rule' => array('numeric'),
            ),
        ),
        'game_ratio' => array(
            'numeric' => array(
                'rule' => array('numeric'),
            ),
        ),
        'round_ratio' => array(
            'numeric' => array(
                'rule' => array('numeric'),
            ),
        ),
    );

    /**
     * Input many standings of a specific user and this method will return the one standing that is from the
     * given tournament. Be careful! If multiple standings are from the same tournament, only the first found will be returned.
     *
     * @param $standings Array The standings to search in. Model['Standing']
     * @param null $tournamentId The tournament to return the standing for.
     * Defaults to current {@link AppModel#currentTournament}.
     * @return mixed The standing of the tournament or false if none matching to the current tournament was found.
     */
    public function getStandingForTournament($standings, $tournamentId = null)
    {
        $currentTournament = $this->currentTournament();
        $tournamentId = $tournamentId ? $tournamentId : $currentTournament['Tournament']['id'];
        foreach ($standings as $standing) {
            if ($standing['Group']['tournament_id'] == $tournamentId) {
                return $standing;
            }
        }
        return false;
    }
}
