<?php

App::uses('AppController', 'Controller');

class TournamentsController extends AppController
{

    public $name = 'Tournaments';
    public $scaffold = 'admin';

    public function beforeFilter()
    {
        parent::beforeFilter();
        $this->Auth->allow('download');
    }

    public function index()
    {
        $this->helpers[] = 'Text';
        $this->Tournament->recursive = 1;
        $tournaments = $this->Tournament->find('all', array(
            'conditions' => array(
                'status' => Tournament::ARCHIVED
            ),
            'order' => 'year DESC'
        ));

        $this->set('tournaments', $tournaments);
    }

    public function view()
    {
        $path = func_get_args();
        $tournamentYear = $path[0];

        $tournament = $this->Tournament->find('first', array(
            'conditions' => array(
                'Tournament.year' => $tournamentYear
            )
        ));

        if (empty($tournament)) {
            throw new NotFoundException('Tournament of the year ' . $tournamentYear . ' was not found in the archive.');
        }

        $this->loadModel('Group');
        $this->set('group', $this->Group->findForGroupsPage($tournament['Tournament']['id']));
        $this->loadModel('Playoff');
        $this->set('playoff', $this->Playoff->findForPlayoffsPage($tournament['Tournament']['id']));
        $this->set('title_for_layout', $tournamentYear);
        $this->set('tournamentYear', $tournamentYear);
    }

    public function admin_index()
    {
        $this->Tournament->recursive = 0;
        $this->set('tournaments', $this->paginate());
    }

    public function admin_view($id = null)
    {
        $this->Tournament->id = $id;
        if (!$this->Tournament->exists()) {
            throw new NotFoundException(__('Invalid tournament'));
        }
        $this->set('tournament', $this->Tournament->read(null, $id));
    }

