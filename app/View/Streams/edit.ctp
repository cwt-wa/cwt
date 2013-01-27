<?php if($description): ?>
	<?php 
		echo $this->Html->script('bbcode', array('inline' => false));
		echo $this->Html->css('bbcode', null, array('inline' => false));
	?>

	<div id="box" style="background-color:#3F2828">
		<center><h1>Edit your stream's description:</h1></center>
	</div>

	<?php
		echo $this->element('bbcode', array(
			'style' => 'width:958px; height:500px;',
			'destination' => '/streams/edit/' . $stream['Stream']['id'] . '/description',
			'redirect' => '/streams/view/' . $stream['Stream']['id'],
			'value' => $stream['Stream']['description']
		));
	?>
<?php else: ?>
	<?php echo $this->Html->script('stream', array('inline' => false)); ?>
	<script type="text/javascript">
		$(document).ready(function() {
			var r = hex2r('<?php echo $stream['Stream']['color'] ?>');
			var g = hex2g('<?php echo $stream['Stream']['color'] ?>');
			var b = hex2b('<?php echo $stream['Stream']['color'] ?>');

			function hex2r(h) {return parseInt((cutHex(h)).substring(0,2),16)}
			function hex2g(h) {return parseInt((cutHex(h)).substring(2,4),16)}
			function hex2b(h) {return parseInt((cutHex(h)).substring(4,6),16)}
			function cutHex(h) {return (h.charAt(0)=="#") ? h.substring(1,7):h}

			rgb = 'rgb(' + r + ', ' + g + ', ' + b + ')';

			switch(rgb) {
				case $('#color1').css('background-color'):				
					$('#color1').css('border', '1px solid red');
				break;
				case $('#color2').css('background-color'):				
					$('#color2').css('border', '1px solid red');
				break;
				case $('#color3').css('background-color'):				
					$('#color3').css('border', '1px solid red');
				break;
				case $('#color4').css('background-color'):				
					$('#color4').css('border', '1px solid red');
				break;
				case $('#color5').css('background-color'):				
					$('#color5').css('border', '1px solid red');
			}

			$('#StreamColor').val(rgb);
		});
	</script>

	<div id="box" style="background-color:#<?php echo $stream['Stream']['color'] ?>; text-align:center;">
		<div id="addStream" style="text-align:center; width:500px; padding-left:200px;">
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
					'inputDefaults' => array(
						'div' => false,
						'style' => '',
						'style' => 'text-align:center;',
						'label' => false
					)
				));
			?>
				<fieldset>
					<legend><b>Edit your stream: “<?php echo $stream['Stream']['title'] ?>”</b></legend>
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
						<?php echo $this->Form->hidden('color', array('id' => 'StreamColor')); ?>
					</div>
				</fieldset>
			<br>
			<?php echo $this->Form->end('Edit “' . $stream['Stream']['title'] . '”'); ?>
		</div>
	</div>
<?php endif; ?>
