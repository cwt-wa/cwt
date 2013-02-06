<?php
// debug($restores)

$this->Paginator->options(array(
    'update' => '.RefreshTheIndexPage',
    'evalScripts' => true
));
?>
<div class="RefreshTheIndexPage">
    <h2><?php echo __('Restored Games'); ?></h2>
    <table cellpadding="10" cellspacing="0" align="center">
        <tr>
            <th><?php echo $this->Paginator->sort('id', '#'); ?></th>
            <th><?php echo $this->Paginator->sort('submitter_id', 'Restored by'); ?></th>
            <th><?php echo $this->Paginator->sort('tournament_id'); ?></th>
            <th><?php echo $this->Paginator->sort('home_id'); ?></th>
            <th><?php echo $this->Paginator->sort('score_h'); ?></th>
            <th><?php echo $this->Paginator->sort('score_a'); ?></th>
            <th><?php echo $this->Paginator->sort('away_id'); ?></th>
            <th><?php echo $this->Paginator->sort('tech_win'); ?></th>
            <th><?php echo $this->Paginator->sort('stage'); ?></th>
            <th><?php echo $this->Paginator->sort('reported'); ?></th>
            <th><?php echo $this->Paginator->sort('created'); ?></th>
        </tr>
        <?php foreach ($restores as $restore): ?>
            <tr>
                <td><?php echo h($restore['Restore']['id']); ?></td>
                <td>
                    <?php
                    echo $this->Html->link(
                            $restore['Submitter']['username'], '/users/view/' . $restore['Submitter']['id']);
                    ?>
                </td>
                <td><?php echo h($restore['Tournament']['year']); ?></td>
                <td>
                    <?php
                    echo $this->Html->link(
                            $restore['Home']['username'], '/users/view/' . $restore['Home']['id']);
                    ?>
                </td>
                <td><?php echo h($restore['Restore']['score_h']); ?></td>
                <td><?php echo h($restore['Restore']['score_a']); ?></td>
                <td>
                    <?php
                    echo $this->Html->link(
                            $restore['Away']['username'], '/users/view/' . $restore['Away']['id']);
                    ?>
                </td>
                <td>
                    <?php
                    if ($restore['Restore']['tech_win']) {
                        echo '<b>yes</b>';
                    } else {
                        echo 'no';
                    }
                    ?>
                </td>
                <td><?php echo h($restore['Restore']['stage']); ?></td>
                <td><?php echo h($restore['Restore']['reported']); ?></td>
                <td><?php echo h($restore['Restore']['created']); ?></td>
            </tr>
        <?php endforeach; ?>
    </table>
    <p>
        <?php
        echo $this->Paginator->counter(array(
            'format' => __('Page {:page} of {:pages}, showing {:current} records out of {:count} total, starting on record {:start}, ending on {:end}')
        ));
        ?>	</p>

    <div class="paging">
        <?php
        echo $this->Paginator->prev('< ' . __('previous'), array(), null, array('class' => 'prev disabled'));
        echo $this->Paginator->numbers(array('separator' => ''));
        echo $this->Paginator->next(__('next') . ' >', array(), null, array('class' => 'next disabled'));
        ?>
    </div>
</div>
<?php echo $this->Js->writeBuffer(); ?>