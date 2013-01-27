<div class="ratings index">
	<h2><?php echo __('Ratings');?></h2>
	<table cellpadding="0" cellspacing="0">
	<tr>
			<th><?php echo $this->Paginator->sort('id');?></th>
			<th><?php echo $this->Paginator->sort('game_id');?></th>
			<th><?php echo $this->Paginator->sort('rating');?></th>
			<th><?php echo $this->Paginator->sort('ratings');?></th>
			<th><?php echo $this->Paginator->sort('sum');?></th>
			<th class="actions"><?php echo __('Actions');?></th>
	</tr>
	<?php
	foreach ($ratings as $rating): ?>
	<tr>
		<td><?php echo h($rating['Rating']['id']); ?>&nbsp;</td>
		<td>
			<?php echo $this->Html->link($rating['Game']['id'], array('controller' => 'games', 'action' => 'view', $rating['Game']['id'])); ?>
		</td>
		<td><?php echo h($rating['Rating']['rating']); ?>&nbsp;</td>
		<td><?php echo h($rating['Rating']['ratings']); ?>&nbsp;</td>
		<td><?php echo h($rating['Rating']['sum']); ?>&nbsp;</td>
		<td class="actions">
			<?php echo $this->Html->link(__('View'), array('action' => 'view', $rating['Rating']['id'])); ?>
			<?php echo $this->Html->link(__('Edit'), array('action' => 'edit', $rating['Rating']['id'])); ?>
			<?php echo $this->Form->postLink(__('Delete'), array('action' => 'delete', $rating['Rating']['id']), null, __('Are you sure you want to delete # %s?', $rating['Rating']['id'])); ?>
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
		<li><?php echo $this->Html->link(__('New Rating'), array('action' => 'add')); ?></li>
		<li><?php echo $this->Html->link(__('List Games'), array('controller' => 'games', 'action' => 'index')); ?> </li>
		<li><?php echo $this->Html->link(__('New Game'), array('controller' => 'games', 'action' => 'add')); ?> </li>
	</ul>
</div>
