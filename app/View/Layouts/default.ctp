<?php echo $this->Html->docType('html4-trans'); ?>
<head>
    <?php echo $this->Html->charset(); ?>
    <title>
        <?php echo ('CWT - '); ?>
        <?php echo $title_for_layout; ?>
    </title>
    <?php
    echo $this->Html->meta('favicon.ico', '/favicon.ico', array('type' => 'icon'));
    echo $this->Html->meta('keywords', 'Crespo\'s Worms Tournament, Worms Armageddon, CWT, Normal No Noobs, NNN, Florian Zemke');
    echo $this->Html->meta('description', 'Crespo\'s Worms Tournament is a yearly hosted tournament of the strategy game "Worms Armageddon". It\'s considered the most prestigious of its kind and has money prizes involved.');

    echo $this->Html->css('style');

    echo $this->Html->script('jquery');
    echo $this->Html->script('gmt');
    echo $this->Html->script('user');

    echo $scripts_for_layout;
    ?>
	<?php if($_SERVER['HTTP_HOST'] == 'cwtsite.com'): ?>
		<script type="text/javascript">
		  var _gaq = _gaq || [];
		  _gaq.push(['_setAccount', 'UA-33673621-1']);
		  _gaq.push(['_trackPageview']);

		  (function() {
		    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
		    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
		    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
		  })();
		</script>
	<?php endif; ?>
	<script type="text/javascript">
		function donate() {
			$('#donate_form').submit();
		}
	</script>
</head>
<style type="text/css">
	.userbutton {
		text-align: center;
	}
</style>
<body>
<div id="wrapper">
    <div id="topbar">
    </div>
    <div id="container">
        <div id="time">
        	<script language="JavaScript">displayTime();</script> GMT
        </div>
        <div id="user">
        	<?php if($logged_in): ?>
		    <div id="userpanel">
	        	<?php if($current_user['up_menu'] == 'apply'): // Ready to apply. ?>
	        		<div id="menu_user">
	        			<?php
		        			echo $this->Html->link('<div id="up_apply" class="plain item_user">Apply for CWT</div>',
		        				'/rules/view/apply/',
		        				array('escape' => false));
		        		?>
	        		</div>
	        	<?php elseif($current_user['up_menu'] == 'report'): // Ready to report a game. ?>
					<div id="menu_user" style="width:120px !important;">
						<div id="up_report" class="item_user">
				    		Report a Game
				    	</div>
				    	<div id="report" class="open_item_user" style="width:350px; margin-right:-120px;">
				    	</div>
					</div>
	        	<?php endif; ?>
	        	<?php if($up_stream != false): ?>
		    		<div id="menu_user">
		    			<div id="up_stream" class="item_user">
				    		<?php echo $up_stream['maintainer']['title'] ?>
				    	</div>
				    	<div id="stream" class="open_item_user">
				    		<div class="gu_item">
				    			<?php echo $this->Html->link('Schedule Stream',
				    				'/streams/schedule/' . $up_stream['maintainer']['id']); ?>
				    		</div>
				    		<div class="gu_item">
				    			<?php echo $this->Html->link('View my Stream',
				    				'/streams/view/' . $up_stream['maintainer']['id']); ?>
				    		</div>
				    		<div class="gu_item">
				    			<?php echo $this->Html->link('Edit Description',
				    				'/streams/edit/' . $up_stream['maintainer']['id'] . '/description'); ?>
				    		</div>
				    		<div class="gu_item">
				    			<?php echo $this->Html->link('Edit Stream',
				    				'/streams/edit/' . $up_stream['maintainer']['id']); ?>
				    		</div>
				    		<div class="gu_item" id="changepw">
				    			<?php echo $this->Form->postLink('Delete Stream :(', '/streams/delete/' . $up_stream['maintainer']['id'], null, __('Are you sure you want to delete %s?', $up_stream['maintainer']['title'])); ?>
				    		</div>
				    	</div>
		    		</div>
		    	<?php endif; ?>
		    	<div id="menu_user">
		    		<div id="up_user" class="item_user">
			    		<?php echo $current_user['username'] ?>
			    	</div>
			    	<div id="generaluser" class="open_item_user">
			    		<div class="gu_item" id="editprofile">
			    			<?php echo $this->Html->link('Edit profile',
			    				array('controller'=>'profiles', 'action'=>'edit')); ?>
			    		</div>
			    		<div class="gu_item">
			    			<?php echo $this->Html->link('View profile',
			    				array('controller'=>'users', 'action'=>'view', $current_user['id'])); ?>
			    		</div>
			    		<div class="gu_item">
			    			<?php echo $this->Html->link('Change photo',
			    				array('controller'=>'profiles', 'action'=>'photo')); ?>
			    		</div>
			    		<div class="gu_item" id="changepw">
			    			<?php echo $this->Html->link('Change password',
			    				array('controller'=>'users', 'action'=>'password')); ?>
			    		</div>
			    		<?php if($current_user['admin']): ?>
				    		<div class="gu_item">
				    			<?php echo $this->Html->link('Admin Panel',
				    				'/pages/admin'); ?>
				    		</div>
				    	<?php endif; ?>
			    		<div class="gu_item">
			    			<?php echo $this->Html->link('Log out',
			    				'/users/logout'); ?>
			    		</div>
			    	</div>
		    	</div>
		    </div>
		    <?php else:
		            echo $this->Form->create('User', array(
		            	'action' => 'login',
		            	'style' => 'display:inline',
					    'inputDefaults' => array(
					        'label' => false,
					        'div' => false,
					        'error' => false
					    )
					));
		            echo $this->Form->input('username', array('div'=>false, 'label'=>false, 'class'=>'userbutton'));
		            echo '&nbsp';
		            echo $this->Form->input('password', array('div'=>false, 'label'=>false, 'class'=>'userbutton'));
		            echo '&nbsp';
		            echo $this->Form->end(array('label'=>'Log in', 'div'=>false, 'style'=>'width:80px'));
		            echo '&nbsp|&nbsp';
		            echo $this->Html->link($this->Form->button('Register', array('style'=>'display:inline; width:80px')),
		            	'/users/add', array('escape' => false));
	        endif; ?>
        </div>

        <div id="banner">
        	<?php
        		echo $this->Html->link($this->Html->image('logo.png'),
        			'/',
        			array('escape'=>false));
            ?>
        </div>
        <div id="menu">
        	<?php echo $this->Html->link('<div class="menu_item">Players</div>', '/users', array('escape'=>false)); ?>
        	<?php echo $this->Html->link('<div class="menu_item">Groups</div>', '/groups', array('escape'=>false)); ?>
        	<?php echo $this->Html->link('<div class="menu_item">Playoff</div>', '/playoffs', array('escape'=>false)); ?>
        	<?php echo $this->Html->link('<div class="menu_item">Archive</div>', '/archive', array('escape'=>false)); ?>
            <?php echo $this->Html->link('<div class="menu_item">Forum</div>', 'http://www.normalnonoobs.com/forum/viewforum.php?f=24', array('target'=>'_blank', 'escape'=>false)); ?>
        	<?php echo $this->Html->link('<div class="popcorn_item">' . $this->Html->image('popcorn.png') . '</div>', '/streams', array('escape'=>false)); ?>
        </div>

        <div id="content">
	        <?php echo $this->Session->flash(); ?>
	        <?php echo $this->Session->flash('auth'); ?>

	    	<?php echo $content_for_layout; ?>
        </div>
    </div>
    <div id="push">
   	</div>
