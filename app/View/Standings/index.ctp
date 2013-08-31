<div class="standings index">
	<h2><?php echo __('Standings'); ?></h2>
	<table cellpadding="0" cellspacing="0">
	<tr>
			<th><?php echo $this->Paginator->sort('id'); ?></th>
			<th><?php echo $this->Paginator->sort('group_id'); ?></th>
			<th><?php echo $this->Paginator->sort('points'); ?></th>
			<th><?php echo $this->Paginator->sort('games'); ?></th>
			<th><?php echo $this->Paginator->sort('game_ratio'); ?></th>
			<th><?php echo $this->Paginator->sort('round_ratio'); ?></th>
			<th class="actions"><?php echo __('Actions'); ?></th>
	</tr>
	<?php
	foreach ($standings as $standing): ?>
	<tr>
		<td><?php echo h($standing['Standing']['id']); ?>&nbsp;</td>
		<td>
			<?php echo $this->Html->link($standing['Group']['group'], array('controller' => 'groups', 'action' => 'view', $standing['Group']['id'])); ?>
		</td>
		<td><?php echo h($standing['Standing']['points']); ?>&nbsp;</td>
		<td><?php echo h($standing['Standing']['games']); ?>&nbsp;</td>
		<td><?php echo h($standing['Standing']['game_ratio']); ?>&nbsp;</td>
		<td><?php echo h($standing['Standing']['round_ratio']); ?>&nbsp;</td>
		<td class="actions">
			<?php echo $this->Html->link(__('View'), array('action' => 'view', $standing['Standing']['id'])); ?>
			<?php echo $this->Html->link(__('Edit'), array('action' => 'edit', $standing['Standing']['id'])); ?>
			<?php echo $this->Form->postLink(__('Delete'), array('action' => 'delete', $standing['Standing']['id']), null, __('Are you sure you want to delete # %s?', $standing['Standing']['id'])); ?>
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
		<li><?php echo $this->Html->link(__('New Standing'), array('action' => 'add')); ?></li>
		<li><?php echo $this->Html->link(__('List Groups'), array('controller' => 'groups', 'action' => 'index')); ?> </li>
		<li><?php echo $this->Html->link(__('New Group'), array('controller' => 'groups', 'action' => 'add')); ?> </li>
	</ul>
</div>
