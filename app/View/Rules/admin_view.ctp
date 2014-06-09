<div class="rules view">
    <h2><?php echo __('Rule'); ?></h2>
    <dl>
        <dt><?php echo __('Id'); ?></dt>
        <dd>
            <?php echo h($rule['Rule']['id']); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Text'); ?></dt>
        <dd>
            <?php echo h($rule['Rule']['text']); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Created Id'); ?></dt>
        <dd>
            <?php echo h($rule['Rule']['created_id']); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Modified Id'); ?></dt>
        <dd>
            <?php echo h($rule['Rule']['modified_id']); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Created'); ?></dt>
        <dd>
            <?php echo h($rule['Rule']['created']); ?>
            &nbsp;
        </dd>
        <dt><?php echo __('Modified'); ?></dt>
        <dd>
            <?php echo h($rule['Rule']['modified']); ?>
            &nbsp;
        </dd>
    </dl>
</div>
<div class="actions">
    <h3><?php echo __('Actions'); ?></h3>
    <ul>
        <li><?php echo $this->Html->link(__('Edit Rule'), array('action' => 'edit', $rule['Rule']['id'])); ?> </li>
        <li><?php echo $this->Form->postLink(__('Delete Rule'), array('action' => 'delete', $rule['Rule']['id']), null, __('Are you sure you want to delete # %s?', $rule['Rule']['id'])); ?> </li>
        <li><?php echo $this->Html->link(__('List Rules'), array('action' => 'index')); ?> </li>
        <li><?php echo $this->Html->link(__('New Rule'), array('action' => 'add')); ?> </li>
    </ul>
</div>
