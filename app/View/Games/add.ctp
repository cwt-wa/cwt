<?php echo $this->Html->script('report', array('inline' => true)); ?>

<div id="report">
    <?php echo $this->Form->create('Report', array('type' => 'file')) ?>
    <?php
    echo $this->Form->select('user',
        array(
            $current_user['id'] => $current_user['username']
        ),
        array(
            'disabled' => 'disabled',
            'value' => $current_user['username'],
            'empty' => false
        ));
    ?>&nbsp;
    <?php
    echo $this->Form->select('userScore', $allowedResults);
    ?>-<?php
    echo $this->Form->select('opponentScore', $allowedResults);
    ?>&nbsp;
    <?php echo $this->Form->select('opponent', $opponents); ?>
    <br>
    <?php echo $this->Form->file('Report.replays', array('div' => false)); ?>
    <br>
    <span id="preview" style="font-weight:bold;"></span>
    <?php
    echo $this->Form->submit('Report this Game', array(
        'disabled' => 'disabled',
        'id' => 'reportSubmit',
        'div' => false
    ));
    ?>
    <?php echo $this->Form->end(); ?>
</div>
