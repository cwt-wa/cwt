<script type="text/javascript">
    $(document).ready(function () {
        $('#timeline').scrollLeft(165);

    });
</script>

<?php
$this->Html->css('profiles', null, array('inline' => false));
echo $this->element('timeline', array('user' => $user['User']['id']));
?>

<div id="boxFloat" style="width:500px; padding:0px; border:none; box-shadow:none; text-align:center;">
    <?php
    echo $this->Html->image('users/' . $photo, array(
        'style' => 'height:auto; max-width:500px; border:1px solid #C4C4C4; padding:0px; box-shadow:4px 4px 3px #C4C4C4;'
    ));
    ?>
</div>

<div id="trashbin">
    <div id="actions" style="display:none;">
        <div class="actions_item" onclick="action('games', '<?php echo $user['User']['id']; ?>')">
            Games
        </div>
        <div class="actions_item" onclick="action('comments', '<?php echo $user['User']['id']; ?>')">
            Comments
        </div>
        <div class="actions_item" onclick="action('groups', '<?php echo $user['User']['id']; ?>')">
            Group
        </div>

        <div id="games"></div>
        <div id="comments"></div>
        <div id="groups"></div>
    </div>

    <div id="box" style="margin-left:510px; background-color:#3E283E; font-size:larger; text-align:center;">
        <u><h1 style="font-size:30pt"><?php echo $user['User']['username'] ?></h1></u>

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
                            'escape' => false,
                            'target' => '_blank'
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
                            'escape' => false,
                            'target' => '_blank'
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
                            'escape' => false,
                            'target' => '_blank'
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
<div style="clear:both; height:10px"></div>
<div id="box" style="background-color:#2F2923; text-align:center;">
    <h2>Ratings and Bets</h2>
    <?php
        echo $this->element('traces', array('traces' => $traces));
    ?>
</div>
<div id="box" style="background-color:#2F2923; text-align:center;">
    <h2>Games</h2>
    <?php
        echo $this->element('games', array('games' => $games));
    ?>
</div>
