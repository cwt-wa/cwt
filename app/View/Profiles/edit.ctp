<div id="box" style="background-color:#3E283E">
<?php
	echo $this->Form->create('Profile', array(
		'inputDefaults' => array(
		    'label' => false,
		    'div' => false,
		    'style' => 'width:160px; text-align:center;'
		)
	));
?>
	<fieldset>
		<legend><b>General</b></legend>
		
		<table>
			<tr>
				<td style="padding:10px 180px 0px 40px">
					Username:
				</td>
				<td style="padding:10px 180px 0px 27px">
					Country:
				</td>
				<td style="padding:10px 180px 0px 30px">
					Clan:
				</td>
			</tr>
			<tr>
				<td style="padding-left:40px">
					<?php echo $this->Form->input('User.username'); ?>
				</td>
				<td style="padding-left:30px">
					<?php 
						echo $this->Form->select('country', $country, array(
							'empty' => false,
							'style' => 'width:167px; text-align:center; margin-left:-4px;'
						)); 
					?>
				</td>
				<td style="padding-left:30px">
					<?php echo $this->Form->input('clan'); ?>
				</td>
			</tr>
		</table>
	</fieldset>
	<br>
	<fieldset>
		<legend><b>Contact</b></legend>
	
		<table>
			<tr>
				<td style="padding:10px 180px 0px 40px">
					Email:
				</td>
				<td style="padding:10px 180px 0px 50px">
					MSN:
				</td>
				<td style="padding:10px 180px 0px 50px">
					ICQ:
				</td>
			</tr>
			<tr>
				<td style="padding-left:40px">
					<?php echo $this->Form->input('email'); ?>
				</td>
				<td style="padding-left:50px">
					<?php echo $this->Form->input('msn'); ?>
				</td>
				<td style="padding-left:50px">
					<?php echo $this->Form->input('icq'); ?>
				</td>
			</tr>
		</table>
	</fieldset>
	<br>
	<fieldset>
		<legend><b>Social</b></legend>
	
		<table>
			<tr>
				<td style="padding:10px 180px 0px 40px">
					Facebook:
				</td>
				<td style="padding:10px 180px 0px 30px">
					Google+:
				</td>
				<td style="padding:10px 180px 0px 30px">
					Twitter:
				</td>
			</tr>
			<tr>
				<td style="padding-left:40px">
					<?php echo $this->Form->input('facebook'); ?>
				</td>
				<td style="padding-left:30px">
					<?php echo $this->Form->input('googlep'); ?>
				</td>
				<td style="padding-left:30px">
					<?php echo $this->Form->input('twitter'); ?>
				</td>
			</tr>
		</table>
	</fieldset>
	<br>
	<fieldset>
		<legend><b>About</b></legend>
	<?php
		echo $this->Form->input('about', array('style'=>'width:100%; height:150px'));
	?>
	</fieldset>
	<br>
	<fieldset>
		<legend><b>Privacy</b></legend>
	<?php echo $this->Form->input('hideProfile', array('style'=>'margin:15px 5px 8px 40px')); ?>
		Hide my profile from non-logged-in users.<br>
	<?php echo $this->Form->input('hideEmail', array('style'=>'margin:5px 5px 0px 40px')); ?>
		Hide my Email address.
	</fieldset>
<?php
	echo $this->Form->submit('Update my Profile', array('style'=>'margin:10px 0px 0px 600px'));
	echo $this->Form->end();
?>
</div>