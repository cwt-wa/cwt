<div class="schedules view">
    <h2><?php echo __('Schedule'); ?></h2>
    <dl>
        <dt><?php echo __('Id'); ?></dt>
        <dd>
            <?php echo h($schedule['Schedule']['id']); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('User'); ?></dt>
        <dd>
            <?php echo $this->Html->link($schedule['User']['username'], array('controller' => 'users', 'action' => 'view', $schedule['User']['id'])); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Home'); ?></dt>
        <dd>
            <?php echo h($schedule['Schedule']['home']); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Away'); ?></dt>
        <dd>
            <?php echo h($schedule['Schedule']['away']); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Schedule'); ?></dt>
        <dd>
            <?php echo h($schedule['Schedule']['schedule']); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Created'); ?></dt>
        <dd>
            <?php echo h($schedule['Schedule']['created']); ?>
            &nbsp;
        </dd>
    </dl>
</div>
<div class="actions">
    <h3><?php echo __('Actions'); ?></h3>
    <ul>
        <li><?php echo $this->Html->link(__('Edit Schedule'), array('action' => 'edit', $schedule['Schedule']['id'])); ?> </li>
        <li><?php echo $this->Form->postLink(__('Delete Schedule'), array('action' => 'delete', $schedule['Schedule']['id']), null, __('Are you sure you want to delete # %s?', $schedule['Schedule']['id'])); ?> </li>
        <li><?php echo $this->Html->link(__('List Schedules'), array('action' => 'index')); ?> </li>
        <li><?php echo $this->Html->link(__('New Schedule'), array('action' => 'add')); ?> </li>
        <li><?php echo $this->Html->link(__('List Users'), array('controller' => 'users', 'action' => 'index')); ?> </li>
        <li><?php echo $this->Html->link(__('New User'), array('controller' => 'users', 'action' => 'add')); ?> </li>
    </ul>
</div>
