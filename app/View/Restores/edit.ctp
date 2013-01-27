<div class="restores form">
<?php echo $this->Form->create('Restore'); ?>
	<fieldset>
		<legend><?php echo __('Edit Restore'); ?></legend>
	<?php
		echo $this->Form->input('id');
		echo $this->Form->input('user_id');
		echo $this->Form->input('tournament_id');
		echo $this->Form->input('home_id');
		echo $this->Form->input('away_id');
		echo $this->Form->input('score_h');
		echo $this->Form->input('score_a');
		echo $this->Form->input('stage');
		echo $this->Form->input('reported');
	?>
	</fieldset>
<?php echo $this->Form->end(__('Submit')); ?>
</div>
<div class="actions">
	<h3><?php echo __('Actions'); ?></h3>
	<ul>

		<li><?php echo $this->Form->postLink(__('Delete'), array('action' => 'delete', $this->Form->value('Restore.id')), null, __('Are you sure you want to delete # %s?', $this->Form->value('Restore.id'))); ?></li>
		<li><?php echo $this->Html->link(__('List Restores'), array('action' => 'index')); ?></li>
		<li><?php echo $this->Html->link(__('List Tournaments'), array('controller' => 'tournaments', 'action' => 'index')); ?> </li>
		<li><?php echo $this->Html->link(__('New Tournament'), array('controller' => 'tournaments', 'action' => 'add')); ?> </li>
		<li><?php echo $this->Html->link(__('List Users'), array('controller' => 'users', 'action' => 'index')); ?> </li>
		<li><?php echo $this->Html->link(__('New Home'), array('controller' => 'users', 'action' => 'add')); ?> </li>
	</ul>
</div>
