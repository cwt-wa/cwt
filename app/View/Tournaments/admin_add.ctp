<?php $this->Html->script('tournaments_add', array('inline' => false)); ?>

<div id="box" style="background-color:#3F3429">
	<center><h1>If you start a tournament, you will be fully responsible for it!</h1></center>
</div>

<div id="box" style="background-color:#3F3429; text-align:center;">
	By submitting you agree on being the head moderator and sole host of the tournament.<br>
	This action should only be executed by the head moderator of CWT <?php echo gmdate('Y') ?>.<br>
	Don't forget to add the people who help you hosting the tournament below.
</div>

<div id="box" style="background-color:#3F3429">
	<?php echo $this->Form->create('Start'); ?>
		<span id="take" style="display:none;"><?php echo $this->Form->select('Helper0', $users); ?></span>

		<span id="bring"></span><br><br>
		<?php echo $this->Form->hidden('Number'); ?>
		<?php
			echo $this->Form->button('+', array(
				'id' => 'addhelper',
				'type' => 'button',
				'style' => 'width:20px'
			)); 
		?> Helper<br>
		<?php 
			echo $this->Form->button('-', array(
				'id' => 'removehelper', 
				'type' => 'button',
				'style' => 'width:20px'
			)); 
		?> Helper<br><br><br>
		<?php
			echo $this->Form->submit('Start a new Tournament', array(
				'onclick' => 'return confirm(\'Are you sure?\')'
			));
		?>
	<?php echo $this->Form->end(); ?>
</div>