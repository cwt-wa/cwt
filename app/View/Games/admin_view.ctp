<div class="games view">
    <h2><?php echo __('Game'); ?></h2>
    <dl>
        <dt><?php echo __('Id'); ?></dt>
        <dd>
            <?php echo h($game['Game']['id']); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Group'); ?></dt>
        <dd>
            <?php echo $this->Html->link($game['Group']['group'], array('controller' => 'groups', 'action' => 'view', $game['Group']['id'])); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Step'); ?></dt>
        <dd>
            <?php echo $this->Html->link($game['Step']['id'], array('controller' => 'playoffs', 'action' => 'view', $game['Step']['id'])); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Home'); ?></dt>
        <dd>
            <?php echo $this->Html->link($game['Home']['username'], array('controller' => 'users', 'action' => 'view', $game['Home']['id'])); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Away'); ?></dt>
        <dd>
            <?php echo $this->Html->link($game['Away']['username'], array('controller' => 'users', 'action' => 'view', $game['Away']['id'])); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Score H'); ?></dt>
        <dd>
            <?php echo h($game['Game']['score_h']); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Score A'); ?></dt>
        <dd>
            <?php echo h($game['Game']['score_a']); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Created'); ?></dt>
        <dd>
            <?php echo h($game['Game']['created']); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Report'); ?></dt>
        <dd>
            <?php echo $this->Html->link($game['Report']['username'], array('controller' => 'users', 'action' => 'view', $game['Report']['id'])); ?>
            &nbsp;
        </dd>
    </dl>
</div>
<div class="actions">
    <h3><?php echo __('Actions'); ?></h3>
    <ul>
        <li><?php echo $this->Html->link(__('Edit Game'), array('action' => 'edit', $game['Game']['id'])); ?> </li>
        <li><?php echo $this->Form->postLink(__('Delete Game'), array('action' => 'delete', $game['Game']['id']), null, __('Are you sure you want to delete # %s?', $game['Game']['id'])); ?> </li>
        <li><?php echo $this->Html->link(__('List Games'), array('action' => 'index')); ?> </li>
        <li><?php echo $this->Html->link(__('New Game'), array('action' => 'add')); ?> </li>
        <li><?php echo $this->Html->link(__('List Playoffs'), array('controller' => 'playoffs', 'action' => 'index')); ?> </li>
        <li><?php echo $this->Html->link(__('New Step'), array('controller' => 'playoffs', 'action' => 'add')); ?> </li>
        <li><?php echo $this->Html->link(__('List Groups'), array('controller' => 'groups', 'action' => 'index')); ?> </li>
        <li><?php echo $this->Html->link(__('New Group'), array('controller' => 'groups', 'action' => 'add')); ?> </li>
        <li><?php echo $this->Html->link(__('List Users'), array('controller' => 'users', 'action' => 'index')); ?> </li>
        <li><?php echo $this->Html->link(__('New Home'), array('controller' => 'users', 'action' => 'add')); ?> </li>
    </ul>
</div>
<div class="related">
    <h3><?php echo __('Related Playoffs'); ?></h3>
    <?php if (!empty($game['Playoff'])): ?>
        <dl>
            <dt><?php echo __('Id'); ?></dt>
            <dd>
                <?php echo $game['Playoff']['id']; ?>
                &nbsp;</dd>
            <dt><?php echo __('StepAssoc'); ?></dt>
            <dd>
                <?php echo $game['Playoff']['stepAssoc']; ?>
                &nbsp;</dd>
            <dt><?php echo __('Step'); ?></dt>
            <dd>
                <?php echo $game['Playoff']['step']; ?>
                &nbsp;</dd>
            <dt><?php echo __('Spot'); ?></dt>
            <dd>
                <?php echo $game['Playoff']['spot']; ?>
                &nbsp;</dd>
            <dt><?php echo __('Game Id'); ?></dt>
            <dd>
                <?php echo $game['Playoff']['game_id']; ?>
                &nbsp;</dd>
        </dl>
    <?php endif; ?>
    <div class="actions">
        <ul>
            <li><?php echo $this->Html->link(__('Edit Playoff'), array('controller' => 'playoffs', 'action' => 'edit', $game['Playoff']['id'])); ?></li>
        </ul>
    </div>
</div>
