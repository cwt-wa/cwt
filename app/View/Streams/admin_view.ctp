<div class="streams view">
<h2><?php  echo __('Stream');?></h2>
	<dl>
		<dt><?php echo __('Id'); ?></dt>
		<dd>
			<?php echo h($stream['Stream']['id']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Title'); ?></dt>
		<dd>
			<?php echo h($stream['Stream']['title']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Description'); ?></dt>
		<dd>
			<?php echo h($stream['Stream']['description']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Embedcode'); ?></dt>
		<dd>
			<?php echo h($stream['Stream']['embedcode']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Curr Views'); ?></dt>
		<dd>
			<?php echo h($stream['Stream']['curr_views']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Alltime Views'); ?></dt>
		<dd>
			<?php echo h($stream['Stream']['alltime_views']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Color'); ?></dt>
		<dd>
			<?php echo h($stream['Stream']['color']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('User'); ?></dt>
		<dd>
			<?php echo $this->Html->link($stream['User']['username'], array('controller' => 'users', 'action' => 'view', $stream['User']['id'])); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Created'); ?></dt>
		<dd>
			<?php echo h($stream['Stream']['created']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Modified'); ?></dt>
		<dd>
			<?php echo h($stream['Stream']['modified']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Online'); ?></dt>
		<dd>
			<?php echo h($stream['Stream']['online']); ?>
			&nbsp;
		</dd>
	</dl>
</div>
<div class="actions">
	<h3><?php echo __('Actions'); ?></h3>
	<ul>
		<li><?php echo $this->Html->link(__('Edit Stream'), array('action' => 'edit', $stream['Stream']['id'])); ?> </li>
		<li><?php echo $this->Form->postLink(__('Delete Stream'), array('action' => 'delete', $stream['Stream']['id']), null, __('Are you sure you want to delete # %s?', $stream['Stream']['id'])); ?> </li>
		<li><?php echo $this->Html->link(__('List Streams'), array('action' => 'index')); ?> </li>
		<li><?php echo $this->Html->link(__('New Stream'), array('action' => 'add')); ?> </li>
		<li><?php echo $this->Html->link(__('List Users'), array('controller' => 'users', 'action' => 'index')); ?> </li>
		<li><?php echo $this->Html->link(__('New User'), array('controller' => 'users', 'action' => 'add')); ?> </li>
	</ul>
</div>
