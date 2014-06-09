<div class="playoffs form">
    <?php echo $this->Form->create('Playoff'); ?>
    <fieldset>
        <legend><?php echo __('Edit Playoff'); ?></legend>
        <?php
        echo $this->Form->input('id');
        echo $this->Form->input('step');
        echo $this->Form->input('spot');
        echo $this->Form->input('game_id');
        ?>
    </fieldset>
    <?php echo $this->Form->end(__('Submit')); ?>
</div>
<div class="actions">
    <h3><?php echo __('Actions'); ?></h3>
    <ul>

        <li><?php echo $this->Form->postLink(__('Delete'), array('action' => 'delete', $this->Form->value('Playoff.id')), null, __('Are you sure you want to delete # %s?', $this->Form->value('Playoff.id'))); ?></li>
        <li><?php echo $this->Html->link(__('List Playoffs'), array('action' => 'index')); ?></li>
        <li><?php echo $this->Html->link(__('List Games'), array('controller' => 'games', 'action' => 'index')); ?> </li>
        <li><?php echo $this->Html->link(__('New Game'), array('controller' => 'games', 'action' => 'add')); ?> </li>
    </ul>
</div>
