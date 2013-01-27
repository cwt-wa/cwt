<?php
	$preview = $this->requestAction($destination);
	$this->Helpers->load('Bbcode');

	echo nl2br($this->Bbcode->parse($preview));
?>