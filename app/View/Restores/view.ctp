<div class="restores view">
<h2><?php  echo __('Restore'); ?></h2>
	<dl>
		<dt><?php echo __('Id'); ?></dt>
		<dd>
			<?php echo h($restore['Restore']['id']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('User Id'); ?></dt>
		<dd>
			<?php echo h($restore['Restore']['user_id']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Tournament'); ?></dt>
		<dd>
			<?php echo $this->Html->link($restore['Tournament']['year'], array('controller' => 'tournaments', 'action' => 'view', $restore['Tournament']['id'])); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Home'); ?></dt>
		<dd>
			<?php echo $this->Html->link($restore['Home']['username'], array('controller' => 'users', 'action' => 'view', $restore['Home']['id'])); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Away'); ?></dt>
		<dd>
			<?php echo $this->Html->link($restore['Away']['username'], array('controller' => 'users', 'action' => 'view', $restore['Away']['id'])); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Score H'); ?></dt>
		<dd>
			<?php echo h($restore['Restore']['score_h']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Score A'); ?></dt>
		<dd>
			<?php echo h($restore['Restore']['score_a']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Stage'); ?></dt>
		<dd>
			<?php echo h($restore['Restore']['stage']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Reported'); ?></dt>
		<dd>
			<?php echo h($restore['Restore']['reported']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Created'); ?></dt>
		<dd>
			<?php echo h($restore['Restore']['created']); ?>
			&nbsp;
		</dd>
	</dl>
</div>
<div class="actions">
	<h3><?php echo __('Actions'); ?></h3>
	<ul>
		<li><?php echo $this->Html->link(__('Edit Restore'), array('action' => 'edit', $restore['Restore']['id'])); ?> </li>
		<li><?php echo $this->Form->postLink(__('Delete Restore'), array('action' => 'delete', $restore['Restore']['id']), null, __('Are you sure you want to delete # %s?', $restore['Restore']['id'])); ?> </li>
		<li><?php echo $this->Html->link(__('List Restores'), array('action' => 'index')); ?> </li>
		<li><?php echo $this->Html->link(__('New Restore'), array('action' => 'add')); ?> </li>
		<li><?php echo $this->Html->link(__('List Tournaments'), array('controller' => 'tournaments', 'action' => 'index')); ?> </li>
		<li><?php echo $this->Html->link(__('New Tournament'), array('controller' => 'tournaments', 'action' => 'add')); ?> </li>
		<li><?php echo $this->Html->link(__('List Users'), array('controller' => 'users', 'action' => 'index')); ?> </li>
		<li><?php echo $this->Html->link(__('New Home'), array('controller' => 'users', 'action' => 'add')); ?> </li>
	</ul>
</div>
