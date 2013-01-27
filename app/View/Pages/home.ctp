<?php
	if($logged_in) $this->Html->script('infoboard', array('inline'=>false));
	else $this->Html->script('guestbook', array('inline'=>false));
	$this->Html->script('jCaret', array('inline'=>false));
	$this->Html->css('infoboard', null, array('inline'=>false));
?>

<?php if($tourney['status'] == 'group' || $tourney['status'] == 'playoff'): ?>
	<?php echo $this->element('scheduler'); ?>
<?php else: ?>
	<div id="boxFloat" style="width:500px; height:365px; background-color:#3C3E28">
		<h1>Crespo’s Worms Tournament 2012</h1>
		<h3 style="text-align:right">by Joschi, Kayz and Zemke</h3>
		<h4 style="font-weight:normal">Crespo’s Worms Tournament is a Worms Armageddon tournament organized since 2002. Once a year the best “wormers” come together to play in a friendly atmosphere and finally find their best.</h4><hr />
		<h4 style="font-weight:normal">The eleventh edition of the most prestigious Worms Armageddon tournament is waiting as best players from around the world get prepared to stride up to the top of the Worms olymp and challenge last year’s champion Dario for the throne of CWT.</h4>
		
		<?php if($tourney['status'] == 'archived'): ?>
			You can sign up for Crespo’s Worms Tournament 2012 by October the 1st.
		<?php elseif($tourney['status'] == 'pending'): ?>
			<?php
				echo $this->Html->link(
					'See who has applied for Crespo’s Worms Tournament 2012',
					'/applications/');
			?>
		<?php endif; ?>
	</div>
	<div id="box" style="margin-left:550px; width:388px; background-color:transparent; border:none; box-shadow:none">
		<?php echo $this->Html->image('cwt12logo.png') ?>
	</div>
<?php endif; ?>

<?php echo $this->element('news'); ?>

<div id="box" style="background-color:white; color:rgb(46,46,46); margin-top:40px; height: 210px">
	<div id="categories">		
		<?php if($logged_in): ?>
			<div class="category_item" id="all">All</div>
			<div class="category_item" id="shoutbox">Shoutbox</div>		
			<div class="category_item" id="pm">Private Messages</div>
			<div class="category_item" id="tourney">Tournament News</div>
		<?php endif; ?>
			<div class="category_item" id="guest">Guestbook</div>
		<?php if(@$current_user['admin']): ?>
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
		The Infoboard is a powerful tool that helps you connect with persons of interest. It comes with a couple of handy features that help you contact exactly those kind of persons privately: <br><br>

		<b>@[Nickname]</b> for sending a private message that only [Nickname] can see. Example: <i>@Zemke Hey, awesome website.</i><br>
		<b>@admins</b> for writing a private message that only admins (Joschi, Kayz and Zemke) can see. Example: <i>@admins Oh guys, could you please do a better job organizing this tournament?</i><br>
		<b>@[yourGroup]</b> for sending a private message that only members of your group can see. Example: <i>@b When can you guys play?</i><br><br>

		<b>It is possible to combine these commands.</b> Example: <i>Hey @admins I think @Zemke sucks and @khamski shares the opinion, because he doesn't play his games in group @f, could you please ban him? Group @a and @e played many more games. Do soemthing!</i><br><br>
		You don't necessarily need to write the commands at the beginning of your message. However, if anything has gone wrong, the website will inform you about it and give you an opportunity to correct your message. <b>Remember that admins can see all messages including private messages they weren't involved in.</b>
	</div>
</div>