<div class="streams index">
    <h2><?php echo __('Streams'); ?></h2>
    <table cellpadding="0" cellspacing="0">
        <tr>
            <th><?php echo $this->Paginator->sort('id'); ?></th>
            <th><?php echo $this->Paginator->sort('title'); ?></th>
            <th><?php echo $this->Paginator->sort('description'); ?></th>
            <th><?php echo $this->Paginator->sort('embedcode'); ?></th>
            <th><?php echo $this->Paginator->sort('curr_views'); ?></th>
            <th><?php echo $this->Paginator->sort('alltime_views'); ?></th>
            <th><?php echo $this->Paginator->sort('color'); ?></th>
            <th><?php echo $this->Paginator->sort('user_id'); ?></th>
            <th><?php echo $this->Paginator->sort('created'); ?></th>
            <th><?php echo $this->Paginator->sort('modified'); ?></th>
            <th><?php echo $this->Paginator->sort('online'); ?></th>
            <th class="actions"><?php echo __('Actions'); ?></th>
        </tr>
        <?php
        foreach ($streams as $stream): ?>
            <tr>
                <td><?php echo h($stream['Stream']['id']); ?>&nbsp;</td>
                <td><?php echo h($stream['Stream']['title']); ?>&nbsp;</td>
                <td><?php echo h($stream['Stream']['description']); ?>&nbsp;</td>
                <td><?php echo h($stream['Stream']['embedcode']); ?>&nbsp;</td>
                <td><?php echo h($stream['Stream']['curr_views']); ?>&nbsp;</td>
                <td><?php echo h($stream['Stream']['alltime_views']); ?>&nbsp;</td>
                <td><?php echo h($stream['Stream']['color']); ?>&nbsp;</td>
                <td>
                    <?php echo $this->Html->link($stream['User']['username'], array('controller' => 'users', 'action' => 'view', $stream['User']['id'])); ?>
                </td>
                <td><?php echo h($stream['Stream']['created']); ?>&nbsp;</td>
                <td><?php echo h($stream['Stream']['modified']); ?>&nbsp;</td>
                <td><?php echo h($stream['Stream']['online']); ?>&nbsp;</td>
                <td class="actions">
                    <?php echo $this->Html->link(__('View'), array('action' => 'view', $stream['Stream']['id'])); ?>
                    <?php echo $this->Html->link(__('Edit'), array('action' => 'edit', $stream['Stream']['id'])); ?>
                    <?php echo $this->Form->postLink(__('Delete'), array('action' => 'delete', $stream['Stream']['id']), null, __('Are you sure you want to delete # %s?', $stream['Stream']['id'])); ?>
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
        <li><?php echo $this->Html->link(__('New Stream'), array('action' => 'add')); ?></li>
        <li><?php echo $this->Html->link(__('List Users'), array('controller' => 'users', 'action' => 'index')); ?> </li>
        <li><?php echo $this->Html->link(__('New User'), array('controller' => 'users', 'action' => 'add')); ?> </li>
    </ul>
</div>
