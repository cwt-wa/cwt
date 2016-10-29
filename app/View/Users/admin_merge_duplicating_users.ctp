<?php $this->Html->script('merge-duplicate-users', array('inline' => false)); ?>

<div id="box" style="background-color:#3F3429; text-align: center">
    <h1>Merge duplicating users</h1>
</div>

<div id="box" style="background-color:#3F3429;">
    Some users have registered multiple times. Most of the times because they had forgotten their passwords and
    just created a new account rather than taking the hassle of the password reset process.<br>
    Of course you would want to keep one user account for the same person to better track their statistics.
</div>

<div id="box" style="background-color:#3F3429">
    <?php echo $this->Form->create('Merge'); ?>

    Merge the following user(s)<br>
    <?php echo $this->Form->select('Legacy', $users, array('name' => 'data[Merge][Legacy][0]')); ?><br>
    <span id="moreUsers"></span>
    <a href="" id="addLink">Add</a><br>
    <br>

    into this user<br>
    <?php echo $this->Form->input('User', array('label' => false)); ?><br>
    <br>

    <?php
    echo $this->Form->submit('Submit', array(
        'onclick' => 'return confirm(\'Are you sure?\')'
    ));
    ?>
    <?php echo $this->Form->end(); ?>
</div>


<?php echo $this->Form->select('Legacy', $users, array('id' => 'draft', 'style' => 'display: none;')); ?>