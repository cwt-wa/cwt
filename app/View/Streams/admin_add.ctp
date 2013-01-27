<div class="streams form">
<?php echo $this->Form->create('Stream');?>
	<fieldset>
		<legend><?php echo __('Admin Add Stream'); ?></legend>
	<?php
		echo $this->Form->input('title');
		echo $this->Form->input('description');
		echo $this->Form->input('embedcode');
		echo $this->Form->input('curr_views');
		echo $this->Form->input('alltime_views');
		echo $this->Form->input('color');
		echo $this->Form->input('user_id');
		echo $this->Form->input('online');
	?>
	</fieldset>
<?php echo $this->Form->end(__('Submit'));?>
</div>
<div class="actions">
	<h3><?php echo __('Actions'); ?></h3>
	<ul>

		<li><?php echo $this->Html->link(__('List Streams'), array('action' => 'index'));?></li>
		<li><?php echo $this->Html->link(__('List Users'), array('controller' => 'users', 'action' => 'index')); ?> </li>
		<li><?php echo $this->Html->link(__('New User'), array('controller' => 'users', 'action' => 'add')); ?> </li>
	</ul>
</div>