</div>
	<div id="footer">
		<div id="copyright">
            <?php
            $string = '<b>Crespo&apos;s Worms Tournament</b>';

            if ($currentTournament == null) {
                echo $string;
            } else {
                $string .= ' <b>' . $currentTournament['Tournament']['year'] . '</b> by ';

                $moderators = array();
                foreach ($currentTournament['Moderator'] as $moderator) {
                    $moderators[] = $moderator['username'];
                }

                $string .= $this->Text->toList($moderators);
                echo $string;
            }

            ?>
   		</div>
   		<div id="pages">
   			<?php echo $this->Html->link('Contact', 'mailto:support@cwtsite.com', array('class' => 'plainer')) ?>
   			&bull;
   			<?php echo $this->Html->link('Scheme', '/tournaments/download/scheme', array('class' => 'plainer')) ?>
   			&bull;
            <?php echo $this->Html->link('Facebook', 'http://facebook.com/CresposWormsTournament', array('target' => '_blank', 'class' => 'plainer')) ?>
   			&bull;
            <?php echo $this->Html->link('WKB', 'http://worms2d.info/Crespo\'s_Worms_Tournament', array('target' => '_blank', 'class' => 'plainer')) ?>
            &bull;
   			<?php echo $this->Html->link('NNN', 'http://www.normalnonoobs.com', array('target' => '_blank', 'class' => 'plainer')) ?>
   			&bull;
   			<?php echo $this->Html->link('TUS', 'http://www.tus-wa.com', array('target' => '_blank', 'class' => 'plainer')) ?>
            &bull;
            <?php echo $this->Html->link('NTM', 'http://www.ntm.normalnonoobs.com', array('target' => '_blank', 'class' => 'plainer')) ?>
   		</div>
	</div>
<?php echo $this->Js->writeBuffer(); // Write cached scripts ?>
<script type="text/javascript">
    var infolinks_pid = 1858339;
    var infolinks_wsid = 0;
</script>
<script type="text/javascript" src="http://resources.infolinks.com/js/infolinks_main.js"></script>
</body>
</html>
