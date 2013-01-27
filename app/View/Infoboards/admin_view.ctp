<div class="infoboards view">
<h2><?php  echo __('Infoboard');?></h2>
	<dl>
		<dt><?php echo __('Id'); ?></dt>
		<dd>
			<?php echo h($infoboard['Infoboard']['id']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Message'); ?></dt>
		<dd>
			<?php echo h($infoboard['Infoboard']['message']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('User'); ?></dt>
		<dd>
			<?php echo $this->Html->link($infoboard['User']['username'], array('controller' => 'users', 'action' => 'view', $infoboard['User']['id'])); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Created'); ?></dt>
		<dd>
			<?php echo h($infoboard['Infoboard']['created']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Category'); ?></dt>
		<dd>
			<?php echo h($infoboard['Infoboard']['category']); ?>
			&nbsp;
		</dd>
	</dl>
</div>
<div class="actions">
	<h3><?php echo __('Actions'); ?></h3>
	<ul>
		<li><?php echo $this->Html->link(__('Edit Infoboard'), array('action' => 'edit', $infoboard['Infoboard']['id'])); ?> </li>
		<li><?php echo $this->Form->postLink(__('Delete Infoboard'), array('action' => 'delete', $infoboard['Infoboard']['id']), null, __('Are you sure you want to delete # %s?', $infoboard['Infoboard']['id'])); ?> </li>
		<li><?php echo $this->Html->link(__('List Infoboards'), array('action' => 'index')); ?> </li>
		<li><?php echo $this->Html->link(__('New Infoboard'), array('action' => 'add')); ?> </li>
		<li><?php echo $this->Html->link(__('List Users'), array('controller' => 'users', 'action' => 'index')); ?> </li>
		<li><?php echo $this->Html->link(__('New User'), array('controller' => 'users', 'action' => 'add')); ?> </li>
	</ul>
</div>
