<div class="groups view">
    <h2><?php echo __('Group'); ?></h2>
    <dl>
        <dt><?php echo __('Id'); ?></dt>
        <dd>
            <?php echo h($group['Group']['id']); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Group'); ?></dt>
        <dd>
            <?php echo h($group['Group']['group']); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('User'); ?></dt>
        <dd>
            <?php echo $this->Html->link($group['User']['id'], array('controller' => 'users', 'action' => 'view', $group['User']['id'])); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Points'); ?></dt>
        <dd>
            <?php echo h($group['Group']['points']); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Games'); ?></dt>
        <dd>
            <?php echo h($group['Group']['games']); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Won'); ?></dt>
        <dd>
            <?php echo h($group['Group']['won']); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Lost'); ?></dt>
        <dd>
            <?php echo h($group['Group']['lost']); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Rounds'); ?></dt>
        <dd>
            <?php echo h($group['Group']['rounds']); ?>
            &nbsp;
        </dd>
    </dl>
</div>
<div class="actions">
    <h3><?php echo __('Actions'); ?></h3>
    <ul>
        <li><?php echo $this->Html->link(__('Edit Group'), array('action' => 'edit', $group['Group']['id'])); ?> </li>
        <li><?php echo $this->Form->postLink(__('Delete Group'), array('action' => 'delete', $group['Group']['id']), null, __('Are you sure you want to delete # %s?', $group['Group']['id'])); ?> </li>
        <li><?php echo $this->Html->link(__('List Groups'), array('action' => 'index')); ?> </li>
        <li><?php echo $this->Html->link(__('New Group'), array('action' => 'add')); ?> </li>
        <li><?php echo $this->Html->link(__('List Users'), array('controller' => 'users', 'action' => 'index')); ?> </li>
        <li><?php echo $this->Html->link(__('New User'), array('controller' => 'users', 'action' => 'add')); ?> </li>
    </ul>
</div>
