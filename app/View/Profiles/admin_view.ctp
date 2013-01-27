<div class="profiles view">
<h2><?php  echo __('Profile');?></h2>
	<dl>
		<dt><?php echo __('Id'); ?></dt>
		<dd>
			<?php echo h($profile['Profile']['id']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('User'); ?></dt>
		<dd>
			<?php echo $this->Html->link($profile['User']['username'], array('controller' => 'users', 'action' => 'view', $profile['User']['id'])); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Modified'); ?></dt>
		<dd>
			<?php echo h($profile['Profile']['modified']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Country'); ?></dt>
		<dd>
			<?php echo h($profile['Profile']['country']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Clan'); ?></dt>
		<dd>
			<?php echo h($profile['Profile']['clan']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Email'); ?></dt>
		<dd>
			<?php echo h($profile['Profile']['email']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Msn'); ?></dt>
		<dd>
			<?php echo h($profile['Profile']['msn']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Icq'); ?></dt>
		<dd>
			<?php echo h($profile['Profile']['icq']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Facebook'); ?></dt>
		<dd>
			<?php echo h($profile['Profile']['facebook']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('Twitter'); ?></dt>
		<dd>
			<?php echo h($profile['Profile']['twitter']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('About'); ?></dt>
		<dd>
			<?php echo h($profile['Profile']['about']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('HideProfile'); ?></dt>
		<dd>
			<?php echo h($profile['Profile']['hideProfile']); ?>
			&nbsp;
		</dd>
		<dt><?php echo __('HideEmail'); ?></dt>
		<dd>
			<?php echo h($profile['Profile']['hideEmail']); ?>
			&nbsp;
		</dd>
	</dl>
</div>
<div class="actions">
	<h3><?php echo __('Actions'); ?></h3>
	<ul>
		<li><?php echo $this->Html->link(__('Edit Profile'), array('action' => 'edit', $profile['Profile']['id'])); ?> </li>
		<li><?php echo $this->Form->postLink(__('Delete Profile'), array('action' => 'delete', $profile['Profile']['id']), null, __('Are you sure you want to delete # %s?', $profile['Profile']['id'])); ?> </li>
		<li><?php echo $this->Html->link(__('List Profiles'), array('action' => 'index')); ?> </li>
		<li><?php echo $this->Html->link(__('New Profile'), array('action' => 'add')); ?> </li>
		<li><?php echo $this->Html->link(__('List Users'), array('controller' => 'users', 'action' => 'index')); ?> </li>
		<li><?php echo $this->Html->link(__('New User'), array('controller' => 'users', 'action' => 'add')); ?> </li>
	</ul>
</div>
