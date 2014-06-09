<div class="infoboards form">
    <?php echo $this->Form->create('Infoboard'); ?>
    <fieldset>
        <legend><?php echo __('Edit Infoboard'); ?></legend>
        <?php
        echo $this->Form->input('id');
        echo $this->Form->input('message');
        echo $this->Form->input('user_id');
        echo $this->Form->input('category');
        ?>
    </fieldset>
    <?php echo $this->Form->end(__('Submit')); ?>
</div>
<div class="actions">
    <h3><?php echo __('Actions'); ?></h3>
    <ul>

        <li><?php echo $this->Form->postLink(__('Delete'), array('action' => 'delete', $this->Form->value('Infoboard.id')), null, __('Are you sure you want to delete # %s?', $this->Form->value('Infoboard.id'))); ?></li>
        <li><?php echo $this->Html->link(__('List Infoboards'), array('action' => 'index')); ?></li>
        <li><?php echo $this->Html->link(__('List Users'), array('controller' => 'users', 'action' => 'index')); ?> </li>
        <li><?php echo $this->Html->link(__('New User'), array('controller' => 'users', 'action' => 'add')); ?> </li>
    </ul>
</div>
