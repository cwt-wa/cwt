<div id="box" style="background-color:#2F2923">
	<h2><?php echo __('Games'); ?></h2>
	<table cellpadding="0" cellspacing="0">
	<tr>
			<th><?php echo $this->Paginator->sort('id'); ?></th>
			<th><?php echo $this->Paginator->sort('tournament_id'); ?></th>
			<th><?php echo $this->Paginator->sort('group_id'); ?></th>
			<th><?php echo $this->Paginator->sort('playoff_id'); ?></th>
			<th><?php echo $this->Paginator->sort('home_id'); ?></th>
			<th><?php echo $this->Paginator->sort('away_id'); ?></th>
			<th><?php echo $this->Paginator->sort('score_h'); ?></th>
			<th><?php echo $this->Paginator->sort('score_a'); ?></th>
			<th><?php echo $this->Paginator->sort('techwin'); ?></th>
			<th><?php echo $this->Paginator->sort('downloads'); ?></th>
			<th><?php echo $this->Paginator->sort('created'); ?></th>
			<th><?php echo $this->Paginator->sort('reporter_id'); ?></th>
	</tr>
	<?php foreach ($games as $game): ?>
	<tr>
		<td><?php echo h($game['Game']['id']); ?>&nbsp;</td>
		<td><?php echo h($game['Tournament']['year']); ?>&nbsp;</td>
		<td>
			<?php echo $this->Html->link($game['Group']['label'], array('controller' => 'groups', 'action' => 'view', $game['Group']['id'])); ?>
		</td>
		<td><?php echo h($game['Game']['playoff_id']); ?>&nbsp;</td>
		<td>
			<?php echo $this->Html->link($game['Home']['username'], array('controller' => 'users', 'action' => 'view', $game['Home']['id'])); ?>
		</td>
		<td>
			<?php echo $this->Html->link($game['Away']['username'], array('controller' => 'users', 'action' => 'view', $game['Away']['id'])); ?>
		</td>
		<td><?php echo h($game['Game']['score_h']); ?>&nbsp;</td>
		<td><?php echo h($game['Game']['score_a']); ?>&nbsp;</td>
		<td><?php echo h($game['Game']['techwin']); ?>&nbsp;</td>
		<td><?php echo h($game['Game']['downloads']); ?>&nbsp;</td>
		<td><?php echo h($game['Game']['created']); ?>&nbsp;</td>
		<td>
			<?php echo $this->Html->link($game['Report']['username'], array('controller' => 'users', 'action' => 'view', $game['Report']['id'])); ?>
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


<div id="box" style="background-color:#2F2923">
<?php
	echo $this->element('games', array('games' => $games));
?>
</div>

