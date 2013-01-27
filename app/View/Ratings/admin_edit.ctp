<div class="ratings form">
<?php echo $this->Form->create('Rating');?>
	<fieldset>
		<legend><?php echo __('Admin Edit Rating'); ?></legend>
	<?php
		echo $this->Form->input('id');
		echo $this->Form->input('game_id');
		echo $this->Form->input('rating');
		echo $this->Form->input('ratings');
		echo $this->Form->input('sum');
	?>
	</fieldset>
<?php echo $this->Form->end(__('Submit'));?>
</div>
<div class="actions">
	<h3><?php echo __('Actions'); ?></h3>
	<ul>

		<li><?php echo $this->Form->postLink(__('Delete'), array('action' => 'delete', $this->Form->value('Rating.id')), null, __('Are you sure you want to delete # %s?', $this->Form->value('Rating.id'))); ?></li>
		<li><?php echo $this->Html->link(__('List Ratings'), array('action' => 'index'));?></li>
		<li><?php echo $this->Html->link(__('List Games'), array('controller' => 'games', 'action' => 'index')); ?> </li>
		<li><?php echo $this->Html->link(__('New Game'), array('controller' => 'games', 'action' => 'add')); ?> </li>
	</ul>
</div>
