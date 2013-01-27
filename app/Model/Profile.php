<?php
App::uses('AppModel', 'Model');
App::uses('Folder', 'Utility');
App::uses('File', 'Utility');

class Profile extends AppModel {
	public $name = 'Profile';
	public $displayField = 'user_id'; 

	public $belongsTo = array(
		'User' => array(
			'className' => 'User',
			'foreignKey' => 'user_id',
			'dependent' => true
		)
	);

	// Getting all flags and create select field out of it.
	public function flags($current) {
		$dir = new Folder(WWW_ROOT . 'img/flags');

		$files = $dir->find('.*', true);

		if($current != 'unknown') $flag[$current] = $current;

		$flag[''] = '';

		foreach ($files as $file) {
		    $file = new File($dir->pwd() . DS . $file);

		    $flag[Inflector::humanize($file->name())] = Inflector::humanize($file->name());

		    $file->close(); // Be sure to close the file when you're done
		}

		return $flag;
	}

	public function deletePhoto() {
		$folder = new Folder('img/users');

		$user = AuthComponent::user('username');

		$photos = $folder->find("\b$user\b.*");

		if(!empty($photos)) {
			foreach($photos as $photo) {
				$file = new File('img/users/' . $photo);
				$file->delete();
			}
		}

		$this->save(array(
			'id' 	   => AuthComponent::user('id'),
			'user_id'  => AuthComponent::user('id'),
			'modified' => gmdate('Y-d-m H:i:s')
		));

		return true;
	}

	public function uploadPhoto($data) {
		$validtypes = array('png', 'gif', 'jpg', 'jpeg', 'pjpeg', 'bmp');
		$filetype = substr($data['photo']['type'], 6);

		if(in_array($filetype, $validtypes)) {
			$this->deletePhoto(); // Delete previous photo.

			$tmp_file = new File($data['photo']['tmp_name']);

			if($filetype == 'jpeg' || $filetype == 'pjpeg') {
				$filetype = 'jpg';
			}

			$filename = 'img/users/' . AuthComponent::user('username') . '.' . $filetype;
			$file = new File($filename, true);

			$file->write($tmp_file->read()); // Write new photo.

			if($file->executable() || !in_array($file->ext(), $validtypes)) {
				$file->delete();
				return false;
			}

			return true;
		}
		
		return false;
	}

	public function displayPhoto($user) {
		$folder = new Folder('img/users');

		$photo = $folder->find("\b$user\b.*");

		if(empty($photo)) {
			return false;
		}

		return $photo[0];
	}
}
