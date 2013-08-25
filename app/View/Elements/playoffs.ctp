<script type="text/javascript">
    function bet(h_or_a, game_id) {
        $.ajax({
            url: '/playoffs/index/bet',
            type: 'POST',
            data: {
                'h_or_a': h_or_a,
                'game_id': game_id
            },
            beforeSend: function() {
                $(':radio').attr('disabled', 'disabled');
            },
            success: function(result) {
                $('#content').html(result);
            }
        });
    }
</script>

<?php
echo $this->Html->css('playoff', null, array('inline' => false));
$newStep = array(8, 12, 14);
$margintop = 0; $marginsmall = 10; $marginbig = 20;
?>

<div id="step">
    <?php for($i = 0; $i < 16; $i++): ?>
    <?php if(in_array($i, $newStep)): ?>
    <?php
    switch($i) {
        case 8:
            $margintop = 33.5;
            $marginbig = $margintop + 53;
            $marginsmall = $marginbig;
            break;
        case 12:
            $margintop = $margintop + 57 + 15;
            $marginbig = $margintop * 2 + 15;
            $marginsmall = $marginbig;
            break;
        case 14:
            if(!isset($backtrack)) {
                $margintop = 253 - 77;
                $marginbig = 0;
                $marginsmall =  0;

                $i = 15;
            } else {
                $margintop = 15;
            }
    }
    ?>
</div>
<div id="step" style="margin-top:<?php echo $margintop; ?>px;">
<?php elseif(in_array($i + 1, $newStep)):
    $marginbig = 0;
    $marginsmall =  0;
endif; ?>

<?php if($i == 14): ?>
    <div style="text-align:center; font-size:12pt; color:black;">
        Third Place
    </div>
<?php elseif($i == 15): ?>
    <div style="text-align:center; font-size:16pt; color:black; font-weight:bold;">
        Final
    </div>
<?php endif; ?>

<?php if(@$playoff[$i]['Game'] == null): // Nothing at all ?>
    <table id="game" cellpadding="3" cellspacing="0">
        <tr>
            <td id="players">

            </td>
            <td id="result" align="center">

            </td>
            <td colspan="3" id="rating" align="center">

            </td>
        </tr>
        <tr>
            <td id="players" style="border-top:none;">

            </td>
            <td id="result" align="center" style="border-top:none;">

            </td>
            <td id="comments" style="background:#1E2B2E;" align="center">

            </td>
            <td id="replays" style="background:#1E2B2E;" align="center">

            </td>
            <td id="go" align="center">

            </td>
        </tr>
    </table>
