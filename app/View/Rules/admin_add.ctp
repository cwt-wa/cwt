<?php 
	echo $this->Html->script('bbcode', array('inline' => false));
	echo $this->Html->css('bbcode', null, array('inline' => false));
?>

<div id="box" style="background-color:#3F2828">
	<center><h1>Write a new set of Rules:</h1></center>
</div>

<?php
	echo $this->element('bbcode', array(
		'style' => 'width:958px; height:500px;',
		'destination' => '/admin/rules/add'
	));
?>