<div class="playoffs index">
    <h2><?php echo __('Playoffs'); ?></h2>
    <table cellpadding="0" cellspacing="0">
        <tr>
            <th><?php echo $this->Paginator->sort('id'); ?></th>
            <th><?php echo $this->Paginator->sort('step'); ?></th>
            <th><?php echo $this->Paginator->sort('spot'); ?></th>
            <th><?php echo $this->Paginator->sort('game_id'); ?></th>
            <th class="actions"><?php echo __('Actions'); ?></th>
        </tr>
        <?php
        foreach ($playoffs as $playoff): ?>
            <tr>
                <td><?php echo h($playoff['Playoff']['id']); ?>&nbsp;</td>
                <td><?php echo h($playoff['Playoff']['step']); ?>&nbsp;</td>
                <td><?php echo h($playoff['Playoff']['spot']); ?>&nbsp;</td>
                <td>
                    <?php echo $this->Html->link($playoff['Game']['id'], array('controller' => 'games', 'action' => 'view', $playoff['Game']['id'])); ?>
                </td>
                <td class="actions">
                    <?php echo $this->Html->link(__('View'), array('action' => 'view', $playoff['Playoff']['id'])); ?>
                    <?php echo $this->Html->link(__('Edit'), array('action' => 'edit', $playoff['Playoff']['id'])); ?>
                    <?php echo $this->Form->postLink(__('Delete'), array('action' => 'delete', $playoff['Playoff']['id']), null, __('Are you sure you want to delete # %s?', $playoff['Playoff']['id'])); ?>
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
        <li><?php echo $this->Html->link(__('New Playoff'), array('action' => 'add')); ?></li>
        <li><?php echo $this->Html->link(__('List Games'), array('controller' => 'games', 'action' => 'index')); ?> </li>
        <li><?php echo $this->Html->link(__('New Game'), array('controller' => 'games', 'action' => 'add')); ?> </li>
    </ul>
</div>
