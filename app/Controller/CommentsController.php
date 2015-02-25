<?php
App::uses('AppController', 'Controller');

class CommentsController extends AppController
{
    public $name = 'Comments';
    public $scaffold = 'admin';

    public function view($game_id)
    {
        $this->helpers[] = 'Time';
        $this->helpers[] = 'Bbcode';

        $this->Comment->recursive = 0;
        $comments = $this->Comment->find('all', array(
            'conditions' => array(
                'Comment.game_id' => $game_id
            ), 'order' => 'Comment.created DESC'
        ));
        if ($this->request->is('requested')) {
            return $comments;
        } else {
            $this->set('comments', $comments);
        }

        $this->set('comments', $comments);
    }


        public function add($game_id)
    {
        if (!$this->Auth->loggedIn()) {
            $this->Session->setFlash($this->Auth->authError, 'default', array('class' => 'error'));
            $this->redirect($this->referer());
        }

        if ($this->request->is('post')) {
            $this->Comment->create();

            $update = $this->Comment->find('first', array(
                'conditions' => array(
                    'Comment.game_id' => $game_id,
                    'Comment.user_id' => $this->Auth->user('id'),
                    'Comment.message' => ''
                )
            ));

            $this->Comment->id = (bool)$update ? $update['Comment']['id'] : false;

            if ($this->request->data['action'] == 'preview') {
                $preview = $this->Comment->field('preview', array(
                    'Comment.game_id' => $game_id,
                    'Comment.user_id' => $this->Auth->user('id'),
                    'Comment.message' => ''
                ));
                if ($this->request->is('requested')) {
                    return $preview;
                } else {
                    $this->set('preview', $preview);
                    $this->set('destination', '/comments/add/' . $game_id);
                }


                $this->Comment->save(array(
                    'game_id' => $game_id,
                    'user_id' => $this->Auth->user('id'),
                    'preview' => $this->request->data['text'],
                    'modified' => '0000-00-00 00:00:00'
                ));

                $this->render('/Elements/bbcodepreview');
            } elseif ($this->request->data['action'] == 'submit') {
                $save = array(
                    'game_id' => $game_id,
                    'message' => $this->request->data['text'],
                    'user_id' => $this->Auth->user('id'),
                    'preview' => '',
                    'modified' => '0000-00-00 00:00:00'
                );

                if ($this->Comment->save($save)) {
                    $this->loadModel('Infoboard');
                    $this->Infoboard->tournamentNews(
                        $game_id, 'commented');
                }
            }
        }

        $this->Comment->Game->recursive = 0;
        $comment = $this->Comment->Game->read(null, $game_id);

        if ($comment['Game']['group_id']) {
            $comment['stage'] = 'Group ' . $comment['Group']['label'];
        } else if ($comment['Game']['playoff_id']) {
            $comment['stage'] = $comment['Playoff']['stepAssoc'];
        }

        $this->set('title_for_layout', 'Comment Game #' . $game_id);
        $this->set('comment', $comment);
    }


    public function edit($id = null)
    {
        $this->Comment->id = $id;
        if (!$this->Comment->exists()) {
            throw new NotFoundException(__('Invalid comment'));
        }

        $this->Comment->recursive = 1;
        $comment = $this->Comment->find('first', array(
            'conditions' => array(
                'Comment.id' => $id
            )
        ));

        if ($comment['Comment']['user_id'] != $this->Auth->user('id')) {
            $this->Session->setFlash('You can’t edit someone else’s comment.', 'default', array('class' => 'error'));
            $this->redirect($this->referer());
        }

        if ($this->request->is('post')) {
            $this->Comment->create();
            $this->Comment->id = $id;

            if ($this->request->data['action'] == 'preview') {
                $preview = $this->Comment->field('preview');
                if ($this->request->is('requested')) {
                    return $preview;
                } else {
                    $this->set('preview', $preview);
                    $this->set('destination', '/comments/edit/' . $id);
                }

                $this->Comment->save(array(
                    'preview' => $this->request->data['text'],
                    'modified' => '0000-00-00 00:00:00'
                ));

                $this->render('/Elements/bbcodepreview');
            } elseif ($this->request->data['action'] == 'submit') {
                if ($this->request->data['text'] == ''
                    || $this->request->data['text'] == null
                ) {
                    $this->Comment->delete($id, false);
                } else {
                    $this->Comment->save(array(
                        'message' => $this->request->data['text'],
                        'preview' => ''
                    ));
                }
            }
        }

        $this->Comment->Game->recursive = 1;
        $game = $this->Comment->Game->read(null, $comment['Game']['id']);

        if ($game['Game']['group_id']) {
            $game['stage'] = 'Group ' . $game['Group']['label'];
        } elseif ($game['Game']['playoff_id']) { // Handle playoff game.
            $this->loadModel('Playoff');
            $game['stage'] = $this->Playoff->stepAssoc[$game['Playoff']['step']];
        }

        $this->set('comment', $comment);
        $this->set('game', $game);
    }
}
