<div class="ratings view">
<h2><?php  echo __('Rating');?></h2>
	<dl>
		<dt><?php echo __('Id'); ?></dt>
		<dd>
			<?php echo h($rating['Rating']['id']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Game'); ?></dt>
		<dd>
			<?php echo $this->Html->link($rating['Game']['id'], array('controller' => 'games', 'action' => 'view', $rating['Game']['id'])); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Rating'); ?></dt>
		<dd>
			<?php echo h($rating['Rating']['rating']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Ratings'); ?></dt>
		<dd>
			<?php echo h($rating['Rating']['ratings']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Sum'); ?></dt>
		<dd>
			<?php echo h($rating['Rating']['sum']); ?>
			&nbsp;
		</dd>
	</dl>
</div>
<div class="actions">
	<h3><?php echo __('Actions'); ?></h3>
	<ul>
		<li><?php echo $this->Html->link(__('Edit Rating'), array('action' => 'edit', $rating['Rating']['id'])); ?> </li>
		<li><?php echo $this->Form->postLink(__('Delete Rating'), array('action' => 'delete', $rating['Rating']['id']), null, __('Are you sure you want to delete # %s?', $rating['Rating']['id'])); ?> </li>
		<li><?php echo $this->Html->link(__('List Ratings'), array('action' => 'index')); ?> </li>
		<li><?php echo $this->Html->link(__('New Rating'), array('action' => 'add')); ?> </li>
		<li><?php echo $this->Html->link(__('List Games'), array('controller' => 'games', 'action' => 'index')); ?> </li>
		<li><?php echo $this->Html->link(__('New Game'), array('controller' => 'games', 'action' => 'add')); ?> </li>
	</ul>
</div>
