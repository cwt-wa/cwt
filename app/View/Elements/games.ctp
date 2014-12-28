<?php echo $this->Html->css('groups', null, array('inline' => false)) ?>

<table border="0" align="center" cellpadding="5">
    <tr>
        <th class="bottomBorder"><?php echo $this->Paginator->sort('id', null); ?></th>
        <th class="bottomBorder"><?php echo $this->Paginator->sort('created'); ?></th>
        <th class="bottomBorder" align="right"><?php echo $this->Paginator->sort('home_id'); ?></th>
        <th class="bottomBorder"></th>
        <th class="bottomBorder" align="left"><?php echo $this->Paginator->sort('away_id'); ?></th>
        <th class="bottomBorder"><?php echo $this->Paginator->sort('tournament_id', 'Year'); ?></th>
        <th class="bottomBorder"><?php echo $this->Paginator->sort('stage', 'Stage', array('direction' => 'desc')); ?></th>
        <th class="bottomBorder"><?php echo $this->Paginator->sort('reporter_id', null, array('direction' => 'desc')); ?></th>
        <th class="bottomBorder"><?php echo $this->Paginator->sort('likes', null, array('direction' => 'desc')); ?></th>
        <th class="bottomBorder"><?php echo $this->Paginator->sort('comments', '&#9729;', array('escape' => false, 'direction' => 'desc')); ?></th>
        <th class="bottomBorder"><?php echo $this->Paginator->sort('downloads', '&darr;', array('escape' => false, 'direction' => 'desc')); ?></th>
        <th class="bottomBorder"></th>
    </tr>
    <?php foreach ($games as $game): ?>
        <tr>
            <td class="bottomBorder" align="right">
                <?php
                echo $this->Html->link($game['Game']['id'], '/games/view/' . $game['Game']['id']);
                ?>
            </td>
            <td class="bottomBorder">
                <?php
                echo $game['Game']['created']
                ?>
            </td>
            <td class="bottomBorder" width="140" align="right">
                <?php
                echo $this->Html->link($game['Home']['username'], '/users/view/' . $game['Home']['id']);
                ?>
            </td>
            <td class="bottomBorder" width="30" align="center">
                <?php echo $game['Game']['score_h'] ?>-<?php echo $game['Game']['score_a'] ?>
            </td>
            <td class="bottomBorder" width="140" align="left">
                <?php
                echo $this->Html->link($game['Away']['username'], '/users/view/' . $game['Away']['id']);
                ?>
            </td>
            <td class="bottomBorder">
                <?php
                echo $this->Html->link($game['Tournament']['year'], '/archive/' . $game['Tournament']['year']);
                ?>
            </td>
            <td class="bottomBorder" align="center">
                <?php if ($game['Game']['playoff_id'] == 0): ?>
                    Group
                <?php else: ?>
                    <?php echo $game['Playoff']['stepAssoc'] ?>
                <?php endif; ?>
            </td>
            <td class="bottomBorder" align="center">
                <?php echo $this->Html->link($game['Report']['username'], array('controller' => 'users', 'action' => 'view', $game['Report']['id'])); ?>
            </td>
            <td class="bottomBorder" width="55" align="center">
                <?php if (!$game['Game']['techwin']): ?>
                    <div
                        title="<?php echo $game['Rating']['likes'] ?> Likes, <?php echo $game['Rating']['dislikes'] ?>  Dislikes"
                        id="ratings">
                        <div class="ratingsBar"
                             style="width:<?php echo $game['Rating']['p']['likes'] ?>%; background-color:green;"></div><div class="ratingsBar"
                             style="width:<?php echo $game['Rating']['p']['dislikes'] ?>%; background-color:red;"></div>
                    </div>
                    <div
                        title="<?php echo $game['Rating']['lightside'] ?> Lightside, <?php echo $game['Rating']['darkside'] ?>  Darkside"
                        id="ratings">
                        <div class="ratingsBar"
                             style="width:<?php echo $game['Rating']['p']['lightside'] ?>%; background-color:white;"></div><div class="ratingsBar"
                             style="width:<?php echo $game['Rating']['p']['darkside'] ?>%; background-color:black;"></div>
                    </div>
                <?php else: ?>
                    <span style="font-size: 8pt;">Tech. Win</span>
                <?php endif; ?>
            </td>
            <td class="bottomBorder" width="24" align="center"
                style="background:url('/img/comment.png') no-repeat center center;">
                <?php echo count($game['Comment']) ?>
            </td>
            <td class="bottomBorder" width="24" align="center"
                style="background:url(/img/wa.png) no-repeat center center;">
                <div title="<?php echo $game['Game']['downloads'] ?> times downloaded">
                    <?php
                    echo $this->Html->link($game['Game']['downloads'],
                        '/games/download/' . $game['Game']['id'],
                        array(
                            'style' => 'color:white; font-weight:normal;'
                        ));
                    ?>
                </div>
            </td>
            <td class="bottomBorder" width="20" align="center">
                <?php
                echo $this->Html->link('GO', '/games/view/' . $game['Game']['id']);
                ?>
            </td>
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
</div>