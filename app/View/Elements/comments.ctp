<?php
if (!isset($comments)) {
    $comments = $this->requestAction('/comments/view/' . $gameId);
}

// Keep in mind that Elements/comments and Comments/view should always be equal.
?>

<?php foreach ($comments as $comment): ?>
    <div id="box" style="background-color:#2F2B23; padding:0px">
        <div style="white-space:nowrap; float:left; width:160px; padding:10px 10px 10px 10px;">
            <?php if ($logged_in && $comment['User']['username'] == $current_user['username']): ?>
                <div onClick="editComment('<?php echo $comment['Comment']['id'] ?>')"
                     style="position:relative; width:20px; font-style:italic; font-size:8pt; text-align:center; cursor:pointer; background-color:black; margin-bottom:-18px; margin-top:-10px; margin-left:<?php echo !isset($gameId) ? '230' : '150' ?>px;">
                    <i>Edit</i>
                </div><br>
            <?php endif; ?>
            <?php if(isset($gameId)): ?>
                <?php
                echo $this->Html->link($comment['User']['username'],
                        '/users/view/' . $comment['User']['id']);
                ?>
            <?php else: ?>
                Game <?php
                echo $this->Html->link('#' . $comment['Game']['id'],
                    '/games/view/' . $comment['Game']['id']);
                ?><br>
                <?php
                echo $this->Html->link($comment['Game']['Home']['username'],
                    '/users/view/' . $comment['Game']['Home']['id']);
                ?>
                <?php
                    echo $comment['Game']['score_h'] . '-' . $comment['Game']['score_a']
                ?>
                <?php
                echo $this->Html->link($comment['Game']['Away']['username'],
                    '/users/view/' . $comment['Game']['Away']['id']);
                ?>
            <?php endif; ?>
            <br>
            <span title="<?php echo $this->Time->format('M j, Y \a\t H:i', $comment['Comment']['created']); ?>">
                <?php
                echo $this->Time->timeAgoInWords($comment['Comment']['created'], array(
                    'format' => 'M j, Y \a\t H:i',
                    'end' => '+12 hour',
                    'accuracy' => array('hour' => 'hour')
                ));
                ?>
            </span>
        </div>
        <div style="padding:10px 10px 10px 10px; margin-left:<?php echo !isset($gameId) ? '260' : '180' ?>px; border-left:1px solid black; word-wrap:break-word;">
            <?php echo nl2br($this->Bbcode->parse($comment['Comment']['message'])); ?><br><br>
            <?php if (!isset($gameId)) echo '<br>'; ?>
            <?php if ($comment['Comment']['modified'] != '0000-00-00 00:00:00'): ?>
                <div style="margin-top:5px; text-align:right; font-style:italic;">
                    Last modification
                    <span title="<?php echo $this->Time->format('M j, Y \a\t H:i', $comment['Comment']['modified']); ?>">
                        <?php
                        echo $this->Time->timeAgoInWords($comment['Comment']['modified'], array(
                            'format' => 'M j, Y \a\t H:i',
                            'end' => '+12 hour',
                            'accuracy' => array('hour' => 'hour')
                        ));
                        ?>
                    </span>
                </div>
            <?php endif; ?>
        </div>
    </div>
<?php endforeach; ?>
