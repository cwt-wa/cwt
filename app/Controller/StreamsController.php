<?php
App::uses('AppController', 'Controller');

/**
 * @property Stream Stream
 */
class StreamsController extends AppController
{
    public $name = 'Streams';
    public $scaffold = 'admin';


    public function beforeFilter()
    {
        parent::beforeFilter();
        $this->Auth->allow('delete', 'videos');
    }


    public function index()
    {
        $this->set('streams', $this->paginate());
    }


    public function videos()
    {
        if ($this->request->is('get')) { // @TODO Should be ajax.
            $videos = $this->Stream->queryAllVideos();
            $this->set('videos', $videos);
            $this->layout = 'ajax';
        }
    }


    public function view($twitchVideoId)
    {
        $res = $this->Stream->callTwitchApi('videos/' . $twitchVideoId);

        if (!$res) {
            $this->Session->setFlash('Couldn’t get this stream.', 'default', array('class' => 'error'));
            $this->redirect($this->referer());
            return;
        }
//        if (isset($_GET['streamId'])) {
//            $stream = $this->Stream->findById($_GET['streamId']);
//            $color = $stream['Stream']['color'];
//        } else {
//            $color = '#3F2828';
//        }
        $explodeOwnerUrl = explode('/', $res['_links']['channel']);
        $provider = $explodeOwnerUrl[count($explodeOwnerUrl) - 1];
        $stream = $this->Stream->find('first', array(
            'conditions' => array(
                'LOWER(provider)' => strtolower($provider)
            )
        ));
        $this->set('provider', $provider);
        $this->set('stream', $stream);
        $this->set('video', $res);
    }


    public function add()
    {
        if ($this->request->is('post')) {
            $this->request->data['Stream']['color'] =
                $this->Stream->rgb2hex(
                    $this->request->data['Stream']['color']);

            $this->request->data['Stream']['maintainer_id'] =
                $this->Auth->user('id');

            if ($this->Stream->save($this->request->data)) {
                $this->Session->setFlash(
                    'Congratulations, you have successfully
                     set your stream up.');

                $this->redirect('/streams/view/' . $this->Stream->id);
            } else {
                $this->Session->setFlash(
                    'That stream is nothing you want to put online this way.',
                    'default', array('class' => 'error'));
            }
        }

        $this->set('users', $this->Stream->User->find('list'));
    }


    public function edit($id, $description = false)
    {
        $this->Stream->id = $id;
        if (!$this->Stream->exists()) {
            throw new NotFoundException(__('Invalid stream'));
        }

        $stream = $this->Stream->read(); // Current stream.

        // Is it the logged in user's stream?
        if ($stream['Stream']['maintainer_id'] != $this->Auth->user('id')) {
            $this->Session->setFlash('You can only edit your own stream.', 'default', array('class' => 'error'));
            $this->redirect('/streams');
        }

        if ($description) { // Editing the description.
            $preview = $stream['Stream']['preview'];
            if ($this->request->is('requested')) {
                return $preview;
            } else {
                $this->set('preview', $preview);
                $this->set('destination', '/streams/edit/' . $id . '/description');
            }

            if ($this->request->is('post')) {
                if ($this->request->data['action'] == 'preview') {
                    $this->Stream->save(array(
                        'id' => $id,
                        'preview' => $this->request->data['text']
                    ));

                    $this->render('/Elements/bbcodepreview');
                } elseif ($this->request->data['action'] == 'submit') {
                    $this->Stream->save(array(
                        'id' => $id,
                        'description' => $this->request->data['text'],
                        'maintainer_id' => $this->Auth->user('id'),
                        'modified' => gmdate('Y-m-d H:i:s'),
                        'preview' => ''
                    ));
                }
            } else {
                $this->request->data = $stream;
            }
        } else { // Editing everything but the description.
            if ($this->request->is('post') || $this->request->is('put')) {
                $this->request->data['Stream']['color'] =
                    $this->Stream->rgb2hex(
                        $this->request->data['Stream']['color']);

                if ($this->Stream->save($this->request->data)) {
                    $this->Session->setFlash('Your stream has been updated.');
                    $this->redirect('/streams/view/' . $id);
                } else {
                    $this->Session->setFlash(
                        'Sorry, something has gone wrong.',
                        'default', array('class' => 'error'));
                }
            } else {
                $this->request->data = $stream;
            }
        }

        $this->set('stream', $stream);
        $this->set('description', $description);
    }


    public function delete($id = null)
    {
        $this->Stream->id = $id;

        if (!$this->request->is('post')) {
            throw new MethodNotAllowedException();
        }
        if (!$this->Stream->exists()) {
            throw new NotFoundException(__('Invalid stream'));
        }

        if ($this->Stream->field('maintainer_id') != $this->Auth->user('id')) {
            $this->Session->setFlash('Log in or delete your own stream.', 'default', array('class' => 'error'));
            $this->redirect('/streams');
        } else {
            if ($this->Stream->delete()) {
                $this->Session->setFlash('Your stream has been deleted.');
                $this->redirect('/streams');
            } else {
                $this->Session->setFlash('Something went wrong while deleting your stream.', 'default', array('class' => 'error'));
                $this->redirect('/streams/view/' . $id);
            }
        }
    }


    public function schedule($id)
    {
        $this->loadModel('Schedule');

        if ($this->request->is('post')) {
            if ($this->request->data['StreamAdd']) {
                foreach ($this->request->data['StreamAdd'] as $scheduleIt) {
                    $this->Schedule->create();
                    $this->Schedule->id = $scheduleIt;
                    $alreadyStreaming = $this->Schedule->field('stream_id');

                    if ($alreadyStreaming) {
                        $newStreamers = $alreadyStreaming . ',' . $id;
                    } else {
                        $newStreamers = $id;
                    }

                    $this->Schedule->save(array(
                        'stream_id' => $newStreamers
                    ));
                }
            } elseif ($this->request->data['StreamDelete']) {
                foreach ($this->request->data['StreamDelete'] as $undoSchedule) {
                    $this->Schedule->create();
                    $this->Schedule->id = $undoSchedule;
                    $alreadyStreaming = $this->Schedule->field('stream_id');
                    $alreadyStreaming = explode(',', $alreadyStreaming);

                    if (count($alreadyStreaming) == 1) {
                        $newStreamers = '';
                    } else {
                        foreach ($alreadyStreaming as $key => $val) {
                            if ($val == $id) {
                                unset($alreadyStreaming[$key]);
                            }
                        }

                        $newStreamers = implode(',', $alreadyStreaming);
                    }

                    $this->Schedule->save(array(
                        'id' => $undoSchedule,
                        'stream_id' => $newStreamers
                    ));
                }
            }

        }

        $schedules = $this->Schedule->find('all', array(
            'order' => 'Schedule.when ASC',
            'recursive' => 1
        ));
        $stream = $this->Stream->read(null, $id);
        $schedules = $this->Schedule->scheduledStreams($schedules);

        foreach ($schedules as $key => $val) {
            if (in_array($id, explode(',', $val['Schedule']['stream_id']))) {
                $scheduleds[] = $schedules[$key];
                unset($schedules[$key]);
            }
        }

        $this->set('stream', $stream);
        $this->set('schedules', $schedules);
        $this->set('scheduleds', $scheduleds);
    }
}
