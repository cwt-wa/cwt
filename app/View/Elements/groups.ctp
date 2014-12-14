<?php echo $this->Html->css('groups', null, array('inline' => false)) ?>

<?php for ($i = 1; $i <= 8; $i++): ?>
    <div id="box" style="background-color:#2F2923">
    <div id="table">
        <table border="0" cellpadding="5" cellspacing="0">
            <tr>
                <td class="border" bgcolor="black" align="center" width="25">
                    <font color="yellow"><?php echo $group[$i]['group'] ?></font>
                </td>
                <td class="border" bgcolor="black" width="145">Nickname</td>
                <td class="border" bgcolor="black" align="center" width="50">Points</td>
                <td class="border" bgcolor="black" align="center" width="50">Games</td>
                <td class="border" bgcolor="black" align="center" width="50">Game Ratio</td>
                <td class="border" bgcolor="black" align="center" width="50">Round Ratio</td>
            </tr>
            <tr>
                <td class="border" bgcolor="green" align="center">1</td>
                <td class="border">
                    <?php
                    echo $this->Html->image($group[$i][1]['User']['flag'], array(
                        'style' => 'height:10px; width:auto;'
                    ));
                    ?>
                    <?php
                    echo $this->Html->link($group[$i][1]['User']['username'],
                        '/users/view/' . $group[$i][1]['User']['id'],
                        array(
                            'style' => 'color:white;'
                        ));
                    ?>
                </td>
                <td class="border" align="center">
                    <?php echo $group[$i][1]['Standing']['points'] ?>
                </td>
                <td class="border" align="center">
                    <?php echo $group[$i][1]['Standing']['games'] ?>
                </td>
                <td class="border" align="center">
                    <?php echo $group[$i][1]['Standing']['game_ratio'] ?>
                </td>
                <td class="border" align="center">
                    <?php echo $group[$i][1]['Standing']['round_ratio'] ?>
                </td>
            </tr>
            <tr>
                <td class="border" bgcolor="green" align="center">2</td>
                <td class="border">
                    <?php
                    echo $this->Html->image($group[$i][2]['User']['flag'], array(
                        'style' => 'height:10px; width:auto;'
                    ));
                    ?>
                    <?php
                    echo $this->Html->link($group[$i][2]['User']['username'],
                        '/users/view/' . $group[$i][2]['User']['id'],
                        array(
                            'style' => 'color:white;'
                        ));
                    ?>
                </td>
                <td class="border" align="center">
                    <?php echo $group[$i][2]['Standing']['points'] ?>
                </td>
                <td class="border" align="center">
                    <?php echo $group[$i][2]['Standing']['games'] ?>
                </td>
                <td class="border" align="center">
                    <?php echo $group[$i][2]['Standing']['game_ratio'] ?>
                </td>
                <td class="border" align="center">
                    <?php echo $group[$i][2]['Standing']['round_ratio'] ?>
                </td>
            </tr>
            <tr>
                <td class="border" bgcolor="red" align="center">3</td>
                <td class="border">
                    <?php
                    echo $this->Html->image($group[$i][3]['User']['flag'], array(
                        'style' => 'height:10px; width:auto;'
                    ));
                    ?>
                    <?php
                    echo $this->Html->link($group[$i][3]['User']['username'],
                        '/users/view/' . $group[$i][3]['User']['id'],
                        array(
                            'style' => 'color:white;'
                        ));
                    ?>
                </td>
                <td class="border" align="center">
                    <?php echo $group[$i][3]['Standing']['points'] ?>
                </td>
                <td class="border" align="center">
                    <?php echo $group[$i][3]['Standing']['games'] ?>
                </td>
                <td class="border" align="center">
                    <?php echo $group[$i][3]['Standing']['game_ratio'] ?>
                </td>
                <td class="border" align="center">
                    <?php echo $group[$i][3]['Standing']['round_ratio'] ?>
                </td>
            </tr>
            <tr>
                <td class="border" bgcolor="red" align="center">4</td>
                <td class="border">
                    <?php
                    echo $this->Html->image($group[$i][4]['User']['flag'], array(
                        'style' => 'height:10px; width:auto;'
                    ));
                    ?>
                    <?php
                    echo $this->Html->link($group[$i][4]['User']['username'],
                        '/users/view/' . $group[$i][4]['User']['id'],
                        array(
                            'style' => 'color:white;'
                        ));
                    ?>
                </td>
                <td class="border" align="center">
                    <?php echo $group[$i][4]['Standing']['points'] ?>
                </td>
                <td class="border" align="center">
                    <?php echo $group[$i][4]['Standing']['games'] ?>
                </td>
                <td class="border" align="center">
                    <?php echo $group[$i][4]['Standing']['game_ratio'] ?>
                </td>
                <td class="border" align="center">
                    <?php echo $group[$i][4]['Standing']['round_ratio'] ?>
                </td>
            </tr>
            <?php if (isset($this->params['pass'][0]) && $this->params['pass'][0] == 2006): // Only year with five group members. ?>
                <tr>
                    <td class="border" bgcolor="red" align="center">5</td>
                    <td class="border">
                        <?php
                        echo $this->Html->image($group[$i][5]['User']['flag'], array(
                            'style' => 'height:10px; width:auto;'
                        ));
                        ?>
                        <?php
                        echo $this->Html->link($group[$i][5]['User']['username'],
                            '/users/view/' . $group[$i][5]['User']['id'],
                            array(
                                'style' => 'color:white;'
                            ));
                        ?>
                    </td>
                    <td class="border" align="center">
                        <?php echo $group[$i][5]['Standing']['points'] ?>
                    </td>
                    <td class="border" align="center">
                        <?php echo $group[$i][5]['Standing']['games'] ?>
                    </td>
                    <td class="border" align="center">
                        <?php echo $group[$i][5]['Standing']['game_ratio'] ?>
                    </td>
                    <td class="border" align="center">
                        <?php echo $group[$i][5]['Standing']['round_ratio'] ?>
                    </td>
                </tr>
            <?php endif; ?>
        </table>
    </div>
    <?php if (isset($group[$i]['Game'])): ?>
        <table border="0" align="right" width="425">
            <?php foreach ($group[$i]['Game'] as $game): ?>
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
                            <div
                                title="<?php echo $game['Rating'][0]['likes'] ?> Likes, <?php echo $game['Rating'][0]['dislikes'] ?>  Dislikes"
                                id="ratings">
                                <div class="ratingsBar"
                                     style="width:<?php echo $game['Rating'][0]['p']['likes'] ?>%; background-color:green;"></div><div class="ratingsBar"
                                     style="width:<?php echo $game['Rating'][0]['p']['dislikes'] ?>%; background-color:red;"></div>
                            </div>
                            <div
                                title="<?php echo $game['Rating'][0]['lightside'] ?> Lightside, <?php echo $game['Rating'][0]['darkside'] ?>  Darkside"
                                id="ratings">
                                <div class="ratingsBar"
                                     style="width:<?php echo $game['Rating'][0]['p']['lightside'] ?>%; background-color:white;"></div><div class="ratingsBar"
                                     style="width:<?php echo $game['Rating'][0]['p']['darkside'] ?>%; background-color:black;"></div>
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
    <?php else: ?>
        <div style="text-align:center; margin-top:30px">
            <i>No games have been played so far.</i>
        </div>
    <?php endif; ?>

    <div style="clear:both;"></div>
    </div>
<?php endfor; ?>
