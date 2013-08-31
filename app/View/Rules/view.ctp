<?php
	echo $this->Html->script('rules', array('inline' => false));
?>

<div id="box" style="background-color:#3F3429">
	<h1 align="center">Rules</h1>
</div>

<?php if(isset($apply)): ?>
	<div id="box"v id="box" style="background-color:#29110D; text-align:center">
		<h2>Before you apply for Crespo’s Worms Tournament <?php echo $currentTournament['Tournament']['year'] ?>, make sure you are acquainted with the guidelines.</h2>
	</div>
	<div id="box" style="background-color:#29110D; text-align:center">
		<h3>The organizers of this tournament may remind you again, that this is only an application and we reserve the decision on who to accept for the tournament.</h3>
	</div>
<?php endif; ?>

<div id="box" style="background-color:#3F3429">
	<?php echo nl2br($this->Bbcode->parse($rule['Rule']['text'])); ?>
</div>

<?php if(isset($apply)): ?>
	<div id="box" style="background-color:#3F3429; text-align:right;">
		If you want to read the rules at a later time, you can find the link in the footer.
	</div>
	<div id="box" id="box" style="background-color:#29110D; text-align:center">
		<?php
			echo $this->Form->create('Apply', array('url' => '/applications/add'));
			echo $this->Form->checkbox('agree');
			echo '&nbsp;I\'m acquainted with the rules and ready to participate.<br><br>';
			echo $this->Form->submit('Apply', array('disabled' => 'disabled'));
			echo $this->Form->end();
		?>
	</div>
<?php endif; ?>
