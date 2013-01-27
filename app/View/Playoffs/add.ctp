<div class="playoffs form">
<?php echo $this->Form->create('Playoff');?>
	<fieldset>
		<legend><?php echo __('Add Playoff'); ?></legend>
	<?php
		echo $this->Form->input('opponent');
		echo $this->Form->input('score_h', array('label' => 'Your score'));
		echo $this->Form->input('score_a', array('label' => 'Opponent\'s score'));
		echo $this->Form->file('Game.replays');
	?>
	</fieldset>
<?php echo $this->Form->end(__('Submit'));?>
</div>
<div class="actions">
	<h3><?php echo __('Actions'); ?></h3>
	<ul>

		<li><?php echo $this->Html->link(__('List Playoffs'), array('action' => 'index'));?></li>
		<li><?php echo $this->Html->link(__('List Games'), array('controller' => 'games', 'action' => 'index')); ?> </li>
		<li><?php echo $this->Html->link(__('New Game'), array('controller' => 'games', 'action' => 'add')); ?> </li>
	</ul>
</div>
