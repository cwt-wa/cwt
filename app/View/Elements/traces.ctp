<?php echo $this->Html->css('groups', null, array('inline' => false)) ?>

<table border="0" align="center" cellpadding="10">
    <tr>
        <th class="bottomBorder" align="center"><?php echo $this->Paginator->sort('user_id'); ?></th>
        <th class="bottomBorder" align="center"><?php echo $this->Paginator->sort('additional', 'Voted for'); ?></th>
        <th class="bottomBorder" colspan="3"><?php echo $this->Paginator->sort('on', 'Game'); ?></th>
        <th class="bottomBorder"></th>
        <th class="bottomBorder" align="center"><?php echo $this->Paginator->sort('controller', 'Type'); ?></th>
        <th class="bottomBorder"><?php echo $this->Paginator->sort('created'); ?></th>
    </tr>
    <?php foreach ($traces as $trace): ?>
        <tr>
            <td class="bottomBorder" align="center">
                <?php echo $this->Html->link($trace['User']['username'], array('controller' => 'users', 'action' => 'view', $trace['User']['id'])); ?>
            </td>
            <td class="bottomBorder" align="center">
                <?php if ($trace['Trace']['additional'] == 'bet_h'): ?>
                    <?php
                    echo $this->Html->link($trace['Game']['Home']['User']['username'], '/users/view/' . $trace['Game']['Home']['User']['id']);
                    ?>
                <?php elseif ($trace['Trace']['additional'] == 'bet_a'): ?>
                    <?php
                    echo $this->Html->link($trace['Game']['Away']['User']['username'], '/users/view/' . $trace['Game']['Away']['User']['id']);
                    ?>
                <?php else: ?>
                    <?php if ($trace['Trace']['additional'] == 'likes'): ?>
                        <span style="color:green;"><?php echo h($trace['Trace']['additional']); ?></span>
                    <?php elseif ($trace['Trace']['additional'] == 'dislikes'): ?>
                        <span style="color:red;"><?php echo h($trace['Trace']['additional']); ?></span>
                    <?php elseif ($trace['Trace']['additional'] == 'lightside'): ?>
                        <span><?php echo h($trace['Trace']['additional']); ?></span>
                    <?php elseif ($trace['Trace']['additional'] == 'darkside'): ?>
                        <span style="color:black;"><?php echo h($trace['Trace']['additional']); ?></span>
                    <?php endif; ?>
                <?php endif; ?>
            </td>
            <td class="bottomBorder" width="140" align="right">
                <?php
                echo $this->Html->link($trace['Game']['Home']['User']['username'], '/users/view/' . $trace['Game']['Home']['User']['id']);
                ?>
            </td>
            <td class="bottomBorder" width="30" align="center">
                <?php if ($trace['Game']['score_h'] == 0 && $trace['Game']['score_a'] == 0): ?>
                    vs
                <?php else: ?>
                    <?php echo $trace['Game']['score_h'] ?>-<?php echo $trace['Game']['score_a'] ?>
                <?php endif; ?>
            </td>
            <td class="bottomBorder" width="140" align="left">
                <?php
                echo $this->Html->link($trace['Game']['Away']['User']['username'], '/users/view/' . $trace['Game']['Away']['User']['id']);
                ?>
            </td>
            <td class="bottomBorder" width="20" align="center">
                <?php
                echo $this->Html->link('GO', '/games/view/' . $trace['Game']['id']);
                ?>
            </td>
            <td class="bottomBorder" align="center">
                <?php echo h($trace['Trace']['controller']); ?>
            </td>
            <td class="bottomBorder"><?php echo h($trace['Trace']['created']); ?></td>
        </tr>
    <?php endforeach; ?>
</table>
<p style="text-align:center;">
    <?php
    echo $this->Paginator->counter(array(
        'format' => __('Page {:page} of {:pages}, showing {:current} records out of {:count} total, starting on record {:start}, ending on {:end}')
    ));
    ?>  </p>
<div class="paging">
    <?php
    echo $this->Paginator->prev('< ' . __('previous'), array(), null, array('class' => 'prev disabled'));
    echo $this->Paginator->numbers(array('separator' => ''));
    echo $this->Paginator->next(__('next') . ' >', array(), null, array('class' => 'next disabled'));
    ?>
</div>
