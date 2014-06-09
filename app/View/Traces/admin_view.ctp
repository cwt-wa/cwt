<div class="traces view">
    <h2><?php echo __('Trace'); ?></h2>
    <dl>
        <dt><?php echo __('Id'); ?></dt>
        <dd>
            <?php echo h($trace['Trace']['id']); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('User'); ?></dt>
        <dd>
            <?php echo $this->Html->link($trace['User']['username'], array('controller' => 'users', 'action' => 'view', $trace['User']['id'])); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Controller'); ?></dt>
        <dd>
            <?php echo h($trace['Trace']['controller']); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Action'); ?></dt>
        <dd>
            <?php echo h($trace['Trace']['action']); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Additional'); ?></dt>
        <dd>
            <?php echo h($trace['Trace']['additional']); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Created'); ?></dt>
        <dd>
            <?php echo h($trace['Trace']['created']); ?>
            &nbsp;
        </dd>
    </dl>
</div>
<div class="actions">
    <h3><?php echo __('Actions'); ?></h3>
    <ul>
        <li><?php echo $this->Html->link(__('Edit Trace'), array('action' => 'edit', $trace['Trace']['id'])); ?> </li>
        <li><?php echo $this->Form->postLink(__('Delete Trace'), array('action' => 'delete', $trace['Trace']['id']), null, __('Are you sure you want to delete # %s?', $trace['Trace']['id'])); ?> </li>
        <li><?php echo $this->Html->link(__('List Traces'), array('action' => 'index')); ?> </li>
        <li><?php echo $this->Html->link(__('New Trace'), array('action' => 'add')); ?> </li>
        <li><?php echo $this->Html->link(__('List Users'), array('controller' => 'users', 'action' => 'index')); ?> </li>
        <li><?php echo $this->Html->link(__('New User'), array('controller' => 'users', 'action' => 'add')); ?> </li>
    </ul>
</div>
