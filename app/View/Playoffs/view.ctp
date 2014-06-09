<div class="playoffs view">
    <h2><?php echo __('Playoff'); ?></h2>
    <dl>
        <dt><?php echo __('Id'); ?></dt>
        <dd>
            <?php echo h($playoff['Playoff']['id']); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Step'); ?></dt>
        <dd>
            <?php echo h($playoff['Playoff']['step']); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Spot'); ?></dt>
        <dd>
            <?php echo h($playoff['Playoff']['spot']); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Game'); ?></dt>
        <dd>
            <?php echo $this->Html->link($playoff['Game']['id'], array('controller' => 'games', 'action' => 'view', $playoff['Game']['id'])); ?>
            &nbsp;
        </dd>
    </dl>
</div>
<div class="actions">
    <h3><?php echo __('Actions'); ?></h3>
    <ul>
        <li><?php echo $this->Html->link(__('Edit Playoff'), array('action' => 'edit', $playoff['Playoff']['id'])); ?> </li>
        <li><?php echo $this->Form->postLink(__('Delete Playoff'), array('action' => 'delete', $playoff['Playoff']['id']), null, __('Are you sure you want to delete # %s?', $playoff['Playoff']['id'])); ?> </li>
        <li><?php echo $this->Html->link(__('List Playoffs'), array('action' => 'index')); ?> </li>
        <li><?php echo $this->Html->link(__('New Playoff'), array('action' => 'add')); ?> </li>
        <li><?php echo $this->Html->link(__('List Games'), array('controller' => 'games', 'action' => 'index')); ?> </li>
        <li><?php echo $this->Html->link(__('New Game'), array('controller' => 'games', 'action' => 'add')); ?> </li>
    </ul>
</div>
