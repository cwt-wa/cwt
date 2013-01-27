<div id="boxFloat" style="background-color:#29110D; width:445px; text-align:center;">
	<?php echo $this->Form->create('Playoff'); ?>
		<fieldset>
			<legend>First Branch:</legend>
			<?php
				for($i = 1; $i <= 8; $i++) {
					echo $this->Form->input('player' . $i, array(
						'options' => $players,
						'label' => false,
						'div' => false
					));
					echo ' vs. '; $i++;
					echo $this->Form->input('player' . $i, array(
						'options' => $players,
						'label' => false,
						'div' => false
					));
					echo '<br><br>'; if($i == 4) {echo '<br>';}
				}
			?>
		</fieldset>
</div>
<div id="box" style="background-color:#29110D; width:445px; margin-left:493px; text-align:center;">
		<fieldset>
			<legend>Second Branch:</legend>
			<?php
				for($i = 9; $i <= 16; $i++) {
					echo $this->Form->input('player' . $i, array(
						'options' => $players,
						'label' => false,
						'div' => false
					));
					echo ' vs. '; $i++;
					echo $this->Form->input('player' . $i, array(
						'options' => $players,
						'label' => false,
						'div' => false
					));
					echo '<br><br>'; if($i == 12) {echo '<br>';}
				}
			?>
		</fieldset>
</div>
<div id="box" style="background-color:#29110D; text-align:center;">
	<?php echo $this->Form->end('Create the Playoff Tree'); ?>
</div>