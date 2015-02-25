<?php
App::uses('AppController', 'Controller');

class PagesController extends AppController
{
    public $name = 'Pages';

    public function beforeFilter()
    {
        parent::beforeFilter();
        $this->Auth->allow('display');
    }

    /**
     * Displays a view
     *
     * @param mixed What page to display
     * @return void
     */
    public function display()
    {
        $path = func_get_args();

        $count = count($path);
        if (!$count) {
            $this->redirect('/');
        }
        $page = $subpage = $title_for_layout = null;

        if (!empty($path[0])) {
            $page = $path[0];
        }
        if (!empty($path[1])) {
            $subpage = $path[1];
        }
        if (!empty($path[$count - 1])) {
            switch ($path[0]) {
                case 'admin':
                    $title_for_layout = 'Admin Panel';
                    break;
                case 'logs':
                    $title_for_layout = 'Log Files';
                    break;
                case 'more':
                    $title_for_layout = 'More...';
                    break;
                default:
                    $title_for_layout = 'Crespo’s Worms Tournament';
            }
        }

        $this->set(compact('page', 'subpage', 'title_for_layout'));
        $this->render(implode('/', $path));
    }
}
