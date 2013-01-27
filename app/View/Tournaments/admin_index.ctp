<div class="tournaments index">
	<h2><?php echo __('Tournaments');?></h2>
	<table cellpadding="0" cellspacing="0">
	<tr>
			<th><?php echo $this->Paginator->sort('id');?></th>
			<th><?php echo $this->Paginator->sort('year');?></th>
			<th><?php echo $this->Paginator->sort('status');?></th>
			<th><?php echo $this->Paginator->sort('gold_id');?></th>
			<th><?php echo $this->Paginator->sort('silver_id');?></th>
			<th><?php echo $this->Paginator->sort('bronze_id');?></th>
			<th><?php echo $this->Paginator->sort('host_id');?></th>
			<th><?php echo $this->Paginator->sort('helpers_id');?></th>
			<th class="actions"><?php echo __('Actions');?></th>
	</tr>
	<?php
	foreach ($tournaments as $tournament): ?>
	<tr>
		<td><?php echo h($tournament['Tournament']['id']); ?>&nbsp;</td>
		<td><?php echo h($tournament['Tournament']['year']); ?>&nbsp;</td>
		<td><?php echo h($tournament['Tournament']['status']); ?>&nbsp;</td>
		<td>
			<?php echo $this->Html->link($tournament['Gold']['username'], array('controller' => 'users', 'action' => 'view', $tournament['Gold']['id'])); ?>
		</td>
		<td>
			<?php echo $this->Html->link($tournament['Silver']['username'], array('controller' => 'users', 'action' => 'view', $tournament['Silver']['id'])); ?>
		</td>
		<td>
			<?php echo $this->Html->link($tournament['Bronze']['username'], array('controller' => 'users', 'action' => 'view', $tournament['Bronze']['id'])); ?>
		</td>
		<td>
			<?php echo $this->Html->link($tournament['Host']['username'], array('controller' => 'users', 'action' => 'view', $tournament['Host']['id'])); ?>
		</td>
		<td><?php echo h($tournament['Tournament']['helpers_id']); ?>&nbsp;</td>
		<td class="actions">
			<?php echo $this->Html->link(__('View'), array('action' => 'view', $tournament['Tournament']['id'])); ?>
			<?php echo $this->Html->link(__('Edit'), array('action' => 'edit', $tournament['Tournament']['id'])); ?>
			<?php echo $this->Form->postLink(__('Delete'), array('action' => 'delete', $tournament['Tournament']['id']), null, __('Are you sure you want to delete # %s?', $tournament['Tournament']['id'])); ?>
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
		<li><?php echo $this->Html->link(__('New Tournament'), array('action' => 'add')); ?></li>
		<li><?php echo $this->Html->link(__('List Users'), array('controller' => 'users', 'action' => 'index')); ?> </li>
		<li><?php echo $this->Html->link(__('New Host'), array('controller' => 'users', 'action' => 'add')); ?> </li>
	</ul>
</div>
