<?php
echo $this->Html->script('bbcode', array('inline' => false));
echo $this->Html->css('bbcode', null, array('inline' => false));
//debug($game);
?>

    <div id="box" style="background-color:#3F2828; text-align:center;">
        <h1>Edit your Comment</h1>
		Submit an empty modification to delete the comment entirely.
    </div>

    <div id="box" style="background-color:#1A2427; text-align:center; font-size:16pt;">
        <font color="lightgray"><?php echo $game['stage'] ?>:</font>
        <?php
        echo $this->Html->link($game['Home']['username'],
            '/users/view/' . $game['Home']['id']);
        ?>
        <?php echo $game['Game']['score_h']; ?>-<?php echo $game['Game']['score_a']; ?>
        <?php
        echo $this->Html->link($game['Away']['username'],
            '/users/view/' . $game['Away']['id']);
        ?>
    </div>

<?php
echo $this->element('bbcode', array(
    'style' => 'width:958px; height:500px;',
    'destination' => '/comments/edit/' . $comment['Comment']['id'],
    'redirect' => '/games/view/' . $comment['Game']['id'],
    'value' => $comment['Comment']['message']
));
?>
