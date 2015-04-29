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

    echo $this->Html->css('style-5raRj');

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

            <?php
            $liveStreams = array(array(
                '_title' => 'KhamsTV',
                '_links' => array(
                    'self' => 'https://api.twitch.tv/kraken/streams/sp4zie',
                    'channel' => 'https://api.twitch.tv/kraken/channels/sp4zie'
                ),
                'stream' => array(
                    '_id' => (int) 14197013456,
                    'game' => 'League of Legends',
                    'viewers' => (int) 15638,
                    'created_at' => '2015-04-28T13:45:38Z',
                    'video_height' => (int) 720,
                    'average_fps' => (float) 59.9690618762,
                    '_links' => array(
                        'self' => 'http://api.twitch.tv/kraken/streams/sp4zie'
                    ),
                    'preview' => array(
                        'small' => 'http://static-cdn.jtvnw.net/previews-ttv/live_user_sp4zie-80x45.jpg',
                        'medium' => 'http://static-cdn.jtvnw.net/previews-ttv/live_user_sp4zie-320x180.jpg',
                        'large' => 'http://static-cdn.jtvnw.net/previews-ttv/live_user_sp4zie-640x360.jpg',
                        'template' => 'http://static-cdn.jtvnw.net/previews-ttv/live_user_sp4zie-{width}x{height}.jpg'
                    ),
                    'channel' => array(
                        '_links' => array(
                            'self' => 'https://api.twitch.tv/kraken/channels/sp4zie',
                            'follows' => 'https://api.twitch.tv/kraken/channels/sp4zie/follows',
                            'commercial' => 'https://api.twitch.tv/kraken/channels/sp4zie/commercial',
                            'stream_key' => 'https://api.twitch.tv/kraken/channels/sp4zie/stream_key',
                            'chat' => 'https://api.twitch.tv/kraken/chat/sp4zie',
                            'features' => 'https://api.twitch.tv/kraken/channels/sp4zie/features',
                            'subscriptions' => 'https://api.twitch.tv/kraken/channels/sp4zie/subscriptions',
                            'editors' => 'https://api.twitch.tv/kraken/channels/sp4zie/editors',
                            'videos' => 'https://api.twitch.tv/kraken/channels/sp4zie/videos',
                            'teams' => 'https://api.twitch.tv/kraken/channels/sp4zie/teams'
                        ),
                        'background' => null,
                        'banner' => 'http://static-cdn.jtvnw.net/jtv_user_pictures/sp4zie-channel_header_image-26e6e281bf3cab1e-640x125.jpeg',
                        'broadcaster_language' => 'en',
                        'display_name' => 'Sp4zie',
                        'game' => 'League of Legends',
                        'logo' => 'http://static-cdn.jtvnw.net/jtv_user_pictures/sp4zie-profile_image-94c7d70156561e7a-300x300.png',
                        'mature' => false,
                        'status' => '♥ Sp4zie ~ Stream-Cream - Bugged but beautiful <>',
                        'partner' => true,
                        'url' => 'http://www.twitch.tv/sp4zie',
                        'video_banner' => 'http://static-cdn.jtvnw.net/jtv_user_pictures/sp4zie-channel_offline_image-fa14f5814accf9c5-640x360.png',
                        '_id' => (int) 28565473,
                        'name' => 'sp4zie',
                        'created_at' => '2012-02-27T12:26:41Z',
                        'updated_at' => '2015-04-28T14:15:47Z',
                        'delay' => (int) 0,
                        'followers' => (int) 342772,
                        'profile_banner' => 'http://static-cdn.jtvnw.net/jtv_user_pictures/sp4zie-profile_banner-44884bde791809a4-480.png',
                        'profile_banner_background_color' => '#0c395e',
                        'views' => (int) 14505853,
                        'language' => 'en'
                    )
                )
            ));

            ?>

            <?php if (!empty($liveStreams)): ?>
                <?php foreach ($liveStreams as $liveStream): ?>
                    <div id="box" style="background: #303F4B url('/img/popcorn.png') no-repeat 15px center; padding-left: 60px;">
                        Live Stream: <?php echo $liveStream['_title'] ?> is live streaming <?php echo $this->Html->link($liveStream['stream']['channel']['status'], $liveStream['stream']['channel']['url'], array('target' => '_blank')) ?> right now! Tune in!
                    </div>
                <?php endforeach; ?>
            <?php endif; ?>

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
