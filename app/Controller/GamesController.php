<?php
App::uses('AppController', 'Controller');
App::uses('Folder', 'Utility');
App::uses('File', 'Utility');

class GamesController extends AppController
{
    public $name = 'Games';
    public $scaffold = 'admin';
    public $paginate = array(
        'limit' => 20,
        'order' => array(
            'Game.created' => 'desc'
        )
    );

    public function beforeFilter()
    {
        parent::beforeFilter();
        $this->Auth->allow('download');
    }


    public function index()
    {
        $this->Paginator->settings = array(
            'limit' => 20,
            'order' => array(
                'Game.created' => 'desc'
            )
        );

        $this->Game->recursive = 1;
        $games = $this->Paginator->paginate();
        $games = $this->Game->addRatingsToGames($games);

        $this->set('games', $games);
    }


    public function view($id = null)
    {
        $this->Game->id = $id;
        if (!$this->Game->exists()) {
            $this->Session->setFlash('This game does not exist.', 'default', array('class' => 'error'));
            $this->redirect($this->referer());
        }

        $this->helpers[] = 'Time';
        $this->helpers[] = 'Bbcode';
        $this->loadModel('Trace');

        // Deleting empty comments that were only saved, because the user
        // supplied a preview without actually posting the message.
        $this->Game->Comment->deleteAll(array(
            'Comment.message' => ''
        ), false
        );

        // Resetting the table key. Not necessary?
        //$this->Game->Comment->query('ALTER TABLE `comments` DROP `id`;');
        //$this->Game->Comment->query('ALTER TABLE `comments` AUTO_INCREMENT=1');
        //$this->Game->Comment->query('ALTER TABLE `comments` ADD `id` int UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY FIRST');

        $this->Game->recursive = 0;
        $game = $this->Game->read(null, $id);

        $game['Comment'] = $this->Game->Comment->find('all', array(
            'conditions' => array(
                'Comment.game_id' => $id
            ), 'order' => 'Comment.created DESC'
        ));

        // Handle group game.
        if ($game['Game']['group_id']) {
            $game['stage'] = 'Group ' . $game['Group']['label'];
            $game['winner']['username'] = $game['Home']['username'];
            $game['winner']['id'] = $game['Home']['id'];
        } elseif ($game['Game']['playoff_id']) { // Handle playoff game.
            $game['stage'] = $this->Game->Playoff->stepAssoc[$game['Playoff']['step']];

            if ($game['Game']['score_h'] > $game['Game']['score_a']) {
                $game['winner']['username'] = $game['Home']['username'];
                $game['winner']['id'] = $game['Home']['id'];
            } else {
                $game['winner']['username'] = $game['Away']['username'];
                $game['winner']['id'] = $game['Away']['id'];
            }

            $bet_h = $this->Trace->check('Bet', 'add', 'bet_h', $game['Game']['id'], 'read');
            $bet_a = $this->Trace->check('Bet', 'add', 'bet_a', $game['Game']['id'], 'read');

            $game['Playoff']['bet_h_traced'] = ($bet_h == null) ? false : true;
            $game['Playoff']['bet_a_traced'] = ($bet_a == null) ? false : true;

            $game['Playoff']['bets'] = $this->Game->Playoff->betStats($game['Playoff']['game_id']);
        }

        $this->loadModel('User');

        if ($this->User->displayPhoto($game['winner']['username']) != '_nophoto.jpg') {
            $game['winner']['photo'] = $this->User->displayPhoto($game['winner']['username']);
        }

        $this->set('game', $game);
    }


    public function add()
    {
        $currentTournament = $this->Game->currentTournament();

        if ($currentTournament['Tournament']['status'] == Tournament::GROUP
            && $this->Auth->user('stage') != 'group'
        ) {
            $this->Session->setFlash('You can\'t report any game.');
            $this->redirect(array('action' => 'index'));
        }
        if ($currentTournament['Tournament']['status'] == Tournament::PLAYOFF
            && $this->Auth->user('stage') != 'playoff'
        ) {
            $this->Session->setFlash('You can\'t report any game.');
            $this->redirect(array('action' => 'index'));
        }

        if ($this->request->is('post')) {
            $this->Game->create();
            if ($this->Game->report($this->request->data['Report'])) {
                $this->Auth->login($this->User->re_login());
                $this->Session->setFlash('The game has been reported.');
                $this->redirect('/games/view/' . $this->Game->id);
                return;
            } else {
                $errors = $this->Game->invalidFields();
                foreach ($errors as $key => $value) {
                    $this->Session->setFlash($errors[$key][0], 'default', array('class' => 'error'));
                    break;
                }
            }
        }

        $allowedResults = array(
            '0' => '0',
            '1' => '1',
            '2' => '2',
            '3' => '3'
        );

        $this->loadModel('Tournament');

        if ($currentTournament['Tournament']['status'] == Tournament::GROUP) {
            $this->Game->Group->Standing->recursive = 0;
            $group = $this->Game->Group->Standing->find(
                'first',
                array(
                    'conditions' => array(
                        'Standing.user_id' => $this->Auth->user('id'),
                        'Group.tournament_id' => $currentTournament['Tournament']['id']
                    )
                )
            );
            $opps = $this->Game->Group->attendees($group['Group']['label']);
            $opps = $this->Game->Group->allowedOpponents($opps);
        } elseif ($currentTournament['Tournament']['status'] == Tournament::PLAYOFF) {
            $currentGame = $this->Game->Playoff->currentGame();

            if (!$currentGame) {
                $this->Session->setFlash('You can\'t report any game.', 'default', array('class' => 'error'));
                $this->redirect(array('action' => 'index'));
            }

            if ($currentGame['Playoff']['step'] > 3) {
                $allowedResults['4'] = '4';
            }

            $opps = $this->Game->Playoff->attendees();
            $opps = $this->Game->Playoff->allowedOpponents($opps);
        }

        $this->set('opponents', $opps);
        $this->set('allowedResults', $allowedResults);
    }

