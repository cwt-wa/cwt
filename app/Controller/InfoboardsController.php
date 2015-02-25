<?php
App::uses('AppController', 'Controller');

class InfoboardsController extends AppController
{
    public $name = 'Infoboards';
    public $scaffold = 'admin';

    public function beforeFilter()
    {
        parent::beforeFilter();
        $this->Auth->allow('show', 'submit');
    }


    public function show($category = 1)
    {
        // Non logged in users may only see the Guestbook.
        if (!$this->Auth->loggedIn()) {
            $category = 4;
        }

        $this->set('category', $category);
        $this->set('messages', $this->Infoboard->messages($category));

        $this->helpers[] = 'Text';
        $this->helpers[] = 'Time';

        /*$messages = $this->Infoboard->messages($category);
        if($this->request->is('requested')) {
            return $messages;
        } else {
            $this->set('messages', $messages);
        }*/
    }


    public function nick_suggest()
    {
        $str = $this->request->data['str'];
        $count = $this->request->data['count'];
        $this->set('suggestions', $this->Infoboard->nick_suggestions($str, $count));
    }

    public function submit()
    {
        if (@$this->request->data['guest'] !== 'undefined') {
            // No guest book entries anymore.
            return;
        } else {
            $check = $this->Infoboard->submit($this->request->data['message']);
        }

        if (is_array($check)) {
            $this->set('unknowns', $check);
        }
    }
}
