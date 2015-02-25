<div id="box" style="background-color: #<?php echo $stream['Stream']['color'] ?>; text-align: center; margin-bottom: 10px;">
    <h1><?php echo $video['title'] ?></h1>
</div>
<object bgcolor="#<?php echo $stream['Stream']['color'] ?>" data="http://www.twitch.tv/swflibs/TwitchPlayer.swf" height="582" id="clip_embed_player_flash"
        type="application/x-shockwave-flash" width="980">
    <param name="movie" value="http://www.twitch.tv/swflibs/TwitchPlayer.swf"/>
    <param name="allowScriptAccess" value="always"/>
    <param name="allowNetworking" value="all"/>
    <param name="allowFullScreen" value="true"/>
    <param name="flashvars"
           value="channel=<?php echo $provider ?>&auto_play=false&start_volume=50&videoId=<?php echo $video['_id'] ?>&device_id=<?php echo $video['broadcast_id'] ?>"/>
</object>
<div id="box" style="background-color: #<?php echo $stream['Stream']['color'] ?>; text-align: center;">
    Recorded on <b><?php echo $this->Time->format($video['recorded_at'], '%A, %B %d, %Y, %H:%M:%S') ?></b> by <b><?php echo $stream['Stream']['title'] ?></b> and viewed by <b><?php echo $video['views'] ?></b> people. Thanks!
</div>
<?php if (!empty($video['description'])): ?>
    <div id="box" style="background-color: #<?php echo $stream['Stream']['color'] ?>;">
        <?php echo $video['description'] ?>
    </div>
<?php endif; ?>
