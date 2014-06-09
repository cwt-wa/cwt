<div class="schedules index">
    <h2><?php echo __('Schedules'); ?></h2>
    <table cellpadding="0" cellspacing="0">
        <tr>
            <th><?php echo $this->Paginator->sort('id'); ?></th>
            <th><?php echo $this->Paginator->sort('user_id'); ?></th>
            <th><?php echo $this->Paginator->sort('home'); ?></th>
            <th><?php echo $this->Paginator->sort('away'); ?></th>
            <th><?php echo $this->Paginator->sort('schedule'); ?></th>
            <th><?php echo $this->Paginator->sort('created'); ?></th>
            <th class="actions"><?php echo __('Actions'); ?></th>
        </tr>
        <?php
        foreach ($schedules as $schedule): ?>
            <tr>
                <td><?php echo h($schedule['Schedule']['id']); ?>&nbsp;</td>
                <td>
                    <?php echo $this->Html->link($schedule['User']['username'], array('controller' => 'users', 'action' => 'view', $schedule['User']['id'])); ?>
                </td>
                <td><?php echo h($schedule['Schedule']['home']); ?>&nbsp;</td>
                <td><?php echo h($schedule['Schedule']['away']); ?>&nbsp;</td>
                <td><?php echo h($schedule['Schedule']['schedule']); ?>&nbsp;</td>
                <td><?php echo h($schedule['Schedule']['created']); ?>&nbsp;</td>
                <td class="actions">
                    <?php echo $this->Html->link(__('View'), array('action' => 'view', $schedule['Schedule']['id'])); ?>
                    <?php echo $this->Html->link(__('Edit'), array('action' => 'edit', $schedule['Schedule']['id'])); ?>
                    <?php echo $this->Form->postLink(__('Delete'), array('action' => 'delete', $schedule['Schedule']['id']), null, __('Are you sure you want to delete # %s?', $schedule['Schedule']['id'])); ?>
                </td>
            </tr>
        <?php endforeach; ?>
    </table>
    <p>
        <?php
        echo $this->Paginator->counter(array(
            'format' => __('Page {:page} of {:pages}, showing {:current} records out of {:count} total, starting on record {:start}, ending on {:end}')
        ));
        ?>    </p>

    <div class="paging">
        <?php
        echo $this->Paginator->prev('< ' . __('previous'), array(), null, array('class' => 'prev disabled'));
        echo $this->Paginator->numbers(array('separator' => ''));
        echo $this->Paginator->next(__('next') . ' >', array(), null, array('class' => 'next disabled'));
        ?>
    </div>
</div>
<div class="actions">
    <h3><?php echo __('Actions'); ?></h3>
    <ul>
        <li><?php echo $this->Html->link(__('New Schedule'), array('action' => 'add')); ?></li>
        <li><?php echo $this->Html->link(__('List Users'), array('controller' => 'users', 'action' => 'index')); ?> </li>
        <li><?php echo $this->Html->link(__('New User'), array('controller' => 'users', 'action' => 'add')); ?> </li>
    </ul>
</div>
