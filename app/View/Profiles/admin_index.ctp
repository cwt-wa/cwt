<div class="profiles index">
	<h2><?php echo __('Profiles');?></h2>
	<table cellpadding="0" cellspacing="0">
	<tr>
			<th><?php echo $this->Paginator->sort('id');?></th>
			<th><?php echo $this->Paginator->sort('user_id');?></th>
			<th><?php echo $this->Paginator->sort('modified');?></th>
			<th><?php echo $this->Paginator->sort('country');?></th>
			<th><?php echo $this->Paginator->sort('clan');?></th>
			<th><?php echo $this->Paginator->sort('email');?></th>
			<th><?php echo $this->Paginator->sort('msn');?></th>
			<th><?php echo $this->Paginator->sort('icq');?></th>
			<th><?php echo $this->Paginator->sort('facebook');?></th>
			<th><?php echo $this->Paginator->sort('twitter');?></th>
			<th><?php echo $this->Paginator->sort('about');?></th>
			<th><?php echo $this->Paginator->sort('hideProfile');?></th>
			<th><?php echo $this->Paginator->sort('hideEmail');?></th>
			<th class="actions"><?php echo __('Actions');?></th>
	</tr>
	<?php
	foreach ($profiles as $profile): ?>
	<tr>
		<td><?php echo h($profile['Profile']['id']); ?>&nbsp;</td>
		<td>
			<?php echo $this->Html->link($profile['User']['username'], array('controller' => 'users', 'action' => 'view', $profile['User']['id'])); ?>
		</td>
		<td><?php echo h($profile['Profile']['modified']); ?>&nbsp;</td>
		<td><?php echo h($profile['Profile']['country']); ?>&nbsp;</td>
		<td><?php echo h($profile['Profile']['clan']); ?>&nbsp;</td>
		<td><?php echo h($profile['Profile']['email']); ?>&nbsp;</td>
		<td><?php echo h($profile['Profile']['msn']); ?>&nbsp;</td>
		<td><?php echo h($profile['Profile']['icq']); ?>&nbsp;</td>
		<td><?php echo h($profile['Profile']['facebook']); ?>&nbsp;</td>
		<td><?php echo h($profile['Profile']['twitter']); ?>&nbsp;</td>
		<td><?php echo h($profile['Profile']['about']); ?>&nbsp;</td>
		<td><?php echo h($profile['Profile']['hideProfile']); ?>&nbsp;</td>
		<td><?php echo h($profile['Profile']['hideEmail']); ?>&nbsp;</td>
		<td class="actions">
			<?php echo $this->Html->link(__('View'), array('action' => 'view', $profile['Profile']['id'])); ?>
			<?php echo $this->Html->link(__('Edit'), array('action' => 'edit', $profile['Profile']['id'])); ?>
			<?php echo $this->Form->postLink(__('Delete'), array('action' => 'delete', $profile['Profile']['id']), null, __('Are you sure you want to delete # %s?', $profile['Profile']['id'])); ?>
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
		<li><?php echo $this->Html->link(__('New Profile'), array('action' => 'add')); ?></li>
		<li><?php echo $this->Html->link(__('List Users'), array('controller' => 'users', 'action' => 'index')); ?> </li>
		<li><?php echo $this->Html->link(__('New User'), array('controller' => 'users', 'action' => 'add')); ?> </li>
	</ul>
</div>