    public function admin_add()
    {
        $currentTournament = $this->Tournament->currentTournament();
        $this->Tournament->id = $currentTournament['id'];

        if ($currentTournament['Tournament']['status'] != null) {
            $this->Session->setFlash('There is already a tournament running at the moment.');
            $this->redirect($this->referer());
        }

        if ($this->request->is('post')) {
            if ($this->Tournament->start($this->request->data['Start'])) {
                $this->Auth->login($this->User->re_login());
                $this->Session->setFlash(
                    'The tournament has started and users are now able
                to apply for participation.');
                $this->redirect('/pages/admin');
            } else {
                $this->Session->setFlash(
                    'A new tournament has not been started.
                You have entered the same helper twice.', 'default', array('class' => 'error'));
            }
        }

        $this->Tournament->bindModel(array(
            'hasMany' => array(
                'User' => array(
                    'className' => 'User'
                )
            )
        ));

        $users = $this->Tournament->User->find('list');
        unset($users[$this->Auth->user('id')]);
        uasort($users, 'strcasecmp');
        $this->set('users', $users);
    }

    public function admin_upload()
    {
        $this->helpers[] = 'AjaxMultiUpload.Upload';
        $this->components[] = 'AjaxMultiUpload.Upload';

        $tourney = $this->Tournament->currentTournament();
        $tourney = $tourney['Tournament'];
        $this->Tournament->id = $tourney['id'];
        $this->set('tournament', $this->Tournament->read());
    }

    public function download($download)
    {
        $this->viewClass = 'Media';

        CakeLog::write('downloads', $this->Tournament->prependUserInfo('downloaded ' . $download, $this->Auth->loggedIn()));

        switch ($download) {
            case 'replays':
                $this->set(array(
                    'id' => 'replays_2007_till_2011.zip',
                    'name' => 'replays_2007_till_2011',
                    'download' => true,
                    'extension' => 'rar',
                    'path' => 'files' . DS . 'downloads' . DS
                ));
                break;
            case 'scheme':
                $this->set(array(
                    'id' => 'CWT Intermediate.wsc',
                    'name' => 'CWT Intermediate',
                    'download' => true,
                    'extension' => 'wsc',
                    'path' => 'files' . DS . 'downloads' . DS
                ));
                break;
            case 'cwt2009':
                $this->set(array(
                    'id' => 'cwt2009.zip',
                    'name' => 'cwt2009',
                    'download' => true,
                    'extension' => 'zip',
                    'path' => 'files' . DS . 'downloads' . DS
                ));
                break;
        }
    }

    /**
     * Action to edit the review of a tournament which can only be done when the tournament has been archived.
     *
     * @param $tournamentId int The tournament whose review text should be edited.
     * @throws NotFoundException When a tournament with the given id wasn't found.
     */
    public function admin_review($tournamentId)
    {
        $this->Tournament->id = $tournamentId;
        if (!$this->Tournament->exists()) {
            throw new NotFoundException(__('Invalid tournament'));
        }

        if ($this->request->is('post')) {
            if ($this->Tournament->save($this->request->data, true, array('review'))) {
                $this->Session->setFlash('Thanks for the review, it has been updated successfully.');
            } else {
                $this->Session->setFlash(
                    'Eww, I couldn\'t update the review.',
                    'default', array('class' => 'error'));
            }
        }

        $tournament = $this->Tournament->read();
        $this->set('tournament', $tournament);
    }

    public function admin_edit()
    {
        // It's always the msot recent tournament.
        $currentTournament = $this->Tournament->currentTournament();
        $this->Tournament->id = $currentTournament['Tournament']['id'];

        /* Redirecting is a bit of a problem here.
         * Depending on the status we have different
         * error/success messages and redirecting paths.
         * (Do that step by step.)
         */

        switch ($currentTournament['Tournament']['status']) { // Next status.
            case Tournament::PENDING:
                $next = 'Enter the Group Stage';
                $Smsg = 'Players who were assigned to groups can now report their group stage games';
                $Emsg = 'You have got to create the groups first.';
                $redirect = '/groups';

                $this->loadModel('Group');

                $groupsCreated = (bool)$this->Group->find('count', array(
                    'conditions' => array(
                        'Group.tournament_id' => $currentTournament['Tournament']['id']
                    )
                ));

                if (!$groupsCreated) {
                    $this->Session->setFlash(
                        'You need to assign players to their groups
                     before starting the group stage.', 'default', array('class' => 'error'));
                    $this->redirect('/admin/groups/add');
                }
                break;
            case Tournament::GROUP:
                $next = 'Start the Playoff';
                $Smsg = 'Players who made it into the playoff can now report their playoff games.';
                $redirect = '/playoffs';

                $this->loadModel('Playoff');

                if (!$this->Playoff->isPaired($currentTournament['Tournament']['id'])) {
                    $this->redirect('/admin/playoffs/add');
                }
                break;
            case Tournament::PLAYOFF:
                $next = 'Archive the tournament';
                $Smsg = 'The current tournament has now been made available in the Archive.';
                $redirect = '/archive';
        }

        if ($this->request->is('post')) {
            if ($this->Tournament->next()) {
                $this->Auth->login($this->User->re_login());
                $this->Session->setFlash($Smsg);
                $this->redirect($redirect);
            } else {
                $this->Session->setFlash(
                    $Emsg, 'default', array('class' => 'error')); //yes
            }
        }

        $this->set('next', $next);
        $this->set('status', $currentTournament['Tournament']['status']);
    }

    public function admin_delete($id = null)
    {
        if (!$this->request->is('post')) {
            throw new MethodNotAllowedException();
        }
        $this->Tournament->id = $id;
        if (!$this->Tournament->exists()) {
            throw new NotFoundException(__('Invalid tournament'));
        }
        if ($this->Tournament->delete()) {
            $this->Session->setFlash(__('Tournament deleted'));
            $this->redirect(array('action' => 'index'));
        }
        $this->Session->setFlash(__('Tournament was not deleted'));
        $this->redirect(array('action' => 'index'));
    }

}
