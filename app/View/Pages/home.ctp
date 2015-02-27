<?php
if ($logged_in) {
    $this->Html->script('infoboard', array('inline' => false));
} else {
    $this->Html->script('guestbook', array('inline' => false));
}

$this->Html->script('jCaret', array('inline' => false));
$this->Html->css('infoboard-v9p3g', null, array('inline' => false));
?>

<?php if ($currentTournament['Tournament']['status'] == Tournament::GROUP
    || $currentTournament['Tournament']['status'] == Tournament::PLAYOFF
): ?>
    <?php echo $this->element('scheduler'); ?>
<?php endif; ?>

<?php echo $this->element('news'); ?>


<?php if ($logged_in): ?>
    <div id="box" class="infoboard">
        <div id="categories">
            <div class="category_item" id="all">All</div>
            <div class="category_item" id="shoutbox">Shoutbox</div>
            <div class="category_item" id="pm">Private Messages</div>
            <div class="category_item" id="tourney">Tournament News</div>
            <?php if (@$current_user['admin']): ?>
                <div class="admin_item" id="admin">Admin</div>
            <?php else: ?>
                <div class="admin_item" id="admin" style="visibility:hidden">Admin</div>
            <?php endif; ?>
            <div class="help_item">Help</div>
        </div>

        <?php
        echo $this->Form->input('message', array(
            'div' => false,
            'label' => false,
            'id' => 'message',
            'maxlength' => 500
        ));
        ?>

        <div id="board"></div>

        <div id="nick_suggest"></div>

        <div id="hidden"></div>

        <a href="#infobard"></a>

        <div id="help_text" style="display:none;">
            The Infoboard is a powerful tool that helps you connect with persons of interest. It comes with a couple of
            handy features that help you contact exactly those kind of persons privately: <br><br>

            <b>@[Nickname]</b> for sending a private message that only [Nickname] can see. Example: <i>@Zemke Hey,
                awesome website.</i><br>
            <b>@admins</b> for writing a private message that only admins (Joschi, Kayz and Zemke) can see. Example: <i>@admins
                Oh guys, could you please do a better job organizing this tournament?</i><br>
            <b>@[yourGroup]</b> for sending a private message that only members of your group can see. Example: <i>@b
                When can you guys play?</i><br><br>

            <b>It is possible to combine these commands.</b> Example: <i>Hey @admins I think @Zemke sucks and @khamski
                shares the opinion, because he doesn't play his games in group @f, could you please ban him? Group @a
                and @e played many more games. Do soemthing!</i><br><br>
            You don't necessarily need to write the commands at the beginning of your message. However, if anything has
            gone wrong, the website will inform you about it and give you an opportunity to correct your message. <b>Remember
                that admins can see all messages including private messages they weren't involved in.</b>
        </div>
    </div>
<?php else: ?>
    <div id="box" style="background-color:white; color:rgb(46,46,46); margin-top:80px; text-align: center;">
        Hey, the guest book has been discontinued. Please log in to use the Infoboard.
    </div>
<?php endif; ?>
