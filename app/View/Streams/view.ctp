<script type="text/javascript">
    $(document).ready(function() {
        var refreshStatus = function() {
            var response = $("#status").load(
                "/streams/view/<?php echo $stream['Stream']['id'] ?> #status"                                
            );
                
            console.log(response.html());
            
//            $.ajax({
//                url: "/streams/view/<?php echo $stream['Stream']['id'] ?>?"+Math.floor((Math.random()*100)+1),
//                type: "GET",
//                cache: false,
//                success: function(response) {
//                    response = $("body").html();
//                    var status = $(response).find("#box.status").html();
//                    
//                    console.log(status);
//                    
//                    $("#box.status").html(status);
//                }
//            });
        }
        
        setInterval(refreshStatus, 5000);
    });
</script>
<div id="status">
        <div id="box" style="font-size:24pt; text-align:center; font-weight:bold; background-color:#<?php echo $stream['Stream']['color'] ?>;">
        <?php if($stream['Stream']['online']): ?>
            <font color="green">Online:</font>
        <?php else: ?>
            <font color="red">Offline:</font>
        <?php endif; ?>
        <?php echo $stream['Stream']['title']; ?>
    </div>
    <?php if($stream['Stream']['description']): ?>
        <div id="box" style="background-color:#<?php echo $stream['Stream']['color'] ?>; font-size:14pt;">
            <?php echo $this->Bbcode->parse($stream['Stream']['description']); ?>
        </div>
    <?php endif; ?>
    <?php if($stream['Stream']['online']): ?>
        <div id="box" style="background-color:#<?php echo $stream['Stream']['color'] ?>; text-align:center; font-size:16pt;">
            <font color="lightgray"><?php echo $stream['Stream']['streaming']['stage'] ?>:</font>
            <?php 
                echo $this->Html->link($stream['Stream']['streaming']['home_id']['username'],
                    '/users/view/' . $stream['Stream']['streaming']['home_id']['id'],
                    array('style' => 'color:white;')
                ); 
            ?>
             vs.
             <?php 
                echo $this->Html->link($stream['Stream']['streaming']['away_id']['username'],
                    '/users/view/' . $stream['Stream']['streaming']['away_id']['id'],
                    array('style' => 'color:white;')
                ); 
             ?>	
        </div>
    <?php endif; ?>
</div>
<div style="white-space: nowrap">
    <div id="boxFloat" style="background-color:#<?php echo $stream['Stream']['color'] ?>; font-size:14pt; margin-top:10px; padding:5px 20px; width:300px; text-align:center;">
	<?php 
		echo $this->Html->link(
			'Past Videos',
			'http://twitch.tv/' . $stream['Stream']['provider'] . '/videos',
			array(
				'target' => '_blank'
			));
	?>
    </div>
    <div id="boxFloat" style="background-color:#<?php echo $stream['Stream']['color'] ?>; font-size:14pt; width:350px; text-align:center; margin-top:10px; margin-left:10px; padding:5px 20px; width:585px;">
        Streamed by <?php echo $this->Html->link($stream['User']['username'], '/users/view/' . $stream['User']['id']); ?>
    </div>
</div>
<div>
	<div id="boxFloat" style="margin:10px auto; padding:0px; text-align:center; display:inline-block;">
		<object type="application/x-shockwave-flash" height="500" width="620" id="live_embed_player_flash" data="http://www.twitch.tv/widgets/live_embed_player.swf?channel=<?php echo $stream['Stream']['provider'] ?>" bgcolor="#000000">
			<param name="allowFullScreen" value="true" />
			<param name="allowScriptAccess" value="always" /><param name="allowNetworking" value="all" />
			<param name="movie" value="http://www.twitch.tv/widgets/live_embed_player.swf" />
			<param name="flashvars" value="hostname=www.twitch.tv&channel=<?php echo $stream['Stream']['provider'] ?>&auto_play=true&start_volume=25" />
		</object>
	</div>
	<div id="box" style="margin:10px auto; padding:0px; text-align:left; display:inline-block; margin-left:5px;">
		<iframe frameborder="0" scrolling="no" id="chat_embed" src="http://twitch.tv/chat/embed?channel=<?php echo $stream['Stream']['provider'] ?>&amp;popout_chat=true" height="500" width="350"></iframe>
	</div>
	<div id="box" style="background-color:#<?php echo $stream['Stream']['color'] ?>; text-align:left; display:inline-block; float:right; margin-top:0px; padding:5px; width:100px;">
		&uarr; Click on <b>Chat</b>
	</div>
</div>