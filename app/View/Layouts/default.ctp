<?php echo $this->Html->docType('html4-trans'); ?>
<head>
    <?php echo $this->Html->charset(); ?>
    <title>
        <?php
            if (!empty($title_for_layout)) {
                echo $title_for_layout;
            } else {
                echo 'Crespo’s Worms Tournament';
            }
        ?>
    </title>
    <link rel="shortcut icon" href="/img/favicons/favicon.ico" type="image/x-icon" />
    <link rel="apple-touch-icon" href="/img/favicons/apple-touch-icon.png" />
    <link rel="apple-touch-icon" sizes="57x57" href="/img/favicons/apple-touch-icon-57x57.png" />
    <link rel="apple-touch-icon" sizes="72x72" href="/img/favicons/apple-touch-icon-72x72.png" />
    <link rel="apple-touch-icon" sizes="76x76" href="/img/favicons/apple-touch-icon-76x76.png" />
    <link rel="apple-touch-icon" sizes="114x114" href="/img/favicons/apple-touch-icon-114x114.png" />
    <link rel="apple-touch-icon" sizes="120x120" href="/img/favicons/apple-touch-icon-120x120.png" />
    <link rel="apple-touch-icon" sizes="144x144" href="/img/favicons/apple-touch-icon-144x144.png" />
    <link rel="apple-touch-icon" sizes="152x152" href="/img/favicons/apple-touch-icon-152x152.png" />
    <?php
    echo $this->Html->meta('keywords', 'Crespo\'s Worms Tournament, Worms Armageddon, CWT, Normal No Noobs, NNN, Florian Zemke');
    echo $this->Html->meta('description', 'Crespo\'s Worms Tournament is a yearly hosted tournament of the strategy game "Worms Armageddon". It\'s considered the most prestigious of its kind and has money prizes involved.');

    echo $this->Html->css('style');

    echo $this->Html->script('jquery');
    echo $this->Html->script('gmt');
    echo $this->Html->script('user');

    echo $scripts_for_layout;
    ?>
    <?php if ($_SERVER['HTTP_HOST'] == 'cwtsite.com'): ?>
        <script>
            (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
                (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
                m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
            })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

            ga('create', 'UA-33673621-2', 'auto');
            ga('send', 'pageview');

        </script>
    <?php endif; ?>
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
    <div id="menubacker">
    </div>
    <div id="container">
        <div id="time">
            <script language="JavaScript">displayTime();</script>
            GMT
        </div>
        <div id="user">
            <?php if ($logged_in): ?>
                <div id="userpanel">
                    <?php if ($current_user['up_menu'] == 'apply'): // Ready to apply. ?>
                        <div id="menu_user">
                            <?php
                            echo $this->Html->link('<div id="up_apply" class="plain item_user">Apply for CWT</div>',
                                '/rules/view/apply/',
                                array('escape' => false));
                            ?>
                        </div>
                    <?php elseif ($current_user['up_menu'] == 'report'): // Ready to report a game. ?>
                        <div id="menu_user" style="width:120px !important;">
                            <div id="up_report" class="item_user">
                                Report a Game
                            </div>
                            <div id="report" class="open_item_user" style="width:350px; margin-right:-120px;">
                            </div>
                        </div>
                    <?php endif; ?>
                    <?php if ($up_stream != false): ?>
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
                                    '/profiles/edit'); ?>
                            </div>
                            <div class="gu_item">
                                <?php echo $this->Html->link('View profile',
                                    '/users/view/' . $current_user['id']); ?>
                            </div>
                            <div class="gu_item">
                                <?php echo $this->Html->link('Change photo',
                                    '/profiles/photo'); ?>
                            </div>
                            <div class="gu_item" id="changepw">
                                <?php echo $this->Html->link('Change password',
                                    '/users/password'); ?>
                            </div>
                            <?php if ($current_user['admin']): ?>
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
            <?php
            else:
                echo $this->Form->create('User', array(
                    'action' => 'login',
                    'style' => 'display:inline',
                    'inputDefaults' => array(
                        'label' => false,
                        'div' => false,
                        'error' => false
                    )
                ));
                echo $this->Form->input('username', array('div' => false, 'label' => false, 'class' => 'userbutton'));
                echo '&nbsp';
                echo $this->Form->input('password', array('div' => false, 'label' => false, 'class' => 'userbutton'));
                echo '&nbsp';
                echo $this->Form->end(array('label' => 'Log in', 'div' => false, 'style' => 'width:80px'));
                echo '&nbsp|&nbsp';
                echo $this->Html->link($this->Form->button('Register', array('style' => 'display:inline; width:80px')),
                    '/users/add', array('escape' => false));
            endif; ?>
        </div>

        <div id="menu">
            <?php echo $this->Html->link('<img class="icon" src="/img/icon.png" />', '/', array('escape' => false)); ?>
            <?php //echo $this->Html->link('<div class="menu_item">Home</div>', '/', array('escape' => false)); ?>
            <?php echo $this->Html->link('<div class="menu_item">Groups</div>', '/groups', array('escape' => false)); ?>
            <?php echo $this->Html->link('<div class="menu_item">Playoffs</div>', '/playoffs', array('escape' => false)); ?>
            <?php echo $this->Html->link('<div class="menu_item">Streams</div>', '/streams', array('escape' => false)); ?>
            <?php echo $this->Html->link('<div class="menu_item">Users</div>', '/users', array('escape' => false)); ?>
            <?php echo $this->Html->link('<div class="menu_item">Games</div>', '/games', array('escape' => false)); ?>
            <?php echo $this->Html->link('<div class="menu_item">Ratings/Bets</div>', '/ratingsbets', array('escape' => false)); ?>
            <?php echo $this->Html->link('<div class="menu_item">Ranking</div>', '/ranking', array('escape' => false)); ?>
            <?php echo $this->Html->link('<div class="menu_item">Archive</div>', '/archive', array('escape' => false)); ?>
            <?php echo $this->Html->link('<div class="menu_item">More...</div>', '/more', array('escape' => false)); ?>
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
        $string = 'Crespo’s Worms Tournament';

        if ($currentTournament == null) {
            echo $string;
        } else {
            $string .= ' ' . $currentTournament['Tournament']['year'] . ' by ';

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
        <?php echo $this->Html->link('Facebook', 'http://facebook.com/CresposWormsTournament', array('target' => '_blank', 'class' => 'plainer')) ?>
        &bull;
        <?php echo $this->Html->link('Twitter', 'http://twitter.com/cwtwa', array('target' => '_blank', 'class' => 'plainer')) ?>
        &bull;
        <?php echo $this->Html->link('WKB', 'http://worms2d.info/Crespo\'s_Worms_Tournament', array('target' => '_blank', 'class' => 'plainer')) ?>
        &bull;
        <?php echo $this->Html->link('NNN', 'http://www.normalnonoobs.com', array('target' => '_blank', 'class' => 'plainer')) ?>
        &bull;
        <?php echo $this->Html->link('TUS', 'http://www.tus-wa.com', array('target' => '_blank', 'class' => 'plainer')) ?>
    </div>
</div>
<?php echo $this->Js->writeBuffer(); // Write cached scripts ?>
</body>
</html>
