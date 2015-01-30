<?php
$comments = $this->requestAction('/comments/view/' . $gameId);
//debug($comments);

// Keep in mind that Elements/comments and Comments/view should always be equal.
?>

<?php foreach ($comments as $comment): ?>
    <div id="box" style="background-color:#2F2B23; padding:0px">
        <div style="white-space:nowrap; float:left; width:160px; padding:10px 10px 10px 10px;">
            <?php if ($logged_in && $comment['User']['username'] == $current_user['username']): ?>
                <div onClick="editComment('<?php echo $comment['Comment']['id'] ?>')"
                     style="position:relative; width:20px; font-style:italic; font-size:8pt; text-align:center; cursor:pointer; background-color:black; margin-bottom:-18px; margin-top:-10px; margin-left:150px;">
                    <i>Edit</i>
                </div><br>
            <?php endif; ?>
            <?php
            echo $this->Html->link($comment['User']['username'],
                    '/users/view/' . $comment['User']['id'],
                    array('target' => '_blank')) . '<br>';

            echo $this->Time->timeAgoInWords($comment['Comment']['created'], array(
                'format' => 'M j, Y \a\t H:i',
                'end' => '+1 day',
                'accuracy' => array('hour' => 'hour')
            ));
            ?>
        </div>
        <div style="padding:10px 10px 10px 10px; margin-left:180px; border-left:1px solid black; word-wrap:break-word;">
            <?php echo nl2br($this->Bbcode->parse($comment['Comment']['message'])); ?><br><br>
            <?php if ($comment['Comment']['modified'] != '0000-00-00 00:00:00'): ?>
                <div style="margin-top:5px; text-align:right; font-style:italic;">
                    Last modification
                    <?php
                    echo $this->Time->timeAgoInWords($comment['Comment']['modified'], array(
                        'format' => 'M j, Y \a\t H:i',
                        'end' => '+1 day',
                        'accuracy' => array('hour' => 'hour')
                    ));
                    ?>
                </div>
            <?php endif; ?>
        </div>
    </div>
<?php endforeach; ?>