<?php else: ?>
    <?php if($playoff[$i]['Game']['home_id'] != '0' XOR $playoff[$i]['Game']['away_id'] != '0'): // One dude ?>
        <table id="game" cellpadding="3" cellspacing="0">
            <tr>
                <td id="players">
                    <?php
                    echo @$this->Html->link(@$playoff[$i]['Home']['username'],
                        '/users/view/' . @$playoff[$i]['Home']['id'],
                        array(
                            'style' => 'color:white;'
                        ));
                    ?>
                </td>
                <td id="result" align="center">

                </td>
                <td colspan="3" id="rating" align="center">

                </td>
            </tr>
            <tr>
                <td id="players" style="border-top:none;">
                    <?php
                    echo @$this->Html->link(@$playoff[$i]['Away']['username'],
                        '/users/view/' . @$playoff[$i]['Away']['id'],
                        array(
                            'style' => 'color:white;1E2B2E'
                        ));
                    ?>
                </td>
                <td id="result" align="center" style="border-top:none;">

                </td>
                <td id="comments" style="background:#1E2B2E;" align="center">

                </td>
                <td id="replays" style="background:#1E2B2E;" align="center">

                </td>
                <td id="go" align="center">

                </td>
            </tr>
        </table>
    <?php elseif ($playoff[$i]['Game']['score_h'] == '0' && $playoff[$i]['Game']['score_a'] == '0'): // Not yet played ?>
        <table id="game" cellpadding="3" cellspacing="0">
            <tr>
                <td id="players" title="<?php echo $playoff[$i]['Playoff']['bet_h'] . ', ' . round($playoff[$i]['Playoff']['bets']['bet_h'], 1) ?>%">
                    <div id="nick">
                        <?php
                        echo $this->Html->link($playoff[$i]['Home']['username'],
                            '/users/view/' . $playoff[$i]['Home']['id'],
                            array(
                                'style' => 'color:white;'
                            ));
                        ?>
                    </div>
                    <div id="bar" style="width:<?php echo $playoff[$i]['Playoff']['bets']['bet_h'] ?>%;">
                    </div>
                </td>
                <td id="result" align="center">
                    <?php
                    if($logged_in) {
                        if($playoff[$i]['Playoff']['bet_h_traced'] || $playoff[$i]['Playoff']['bet_a_traced']) {
                            if($playoff[$i]['Playoff']['bet_h_traced']) {
                                echo 'X';
                            } else {
                                echo '&nbsp;';
                            }
                        } else {
                            echo $this->Form->radio('bet_h', array(
                                '' => ''
                            ), array(
                                'legend' => false,
                                'style' => 'margin:0px;',
                                'onClick' => 'bet(\'home\', \'' . $playoff[$i]['Game']['id'] . '\')'
                            ));
                        }
                    }
                    ?>
                </td>
                <td id="empty" align="center">
                    <?php echo $playoff[$i]['Playoff']['bet_h'] ?>,
                    <?php echo round($playoff[$i]['Playoff']['bets']['bet_h'], 1) ?>%
                </td>
            </tr>
            <tr>
                <td id="players" style="border-top:none;" title="<?php echo $playoff[$i]['Playoff']['bet_a'] . ', ' . round($playoff[$i]['Playoff']['bets']['bet_a'], 1) ?>%">
                    <div id="nick">
                        <?php
                        echo $this->Html->link($playoff[$i]['Away']['username'],
                            '/users/view/' . $playoff[$i]['Away']['id'],
                            array(
                                'style' => 'color:white;'
                            ));
                        ?>
                    </div>
                    <div id="bar" style="width:<?php echo $playoff[$i]['Playoff']['bets']['bet_a'] ?>%;">
                    </div>
                </td>
                <td id="result" align="center" style="border-top:none;">
                    <?php
                    if($logged_in) {
                        if($playoff[$i]['Playoff']['bet_h_traced'] || $playoff[$i]['Playoff']['bet_a_traced']) {
                            if($playoff[$i]['Playoff']['bet_a_traced']) {
                                echo 'X';
                            } else {
                                echo '&nbsp;';
                            }
                        } else {
                            echo $this->Form->radio('bet_a', array(
                                '' => ''
                            ), array(
                                'legend' => false,
                                'style' => 'margin:0px;',
                                'onClick' => 'bet(\'away\', \'' . $playoff[$i]['Game']['id'] . '\')'
                            ));
                        }
                    }
                    ?>
                </td>
                <td id="empty" align="center" style="border-top:none;">
                    <?php echo $playoff[$i]['Playoff']['bet_a'] ?>,
                    <?php echo round($playoff[$i]['Playoff']['bets']['bet_a'], 1) ?>%
                </td>
            </tr>
        </table>
    <?php else: // It's been played ?>
        <table id="game" cellpadding="3" cellspacing="0">
            <tr>
                <td id="players" title="<?php echo $playoff[$i]['Playoff']['bet_h'] . ', ' . round($playoff[$i]['Playoff']['bets']['bet_h'], 1) ?>%">
                    <div id="nick">
                        <?php
                        echo $this->Html->link($playoff[$i]['Home']['username'],
                            '/users/view/' . $playoff[$i]['Home']['id'],
                            array(
                                'style' => 'color:white;'
                            ));
                        ?>
                    </div>
                    <div id="bar" style="width:<?php echo $playoff[$i]['Playoff']['bets']['bet_h'] ?>%;">
                    </div>
                </td>
                <td id="result" align="center">
                    <?php echo $playoff[$i]['Game']['score_h']; ?>
                </td>
                <td colspan="3" id="rating" align="center">
                    <?php if (!$playoff[$i]['Game']['techwin']): ?>
                        <div title="<?php echo $playoff[$i]['Rating'][0]['likes'] ?> Likes, <?php echo $playoff[$i]['Rating'][0]['dislikes'] ?> Dislikes" id="ratings">
                            <div class="ratingsBar" style="width:<?php echo $playoff[$i]['Rating'][0]['p']['likes'] ?>%; background-color: green;"></div><div class="ratingsBar" style="width:<?php echo $playoff[$i]['Rating'][0]['p']['dislikes'] ?>%; background-color: red;"></div>
                        </div>
                        <div title="<?php echo $playoff[$i]['Rating'][0]['lightside'] ?> Lightside, <?php echo $playoff[$i]['Rating'][0]['darkside'] ?> Darkside" id="ratings">
                            <div class="ratingsBar" style="width:<?php echo $playoff[$i]['Rating'][0]['p']['lightside'] ?>%; background-color: white;"></div><div class="ratingsBar" style="width:<?php echo $playoff[$i]['Rating'][0]['p']['darkside'] ?>%; background-color: black;"></div>
                        </div>
                    <?php else: ?>
                        <span style="font-size: 9pt;">Tech. Win</span>
                    <?php endif; ?>
                </td>
            </tr>
            <tr>
                <td id="players" style="border-top:none;" title="<?php echo $playoff[$i]['Playoff']['bet_a'] . ', ' . round($playoff[$i]['Playoff']['bets']['bet_a'], 1) ?>%">
                    <div id="nick">
                        <?php
                        echo $this->Html->link($playoff[$i]['Away']['username'],
                            '/users/view/' . $playoff[$i]['Away']['id'],
                            array(
                                'style' => 'color:white;'
                            ));
                        ?>
                    </div>
                    <div id="bar" style="width:<?php echo $playoff[$i]['Playoff']['bets']['bet_a'] ?>%;">
                    </div>
                </td>
                <td id="result" align="center" style="border-top:none;">
                    <?php echo $playoff[$i]['Game']['score_a']; ?>
                </td>
                <td id="comments" align="center">
                    <?php echo count($playoff[$i]['Comment']) ?>
                </td>
                <td id="replays" align="center">
                    <div title="10 times downloaded">
                        <?php
                        echo $this->Html->link($playoff[$i]['Game']['downloads'],
                            '/games/download/' . $playoff[$i]['Game']['id'],
                            array(
                                'style' => 'color:white; font-weight:normal;'
                            ));
                        ?>
                    </div>
                </td>
                <td id="go" align="center">
                    <?php
                    echo $this->Html->link('GO', '/games/view/' . $playoff[$i]['Game']['id'],
                        array(
                            'style' => 'color:white; font-weight:bold;'
                        ));
                    ?>
                </td>
            </tr>
        </table>
    <?php endif; ?>
<?php endif; ?>

<?php if($i % 2 != 0): ?>
    <div style="margin-top:<?php echo $marginbig ?>px;"></div>
<?php else: ?>
    <div style="margin-top:<?php echo $marginsmall ?>px;"></div>
<?php endif; ?>

<?php
if($i == 15) {
    $i = 13;
    $backtrack = 1;
} elseif($i == 14) {
    break;
}
?>
<?php endfor; ?>
</div>
