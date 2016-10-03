<?php echo $this->Html->script('timeline', array('inline' => false)); ?>
<?php
$this->Html->css('profiles', null, array('inline' => false));
echo $this->element('timeline', array('user' => $user['User']['id']));
?>
<div id="boxFloat" class="action_item">
    <?php
    echo $this->Html->link('<span></span>', '/games?user_id=' . $user['User']['id'], array('escape' => false));
    ?>
    Games
</div>
<div id="boxFloat" class="action_item" style="margin-left:10px; margin-top:0;">
    <?php
    echo $this->Html->link('<span></span>', '/comments?user_id=' . $user['User']['id'], array('escape' => false));
    ?>
    Comments
</div>
<div id="box" class="action_item" style="margin-left:10px; margin-top:0;">
    <?php
    echo $this->Html->link('<span></span>', '/traces?user_id=' . $user['User']['id'], array('escape' => false));
    ?>
    Ratings and Bets
</div>
<div>
<div id="boxFloat" style="width:500px; padding:0px; border:none; box-shadow:none; text-align:center;">
    <?php
    echo $this->Html->image('users/' . $photo, array(
        'style' => 'height:auto; max-width:500px; padding:0px;'
    ));
    ?>
</div>
    <div id="box" style="margin-left:510px; background-color:#3E283E; text-align:center; padding:5px;">
        <h1 style="font-size:30pt"><?php echo $user['User']['username'] ?></h1>
    </div>
    <div id="box" style="margin-left:510px; background-color:#3E283E; font-size:larger; text-align:center;">
        <?php if ($user['Profile']['country'] != 'unknown'): ?>
            <p>Country:<br><b><?php echo $user['Profile']['country'] ?></b></p>
        <?php endif; ?>

        <?php if ($user['Profile']['clan']): ?>
            <p>Clan:<br><b><?php echo $user['Profile']['clan'] ?></b></p>
        <?php endif; ?>

        <?php if ($user['Profile']['skype']): ?>
            <p>Skype:<br><b><?php echo $user['Profile']['skype'] ?></b></p>
        <?php endif; ?>

        <?php if ($user['Profile']['icq']): ?>
            <p>ICQ:<br><b><?php echo $user['Profile']['icq'] ?></b></p>
        <?php endif; ?>

        <?php if ($user['Profile']['email'] && !$user['Profile']['hideEmail']): ?>
            <p>Email:<br><b><?php echo $this->Text->autoLinkEmails($user['Profile']['email']) ?></b></p>
        <?php endif; ?>


        <?php if ($user['Profile']['facebook'] || $user['Profile']['googlep'] || $user['Profile']['twitter']): ?>
            <p>
                Social:<br>
                <?php if ($user['Profile']['facebook']): ?>
                    <?php
                    echo $this->Html->link(
                        $this->Html->image('contact/facebook.png', array(
                            'height' => 14,
                            'width' => 14
                        )),
                        $user['Profile']['facebook'], array(
                            'alt' => $user['Profile']['facebook'],
                            'title' => $user['Profile']['facebook'],
                            'escape' => false
                        )
                    )
                    ?>
                <?php endif; ?>
                <?php if ($user['Profile']['googlep']): ?>
                    <?php
                    echo $this->Html->link(
                        $this->Html->image('contact/googlep.png', array(
                            'height' => 14,
                            'width' => 14
                        )),
                        $user['Profile']['googlep'], array(
                            'alt' => $user['Profile']['googlep'],
                            'title' => $user['Profile']['googlep'],
                            'escape' => false
                        )
                    )
                    ?>
                <?php endif; ?>
                <?php if ($user['Profile']['twitter']): ?>
                    <?php
                    echo $this->Html->link(
                        $this->Html->image('contact/twitter.png', array(
                            'height' => 14,
                            'width' => 14
                        )),
                        $user['Profile']['twitter'], array(
                            'alt' => $user['Profile']['twitter'],
                            'title' => $user['Profile']['twitter'],
                            'escape' => false
                        )
                    )
                    ?>
                <?php endif; ?>
            </p>
        <?php endif; ?>
    </div>
    <?php if ($user['Profile']['about'] != ''): ?>
        <div id="box" style="margin-left:510px; background-color:#33283E;">
            “<?php echo nl2br($user['Profile']['about']) ?>”
        </div>
    <?php endif; ?>
</div>
