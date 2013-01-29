<div id="box" style="border:none; box-shadow:none; text-align:center; color:black; width:300px;">
	<?php 
		echo $this->Form->create('User', array(
			'inputDefaults' => array(
				'label' => false,
				'style' => 'text-align:center;',
				'div' => false
			),
            'action' => '/login?referer=' . $referer
		));
	?>
		<fieldset>
	 		<legend>Login</legend>
		
		Username:<br>
		<?php echo $this->Form->input('username'); ?><br><br>
		
		Password:<br>
		<?php echo $this->Form->input('password'); ?><br>
		</fieldset>
		<br>
	<?php echo $this->Form->end('Log in');?>
</div>