<div class="games form">
<?php echo $this->Form->create('Game');?>
	<fieldset>
		<legend><?php echo __('Admin Add Game'); ?></legend>
	<?php
		echo $this->Form->input('group_id');
		echo $this->Form->input('playoff_id');
		echo $this->Form->input('home_id');
		echo $this->Form->input('away_id');
		echo $this->Form->input('score_h');
		echo $this->Form->input('score_a');
		echo $this->Form->input('reporter_id');
	?>
	</fieldset>
<?php echo $this->Form->end(__('Submit'));?>
</div>
<div class="actions">
	<h3><?php echo __('Actions'); ?></h3>
	<ul>

		<li><?php echo $this->Html->link(__('List Games'), array('action' => 'index'));?></li>
		<li><?php echo $this->Html->link(__('List Playoffs'), array('controller' => 'playoffs', 'action' => 'index')); ?> </li>
		<li><?php echo $this->Html->link(__('New Step'), array('controller' => 'playoffs', 'action' => 'add')); ?> </li>
		<li><?php echo $this->Html->link(__('List Groups'), array('controller' => 'groups', 'action' => 'index')); ?> </li>
		<li><?php echo $this->Html->link(__('New Group'), array('controller' => 'groups', 'action' => 'add')); ?> </li>
		<li><?php echo $this->Html->link(__('List Users'), array('controller' => 'users', 'action' => 'index')); ?> </li>
		<li><?php echo $this->Html->link(__('New Home'), array('controller' => 'users', 'action' => 'add')); ?> </li>
	</ul>
</div>
