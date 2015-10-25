<?php echo $this->Html->css('groups', null, array('inline' => false)) ?>
<div id="box"
     style="background-color: #<?php echo $stream['Stream']['color'] ?>; text-align: center; margin-bottom: 10px;">
    <h1><?php echo $video['title'] ?></h1>
</div>
<!--<object bgcolor="#--><?php //echo $stream['Stream']['color'] ?><!--" data="http://www.twitch.tv/swflibs/TwitchPlayer.swf"-->
<!--        height="582" id="clip_embed_player_flash"-->
<!--        type="application/x-shockwave-flash" width="980">-->
<!--    <param name="movie" value="http://www.twitch.tv/swflibs/TwitchPlayer.swf"/>-->
<!--    <param name="allowScriptAccess" value="always"/>-->
<!--    <param name="allowNetworking" value="all"/>-->
<!--    <param name="allowFullScreen" value="true"/>-->
<!--    <param name="flashvars"-->
<!--           value="channel=--><?php //echo $provider ?><!--&auto_play=false&start_volume=50&videoId=--><?php //echo $video['_id'] ?><!--&device_id=--><?php //echo $video['broadcast_id'] ?><!--"/>-->
<!--</object>-->
<div id="box" style="background-color: #<?php echo $stream['Stream']['color'] ?>; text-align: center;">
    Recorded on <b><?php echo $this->Time->format($video['recorded_at'], '%A, %B %d, %Y, %H:%M:%S') ?></b> by
    <b><?php echo $stream['Stream']['title'] ?></b> and viewed by <b><?php echo $video['views'] ?></b> people. Thanks!
</div>
<?php if (!empty($video['description'])): ?>
    <div id="box" style="background-color: #<?php echo $stream['Stream']['color'] ?>;">
        <?php echo $video['description'] ?>
    </div>
<?php endif; ?>
<div id="box" style="background-color: #<?php echo $stream['Stream']['color'] ?>;">
    <?php if (empty($gamesForStream)): ?>
        <i>
            Unfortunately no game for this stream could have been found in the CWT database.<br/>
            Please bare in mind that this doesn't necessarily mean that there is none in the database,
            the algorithm simply failed to find one, sorry.
        </i>
    <?php else: ?>
        <table border="0" align="center" width="425">
            <?php foreach ($gamesForStream as $game): ?>
                <tr>
                    <td class="bottomBorder" width="140" align="right">
                        <?php
                        echo $this->Html->link($game['Home']['username'], '/users/view/' . $game['Home']['id'],
                            array(
                                'style' => 'color:white'
                            ));
                        ?>
                    </td>
                    <td class="bottomBorder" width="30" align="center">
                        <?php echo $game['Game']['score_h'] ?>-<?php echo $game['Game']['score_a'] ?>
                    </td>
                    <td class="bottomBorder" width="140" align="left">
                        <?php
                        echo $this->Html->link($game['Away']['username'], '/users/view/' . $game['Away']['id'],
                            array(
                                'style' => 'color:white'
                            ));
                        ?>
                    </td>
                    <td class="bottomBorder" width="55" align="center">
                        <?php if (!$game['Game']['techwin']): ?>
                            <div style="margin-bottom: 15px;">
                                <div
                                    title="<?php echo $game['Rating'][0]['likes'] ?> Likes, <?php echo $game['Rating'][0]['dislikes'] ?>  Dislikes"
                                    id="ratings">
                                    <div class="ratingsBar"
                                         style="width:<?php echo $game['Rating'][0]['p']['likes'] ?>%; background-color:green;"></div>
                                    <div class="ratingsBar"
                                         style="width:<?php echo $game['Rating'][0]['p']['dislikes'] ?>%; background-color:red;"></div>
                                </div>
                                <div
                                    title="<?php echo $game['Rating'][0]['lightside'] ?> Lightside, <?php echo $game['Rating'][0]['darkside'] ?>  Darkside"
                                    id="ratings">
                                    <div class="ratingsBar"
                                         style="width:<?php echo $game['Rating'][0]['p']['lightside'] ?>%; background-color:white;"></div>
                                    <div class="ratingsBar"
                                         style="width:<?php echo $game['Rating'][0]['p']['darkside'] ?>%; background-color:black;"></div>
                                </div>
                            </div>
                        <?php else: ?>
                            <span style="font-size: 8pt;">Tech. Win</span>
                        <?php endif; ?>
                    </td>
                    <td class="bottomBorder" width="24" align="center"
                        style="background:url('/img/comment.png') no-repeat center center;">
                        <?php echo count($game['Comment']) ?>
                    </td>
                    <td class="bottomBorder" width="24" align="center"
                        style="background:url(/img/wa.png) no-repeat center center;">
                        <div title="<?php echo $game['Game']['downloads'] ?> times downloaded">
                            <?php
                            echo $this->Html->link($game['Game']['downloads'],
                                '/games/download/' . $game['Game']['id'],
                                array(
                                    'style' => 'color:white; font-weight:normal;'
                                ));
                            ?>
                        </div>
                    </td>
                    <td class="bottomBorder" width="20" align="center">
                        <?php
                        echo $this->Html->link('GO', '/games/view/' . $game['Game']['id'],
                            array(
                                'style' => 'color:white; font-weight:bold;'
                            ));
                        ?>
                    </td>
                </tr>
            <?php endforeach; ?>
        </table>
    <?php endif; ?>
</div>
