<?php echo $this->Html->script('game', array('inline' => false)); ?>

<?php if (isset($user)): ?>
    <div id="box" style="background-color:#2F2923; text-align:center;">
        <h1>
            <span style="font-weight:normal;">Comments of</span>
            <?php
            echo $this->Html->link($user['User']['username'], '/users/view/' . $user['User']['id']);
            ?>
        </h1>
    </div>
<?php else: ?>
    <div id="box" style="background-color:#2F2923; text-align:center;">
        <h1>Comments</h1>
    </div>
<?php endif; ?>

<?php
$options = array(
    'update' => '#content',
    'evalScripts' => true
);
$this->Paginator->options($options);
?>

<div id="box" style="background-color:#2F2923; text-align:right">
    Order by
    <?php echo $this->Paginator->sort('id'); ?>,
    <?php echo $this->Paginator->sort('game_id'); ?>,
    <?php echo $this->Paginator->sort('created'); ?>,
    <?php echo $this->Paginator->sort('modified'); ?>,
</div>

<?php echo $this->element('comments', array('comments' => $comments)); ?>

<div id="box" style="background-color:#2F2923">
    <p style="text-align:center;">
        <?php
        echo $this->Paginator->counter(array(
            'format' => __('Page {:page} of {:pages}, showing {:current} records out of {:count} total, starting on record {:start}, ending on {:end}')
        ));
        ?>  </p>
    <div class="paging">
        <?php
        echo $this->Paginator->prev('< ' . __('previous'), array(), null, array('class' => 'prev disabled'));
        echo $this->Paginator->numbers(array('separator' => '', 'modulus' => 24));
        echo $this->Paginator->next(__('next') . ' >', array(), null, array('class' => 'next disabled'));
        ?>
    </div>
</div>
<?php echo $this->Js->writeBuffer(); // Write cached scripts ?>
