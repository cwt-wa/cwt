<?php if(!$logged_in): ?>
	<div id="box" style="background-color:#29110D; text-align:center;">
		Log in, if you want to start your own Stream.
	</div>
<?php endif; ?>

<?php if($logged_in && !$up_stream['maintainer']): ?>
	<?php echo $this->Html->script('stream', array('inline' => false)); ?>
	<script type="text/javascript">
		$(document).ready(function() {
			$('#color1').css('border', '1px solid red');
		});
	</script>
	<div id="box" style="background-color:#3F2828; text-align:center;">
		<div id="willing" style="font-size:12pt; test-align:right; padding-left:400px; cursor:pointer; font-weight:bold; color:#3170CE;">
			Hey, willing to create your own live stream?
		</div>
		<div id="addStream" style="text-align:center; width:500px; padding-left:200px; display:none;">
			You can only stream from 
			<?php 
				echo $this->Html->link(
					'TwitchTV',
					'http://twitch.tv',
					array(
						'target' => '_blank'
					));
			?>.<br><br>
			<?php 
				echo $this->Form->create('Stream', array(
					'action' => 'add',
					'inputDefaults' => array(
						'div' => false,
						'style' => 'text-align:center;',
						'label' => false
					)
				));
			?>
				<fieldset>
					<legend><b>Start your own Live Stream!</b></legend>
					<br>
					<div id="box" style="border:none; box-shadow:none; padding:0px;">
						Stream Title<br>
						<?php echo $this->Form->input('title'); ?>
						<br><br>
						TwitchTV username<br>
						<?php echo $this->Form->input('provider'); ?>
						<br><br>
						Stream Color<br>
						<div style="background-color:white; width:156px; text-align:center; padding:2px; border:1px solid #7F9DB9; margin-left:158px;">
							<div id="color1" style="background-color:#2F2923; width:15px; height:15px; cursor:pointer; display:inline-block;">
								&nbsp;
							</div>
							<div id="color2" style="background-color:#887059; width:15px; height:15px; cursor:pointer; display:inline-block;">
								&nbsp;
							</div>
							<div id="color3" style="background-color:#405263; width:15px; height:15px; cursor:pointer; display:inline-block;">
								&nbsp;
							</div>
							<div id="color4" style="background-color:#3E283E; width:15px; height:15px; cursor:pointer; display:inline-block;">
								&nbsp;
							</div>
							<div id="color5" style="background-color:#283E28; width:15px; height:15px; cursor:pointer; display:inline-block;">
								&nbsp;
							</div>
						</div>
						<?php
							echo $this->Form->hidden('color', array(
								'id' => 'StreamColor',
								'value' => 'rgb(47, 41, 35)'
							)); 
						?>
					</div>
				</fieldset>
			<br>
			<?php echo $this->Form->end('Put your Stream online');?>
		</div>
	</div>
<?php endif; ?>

<div style="text-align:center;">
	<?php foreach($streams as $key => $val): ?>	
		<div id="box" style="background-color:#<?php echo $val['Stream']['color'] ?>; font-size:16pt; display:inline-block; margin:10px;">
			<?php if($val['Stream']['online']): ?>
				<b><font color="green">Online:</font></b>
			<?php else: ?>
				<font color="red">Offline:</font>
			<?php endif; ?>
			<?php
				echo $this->Html->link($val['Stream']['title'], 
					'/streams/view/' . $val['Stream']['id']
				);
			?>
		</div>
	<?php endforeach; ?>
</div>