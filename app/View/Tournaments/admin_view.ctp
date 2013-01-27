<div class="tournaments view">
<h2><?php  echo __('Tournament');?></h2>
	<dl>
		<dt><?php echo __('Id'); ?></dt>
		<dd>
			<?php echo h($tournament['Tournament']['id']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Year'); ?></dt>
		<dd>
			<?php echo h($tournament['Tournament']['year']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Status'); ?></dt>
		<dd>
			<?php echo h($tournament['Tournament']['status']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Gold'); ?></dt>
		<dd>
			<?php echo $this->Html->link($tournament['Gold']['username'], array('controller' => 'users', 'action' => 'view', $tournament['Gold']['id'])); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Silver'); ?></dt>
		<dd>
			<?php echo $this->Html->link($tournament['Silver']['username'], array('controller' => 'users', 'action' => 'view', $tournament['Silver']['id'])); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Bronze'); ?></dt>
		<dd>
			<?php echo $this->Html->link($tournament['Bronze']['username'], array('controller' => 'users', 'action' => 'view', $tournament['Bronze']['id'])); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Host'); ?></dt>
		<dd>
			<?php echo $this->Html->link($tournament['Host']['username'], array('controller' => 'users', 'action' => 'view', $tournament['Host']['id'])); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Helpers Id'); ?></dt>
		<dd>
			<?php echo h($tournament['Tournament']['helpers_id']); ?>
			&nbsp;
		</dd>
	</dl>
</div>
<div class="actions">
	<h3><?php echo __('Actions'); ?></h3>
	<ul>
		<li><?php echo $this->Html->link(__('Edit Tournament'), array('action' => 'edit', $tournament['Tournament']['id'])); ?> </li>
		<li><?php echo $this->Form->postLink(__('Delete Tournament'), array('action' => 'delete', $tournament['Tournament']['id']), null, __('Are you sure you want to delete # %s?', $tournament['Tournament']['id'])); ?> </li>
		<li><?php echo $this->Html->link(__('List Tournaments'), array('action' => 'index')); ?> </li>
		<li><?php echo $this->Html->link(__('New Tournament'), array('action' => 'add')); ?> </li>
		<li><?php echo $this->Html->link(__('List Users'), array('controller' => 'users', 'action' => 'index')); ?> </li>
		<li><?php echo $this->Html->link(__('New Host'), array('controller' => 'users', 'action' => 'add')); ?> </li>
	</ul>
</div>
