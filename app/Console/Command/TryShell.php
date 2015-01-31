<?php

App::uses('Component', 'Auth');
App::uses('AppShell', 'Console/Command');

/**
 * @property Stream Stream
 */
class TryShell extends AppShell {

    public $uses = array('User', 'Tournament', 'Group', 'Playoff', 'Standing', 'Stream', 'Game');

    public function main() {
        $titles = "";
        $streams = $this->Stream->find('all');
        foreach ($streams as $streamKey => $stream) {
            $res = $this->Stream->callTwitchApi('channels/' . $stream['Stream']['provider'] . '/videos?limit=100');
            foreach ($res['videos'] as $videoKey => $video) {
                $titles .= $video['title'] . "\n";
            }
        }

        debug($titles);return;



        $this->Game->recursive = 1;
        $game = $this->Game->findById(1084);
        $possibleStreamsForGame = $this->Stream->fetchPossibleStreamsForGame($game);
        return $possibleStreamsForGame;
    }
}
