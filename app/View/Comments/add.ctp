<?php
echo $this->Html->script('bbcode', array('inline' => false));
echo $this->Html->css('bbcode', null, array('inline' => false));
?>

    <div id="box" style="background-color:#3F2828">
        <center><h1>Write a comment for this game:</h1></center>
    </div>

    <div id="box" style="background-color:#1A2427; text-align:center; font-size:16pt; margin-top:0px">
        <font color="lightgray"><?php echo $comment['stage'] ?>:</font>
        <?php
        echo $this->Html->link($comment['Home']['username'],
            '/users/view/' . $comment['Home']['id']);
        ?>
        <?php echo $comment['Game']['score_h']; ?>-<?php echo $comment['Game']['score_a']; ?>
        <?php
        echo $this->Html->link($comment['Away']['username'],
            '/users/view/' . $comment['Away']['id']);
        ?>
    </div>

<?php
echo $this->element('bbcode', array(
    'style' => 'width:958px; height:500px;',
    'destination' => '/comments/add/' . $comment['Game']['id'],
    'redirect' => '/games/view/' . $comment['Game']['id']
));
?>
