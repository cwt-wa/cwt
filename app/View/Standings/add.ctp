<div class="standings form">
<?php echo $this->Form->create('Standing'); ?>
	<fieldset>
		<legend><?php echo __('Add Standing'); ?></legend>
	<?php
		echo $this->Form->input('group_id');
		echo $this->Form->input('points');
		echo $this->Form->input('games');
		echo $this->Form->input('game_ratio');
		echo $this->Form->input('round_ratio');
	?>
	</fieldset>
<?php echo $this->Form->end(__('Submit')); ?>
</div>
<div class="actions">
	<h3><?php echo __('Actions'); ?></h3>
	<ul>

		<li><?php echo $this->Html->link(__('List Standings'), array('action' => 'index')); ?></li>
		<li><?php echo $this->Html->link(__('List Groups'), array('controller' => 'groups', 'action' => 'index')); ?> </li>
		<li><?php echo $this->Html->link(__('New Group'), array('controller' => 'groups', 'action' => 'add')); ?> </li>
	</ul>
</div>
