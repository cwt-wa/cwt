<?php
App::uses('AppController', 'Controller');

class RulesController extends AppController
{
    public $name = 'Rules';
    public $scaffold = 'admin';


    public function index()
    {
        $this->Rule->recursive = 0;
        $this->set('rules', $this->paginate());
    }


    public function view($apply = false)
    {
        $this->helpers[] = 'Bbcode';

        $this->loadModel('Tournament');
        $currentTournament = $this->Tournament->currentTournament();

        if ($apply && $this->Auth->loggedIn()
            && $this->Auth->user('stage') == 'retired'
            && $currentTournament['Tournament']['status'] == Tournament::PENDING
        ) {
            $this->set('apply', $apply);
        }

        $this->set('rule', $this->Rule->read(null, 1));
    }


    public function admin_edit()
    {
        $preview = $this->Rule->field('preview', 1);
        if ($this->request->is('requested')) {
            return $preview;
        } else {
            $this->set('preview', $preview);
            $this->set('destination', '/admin/rules/edit');
            $this->set('value', $this->Rule->field('text', 1));
        }

        if ($this->request->is('post')) {
            if ($this->request->data['action'] == 'preview') {
                $this->Rule->save(array(
                    'id' => 1,
                    'preview' => $this->request->data['text']
                ));

                $this->render('/Elements/bbcodepreview');
            } elseif ($this->request->data['action'] == 'submit') {
                $this->Rule->save(array(
                    'id' => 1,
                    'text' => $this->request->data['text'],
                    'user_id' => $this->Auth->user('id'),
                    'modified' => gmdate('Y-m-d H:i:s'),
                    'preview' => ''
                ));
            }
        }
    }
}
