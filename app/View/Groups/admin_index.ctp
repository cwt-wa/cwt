<div class="groups index">
	<h2><?php echo __('Groups');?></h2>
	<table cellpadding="0" cellspacing="0">
	<tr>
			<th><?php echo $this->Paginator->sort('id');?></th>
			<th><?php echo $this->Paginator->sort('group');?></th>
			<th><?php echo $this->Paginator->sort('user_id');?></th>
			<th><?php echo $this->Paginator->sort('points');?></th>
			<th><?php echo $this->Paginator->sort('games');?></th>
			<th><?php echo $this->Paginator->sort('won');?></th>
			<th><?php echo $this->Paginator->sort('lost');?></th>
			<th><?php echo $this->Paginator->sort('rounds');?></th>
			<th class="actions"><?php echo __('Actions');?></th>
	</tr>
	<?php
	$i = 0;
	foreach ($groups as $group): ?>
	<tr>
		<td><?php echo h($group['Group']['id']); ?>&nbsp;</td>
		<td><?php echo h($group['Group']['group']); ?>&nbsp;</td>
		<td>
			<?php echo $this->Html->link($group['User']['id'], array('controller' => 'users', 'action' => 'view', $group['User']['id'])); ?>
		</td>
		<td><?php echo h($group['Group']['points']); ?>&nbsp;</td>
		<td><?php echo h($group['Group']['games']); ?>&nbsp;</td>
		<td><?php echo h($group['Group']['won']); ?>&nbsp;</td>
		<td><?php echo h($group['Group']['lost']); ?>&nbsp;</td>
		<td><?php echo h($group['Group']['rounds']); ?>&nbsp;</td>
		<td class="actions">
			<?php echo $this->Html->link(__('View'), array('action' => 'view', $group['Group']['id'])); ?>
			<?php echo $this->Html->link(__('Edit'), array('action' => 'edit', $group['Group']['id'])); ?>
			<?php echo $this->Form->postLink(__('Delete'), array('action' => 'delete', $group['Group']['id']), null, __('Are you sure you want to delete # %s?', $group['Group']['id'])); ?>
		</td>
	</tr>
<?php endforeach; ?>
	</table>
	<p>
	<?php
	echo $this->Paginator->counter(array(
	'format' => __('Page {:page} of {:pages}, showing {:current} records out of {:count} total, starting on record {:start}, ending on {:end}')
	));
	?>	</p>

	<div class="paging">
	<?php
		echo $this->Paginator->prev('< ' . __('previous'), array(), null, array('class' => 'prev disabled'));
		echo $this->Paginator->numbers(array('separator' => ''));
		echo $this->Paginator->next(__('next') . ' >', array(), null, array('class' => 'next disabled'));
	?>
	</div>
</div>
<div class="actions">
	<h3><?php echo __('Actions'); ?></h3>
	<ul>
		<li><?php echo $this->Html->link(__('New Group'), array('action' => 'add')); ?></li>
		<li><?php echo $this->Html->link(__('List Users'), array('controller' => 'users', 'action' => 'index')); ?> </li>
		<li><?php echo $this->Html->link(__('New User'), array('controller' => 'users', 'action' => 'add')); ?> </li>
	</ul>
</div>