    public function download($id)
    {
        $this->Game->id = $id;
        $game = $this->Game->read();

        if ($game['Game']['techwin']) {
            $this->Session->setFlash(
                'This game was technically decided. There is no replay file.',
                'default', array('class' => 'error')
            );
            $this->redirect($this->referer());
            return;
        }

        $this->viewClass = 'Media';

        $dir = new Folder('files/replays/');

        $replay = $dir->find("\[$id*\].*\.(rar|zip)$");

        if (empty($replay)) {
            $this->redirect('/tournaments/download/replays');
            return;
        }

        if ($this->Auth->loggedIn()) {
            $auth = $this->Auth->user('username') . ' (#' . $this->Auth->user('id') . ')';
        } else {
            $auth = 'false';
        }

        CakeLog::write('replays',
            'Game #' . $id
            . ': ' . $this->User->realIP()
            . ' authorized: ' . $auth
        );

        $this->Game->save(array(
            'id' => $game['Game']['id'],
            'downloads' => $game['Game']['downloads'] + 1
        ));

        $this->set(array(
            'id' => $replay[0],
            'name' => substr($replay[0], 0, -4),
            'download' => true,
            'extension' => substr($replay[0], -3),
            'path' => 'webroot' . DS . 'files' . DS . 'replays' . DS
        ));
    }


    public function edit($id = null)
    {
        $this->Game->id = $id;
        if (!$this->Game->exists()) {
            throw new NotFoundException(__('Invalid game'));
        }
        if ($this->request->is('post') || $this->request->is('put')) {
            if ($this->Game->save($this->request->data)) {
                $this->Session->setFlash(__('The game has been saved'));
                $this->redirect(array('action' => 'index'));
            } else {
                $this->Session->setFlash(__('The game could not be saved. Please, try again.'));
            }
        } else {
            $this->request->data = $this->Game->read(null, $id);
        }
        $groups = $this->Game->Group->find('list');
        $playoffs = $this->Game->Playoff->find('list');
        $this->set(compact('groups', 'playoffs'));
    }


    public function delete($id = null)
    {
        if (!$this->request->is('post')) {
            throw new MethodNotAllowedException();
        }
        $this->Game->id = $id;
        if (!$this->Game->exists()) {
            throw new NotFoundException(__('Invalid game'));
        }
        if ($this->Game->delete()) {
            $this->Session->setFlash(__('Game deleted'));
            $this->redirect(array('action' => 'index'));
        }
        $this->Session->setFlash(__('Game was not deleted'));
        $this->redirect(array('action' => 'index'));
    }


    public function admin_index()
    {
        $this->Game->recursive = 0;
        $this->set('games', $this->paginate());
    }


    public function admin_view($id = null)
    {
        $this->Game->id = $id;
        if (!$this->Game->exists()) {
            throw new NotFoundException(__('Invalid game'));
        }
        $this->set('game', $this->Game->read(null, $id));
    }

    public function admin_techwin()
    {
        if ($this->request->is('post')) {
            if (isset($this->request->data['getAways'])) {
                $allowedOpponents = $this->User->findAllowedOpponents($this->request->data['home_id']);
                $this->set('aways', $allowedOpponents);
                return;
            }

            $this->Game->create();
            if ($this->Game->reportTechwin($this->request->data['Game']['home_id'], $this->request->data['Game']['away_id'])) {
                $this->Session->setFlash('The technically decided game has been reported successfully.');
            } else {
                $this->Session->setFlash(
                    'Something went wrong. The game could not be submitted.',
                    'default', array('class' => 'error'));
            }
        }

        $this->loadModel('User');
        $allUsersStillInTournament = $this->User->getAllUsersStillInTournament();
        $this->set('homes', $allUsersStillInTournament);
    }

    public function admin_edit($id = null)
    {
        $this->Game->id = $id;
        if (!$this->Game->exists()) {
            throw new NotFoundException(__('Invalid game'));
        }
        if ($this->request->is('post') || $this->request->is('put')) {
            if ($this->Game->save($this->request->data)) {
                $this->Session->setFlash(__('The game has been saved'));
                $this->redirect(array('action' => 'index'));
            } else {
                $this->Session->setFlash(__('The game could not be saved. Please, try again.'));
            }
        } else {
            $this->request->data = $this->Game->read(null, $id);
        }
        $groups = $this->Game->Group->find('list');
        $playoffs = $this->Game->Playoff->find('list');
        $this->set(compact('groups', 'playoffs'));
    }


    public function admin_delete($id = null)
    {
        if (!$this->request->is('post')) {
            throw new MethodNotAllowedException();
        }
        $this->Game->id = $id;
        if (!$this->Game->exists()) {
            throw new NotFoundException(__('Invalid game'));
        }
        if ($this->Game->delete()) {
            $this->Session->setFlash(__('Game deleted'));
            $this->redirect(array('action' => 'index'));
        }
        $this->Session->setFlash(__('Game was not deleted'));
        $this->redirect(array('action' => 'index'));
    }
}
