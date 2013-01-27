<div id="box" style="border:none; box-shadow:none; text-align:center; color:black; width:300px;">
	<?php 
		echo $this->Form->create('User', array(
			'inputDefaults' => array(
				'label' => false,
				'style' => 'text-align:center;',
				'error' => false
			)
		));
	?>
		<fieldset>
	 		<legend>Register</legend>
		
		Username:<br>
		<?php echo $this->Form->input('username'); ?><br><br>
		
		Password:<br>
		<?php echo $this->Form->input('password'); ?><br>
		Password again:<br>
		<?php echo $this->Form->input('password_confirmation', array('type'=>'password')); ?><br><br>
		<?php echo $captcha ?><br>
		<?php echo $this->Form->input('captcha'); ?>
		<?php echo $this->Form->hidden('result', array('value' => $result)); ?>
		</fieldset>
		<br>
	<?php echo $this->Form->end('Submit');?>
</div>