<div class="games index">
    <h2><?php echo __('Games'); ?></h2>
    <table cellpadding="0" cellspacing="0">
        <tr>
            <th><?php echo $this->Paginator->sort('id'); ?></th>
            <th><?php echo $this->Paginator->sort('group_id'); ?></th>
            <th><?php echo $this->Paginator->sort('playoff_id'); ?></th>
            <th><?php echo $this->Paginator->sort('home_id'); ?></th>
            <th><?php echo $this->Paginator->sort('away_id'); ?></th>
            <th><?php echo $this->Paginator->sort('score_h'); ?></th>
            <th><?php echo $this->Paginator->sort('score_a'); ?></th>
            <th><?php echo $this->Paginator->sort('created'); ?></th>
            <th><?php echo $this->Paginator->sort('reporter_id'); ?></th>
            <th class="actions"><?php echo __('Actions'); ?></th>
        </tr>
        <?php
        foreach ($games as $game): ?>
            <tr>
                <td><?php echo h($game['Game']['id']); ?>&nbsp;</td>
                <td>
                    <?php echo $this->Html->link($game['Group']['group'], array('controller' => 'groups', 'action' => 'view', $game['Group']['id'])); ?>
                </td>
                <td>
                    <?php echo $this->Html->link($game['Step']['id'], array('controller' => 'playoffs', 'action' => 'view', $game['Step']['id'])); ?>
                </td>
                <td>
                    <?php echo $this->Html->link($game['Home']['username'], array('controller' => 'users', 'action' => 'view', $game['Home']['id'])); ?>
                </td>
                <td>
                    <?php echo $this->Html->link($game['Away']['username'], array('controller' => 'users', 'action' => 'view', $game['Away']['id'])); ?>
                </td>
                <td><?php echo h($game['Game']['score_h']); ?>&nbsp;</td>
                <td><?php echo h($game['Game']['score_a']); ?>&nbsp;</td>
                <td><?php echo h($game['Game']['created']); ?>&nbsp;</td>
                <td>
                    <?php echo $this->Html->link($game['Report']['username'], array('controller' => 'users', 'action' => 'view', $game['Report']['id'])); ?>
                </td>
                <td class="actions">
                    <?php echo $this->Html->link(__('View'), array('action' => 'view', $game['Game']['id'])); ?>
                    <?php echo $this->Html->link(__('Edit'), array('action' => 'edit', $game['Game']['id'])); ?>
                    <?php echo $this->Form->postLink(__('Delete'), array('action' => 'delete', $game['Game']['id']), null, __('Are you sure you want to delete # %s?', $game['Game']['id'])); ?>
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
        <li><?php echo $this->Html->link(__('New Game'), array('action' => 'add')); ?></li>
        <li><?php echo $this->Html->link(__('List Playoffs'), array('controller' => 'playoffs', 'action' => 'index')); ?> </li>
        <li><?php echo $this->Html->link(__('New Step'), array('controller' => 'playoffs', 'action' => 'add')); ?> </li>
        <li><?php echo $this->Html->link(__('List Groups'), array('controller' => 'groups', 'action' => 'index')); ?> </li>
        <li><?php echo $this->Html->link(__('New Group'), array('controller' => 'groups', 'action' => 'add')); ?> </li>
        <li><?php echo $this->Html->link(__('List Users'), array('controller' => 'users', 'action' => 'index')); ?> </li>
        <li><?php echo $this->Html->link(__('New Home'), array('controller' => 'users', 'action' => 'add')); ?> </li>
    </ul>
</div>
