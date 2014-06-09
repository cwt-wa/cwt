<div class="standings view">
    <h2><?php echo __('Standing'); ?></h2>
    <dl>
        <dt><?php echo __('Id'); ?></dt>
        <dd>
            <?php echo h($standing['Standing']['id']); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Group'); ?></dt>
        <dd>
            <?php echo $this->Html->link($standing['Group']['group'], array('controller' => 'groups', 'action' => 'view', $standing['Group']['id'])); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Points'); ?></dt>
        <dd>
            <?php echo h($standing['Standing']['points']); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Games'); ?></dt>
        <dd>
            <?php echo h($standing['Standing']['games']); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Game Ratio'); ?></dt>
        <dd>
            <?php echo h($standing['Standing']['game_ratio']); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Round Ratio'); ?></dt>
        <dd>
            <?php echo h($standing['Standing']['round_ratio']); ?>
            &nbsp;
        </dd>
    </dl>
</div>
<div class="actions">
    <h3><?php echo __('Actions'); ?></h3>
    <ul>
        <li><?php echo $this->Html->link(__('Edit Standing'), array('action' => 'edit', $standing['Standing']['id'])); ?> </li>
        <li><?php echo $this->Form->postLink(__('Delete Standing'), array('action' => 'delete', $standing['Standing']['id']), null, __('Are you sure you want to delete # %s?', $standing['Standing']['id'])); ?> </li>
        <li><?php echo $this->Html->link(__('List Standings'), array('action' => 'index')); ?> </li>
        <li><?php echo $this->Html->link(__('New Standing'), array('action' => 'add')); ?> </li>
        <li><?php echo $this->Html->link(__('List Groups'), array('controller' => 'groups', 'action' => 'index')); ?> </li>
        <li><?php echo $this->Html->link(__('New Group'), array('controller' => 'groups', 'action' => 'add')); ?> </li>
    </ul>
</div>
