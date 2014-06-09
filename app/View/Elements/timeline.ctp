<?php
//$this->Html->script('jscrollbar', array('inline'=>false));
//$this->Html->script('scrollbars', array('inline'=>false));

$timeline = $this->requestAction('users/timeline/' . $user);
?>

<div id="timeline">
    <?php $year = 2002;
    foreach ($timeline as $achievement): ?>
        <div class="timeline_item">
            <?php echo $this->Html->image('timeline/' . $achievement . '.png'); ?>
            <br><?php echo $this->Html->link($year, '/archive/' . $year, array('style' => 'color:lightgray;'));
            $year++; ?>
        </div>
    <?php endforeach; ?>
</div>
