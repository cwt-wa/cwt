<div class="restores index">
	<h2><?php echo __('Restores'); ?></h2>
	<table cellpadding="0" cellspacing="0">
	<tr>
			<th><?php echo $this->Paginator->sort('id'); ?></th>
			<th><?php echo $this->Paginator->sort('user_id'); ?></th>
			<th><?php echo $this->Paginator->sort('tournament_id'); ?></th>
			<th><?php echo $this->Paginator->sort('home_id'); ?></th>
			<th><?php echo $this->Paginator->sort('away_id'); ?></th>
			<th><?php echo $this->Paginator->sort('score_h'); ?></th>
			<th><?php echo $this->Paginator->sort('score_a'); ?></th>
			<th><?php echo $this->Paginator->sort('stage'); ?></th>
			<th><?php echo $this->Paginator->sort('reported'); ?></th>
			<th><?php echo $this->Paginator->sort('created'); ?></th>
			<th class="actions"><?php echo __('Actions'); ?></th>
	</tr>
	<?php
	foreach ($restores as $restore): ?>
	<tr>
		<td><?php echo h($restore['Restore']['id']); ?>&nbsp;</td>
		<td><?php echo h($restore['Restore']['user_id']); ?>&nbsp;</td>
		<td>
			<?php echo $this->Html->link($restore['Tournament']['year'], array('controller' => 'tournaments', 'action' => 'view', $restore['Tournament']['id'])); ?>
		</td>
		<td>
			<?php echo $this->Html->link($restore['Home']['username'], array('controller' => 'users', 'action' => 'view', $restore['Home']['id'])); ?>
		</td>
		<td>
			<?php echo $this->Html->link($restore['Away']['username'], array('controller' => 'users', 'action' => 'view', $restore['Away']['id'])); ?>
		</td>
		<td><?php echo h($restore['Restore']['score_h']); ?>&nbsp;</td>
		<td><?php echo h($restore['Restore']['score_a']); ?>&nbsp;</td>
		<td><?php echo h($restore['Restore']['stage']); ?>&nbsp;</td>
		<td><?php echo h($restore['Restore']['reported']); ?>&nbsp;</td>
		<td><?php echo h($restore['Restore']['created']); ?>&nbsp;</td>
		<td class="actions">
			<?php echo $this->Html->link(__('View'), array('action' => 'view', $restore['Restore']['id'])); ?>
			<?php echo $this->Html->link(__('Edit'), array('action' => 'edit', $restore['Restore']['id'])); ?>
			<?php echo $this->Form->postLink(__('Delete'), array('action' => 'delete', $restore['Restore']['id']), null, __('Are you sure you want to delete # %s?', $restore['Restore']['id'])); ?>
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
		<li><?php echo $this->Html->link(__('New Restore'), array('action' => 'add')); ?></li>
		<li><?php echo $this->Html->link(__('List Tournaments'), array('controller' => 'tournaments', 'action' => 'index')); ?> </li>
		<li><?php echo $this->Html->link(__('New Tournament'), array('controller' => 'tournaments', 'action' => 'add')); ?> </li>
		<li><?php echo $this->Html->link(__('List Users'), array('controller' => 'users', 'action' => 'index')); ?> </li>
		<li><?php echo $this->Html->link(__('New Home'), array('controller' => 'users', 'action' => 'add')); ?> </li>
	</ul>
</div>
