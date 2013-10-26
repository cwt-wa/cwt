<?php
App::uses('AppController', 'Controller');

class NewsController extends AppController {
	public $name = 'News';
	public $scaffold = 'admin';

	public function view() {
		$this->News->id = 1;
		$this->News->recursive = 0;
		$news = $this->News->read();

        if($this->request->is('requested')) {
            return $news;
        } else {
            $this->set('news', $news);
        }
	}

	public function admin_edit() {
		$this->News->id = 1;
		$preview = $this->News->field('preview');

        if ($this->request->is('requested')) {
            return $preview;
        } else {
            $this->set('preview', $preview);
            $this->set('destination', '/admin/news/edit');
        }

		if($this->request->is('post')) {
			if($this->request->data['action'] == 'preview') {
				$this->News->save(array(
					'preview' => $this->request->data['text']
				));

				$this->render('/Elements/bbcodepreview');
			} elseif($this->request->data['action'] == 'submit') {
				$this->News->save(array(
					'text' => $this->request->data['text'],
					'user_id' => $this->Auth->user('id'),
					'modified' => gmdate('Y-m-d H:i:s'),
					'preview' => ''
				));
			}
		}

		$this->set('news', $this->News->read());
	}
}
