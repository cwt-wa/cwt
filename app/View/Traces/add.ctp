<div class="traces form">
<?php echo $this->Form->create('Trace');?>
	<fieldset>
		<legend><?php echo __('Add Trace'); ?></legend>
	<?php
		echo $this->Form->input('user_id');
		echo $this->Form->input('controller');
		echo $this->Form->input('action');
		echo $this->Form->input('additional');
	?>
	</fieldset>
<?php echo $this->Form->end(__('Submit'));?>
</div>
<div class="actions">
	<h3><?php echo __('Actions'); ?></h3>
	<ul>

		<li><?php echo $this->Html->link(__('List Traces'), array('action' => 'index'));?></li>
		<li><?php echo $this->Html->link(__('List Users'), array('controller' => 'users', 'action' => 'index')); ?> </li>
		<li><?php echo $this->Html->link(__('New User'), array('controller' => 'users', 'action' => 'add')); ?> </li>
	</ul>
</div>
