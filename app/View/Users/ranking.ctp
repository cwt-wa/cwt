<?php echo $this->Html->css('groups', null, array('inline' => false)) ?>

<div id="box" style="background-color:#2F2923">
<div style="position: absolute; background-color: lightgray; padding: 10px; width: 140px; margin: -20px -20px -20px 800px;"><a href="#explanation">How is this being calculated?</a></div>
<table border="0" cellspace="0" cellpadding="3" align="center">

    <?php foreach ($users as $key => $user): ?>
        <tr>
            <td class="bottomBorder" align="center">
                <?php echo $user['position']; ?>
            </td>
            <td class="bottomBorder" align="center">
                <?php
                echo $this->Html->image('flags/' . str_replace(' ', '_', strtolower($user['Profile']['country'])) . '.png', array(
                    'alt' => $user['Profile']['country'],
                    'title' => $user['Profile']['country']
                ));
                ?>
            </td>
            <td class="bottomBorder" align="center">
                <?php echo $user['Profile']['clan'] ?>
            </td>
            <td class="bottomBorder" align="left">
                <?php
                echo $this->Html->link($user['User']['username'],
                    '/users/view/' . $user['User']['id']);
                ?>
            </td>
            <td class="bottomBorder" align="right">
                <b><?php echo $user['User']['achievements'] ?></b>
            </td>
            <td class="bottomBorder" align="left">
                <?php
                foreach ($achievements as $key => $val) {
                    if ($val['gold'] == $user['User']['id']) {
                        echo $this->Html->image('medals/' . array_search($val['gold'], $val) . '.gif', array(
                                    'alt' => $key,
                                    'title' => $key,
                                    'style' => 'height:15px; width:auto;'
                                )
                            ) . '&nbsp;';
                    } elseif ($val['silver'] == $user['User']['id']) {
                        echo $this->Html->image('medals/' . array_search($val['silver'], $val) . '.gif', array(
                                    'alt' => $key,
                                    'title' => $key,
                                    'style' => 'height:15px; width:auto;'
                                )
                            ) . '&nbsp;';
                    } elseif ($val['bronze'] == $user['User']['id']) {
                        echo $this->Html->image('medals/' . array_search($val['bronze'], $val) . '.gif', array(
                                    'alt' => $key,
                                    'title' => $key,
                                    'style' => 'height:15px; width:auto;'
                                )
                            ) . '&nbsp;';
                    }
                }
                ?>
            </td>
        </tr>
    <?php endforeach; ?>
</table>
</div>
<div id="explanation"></div>
<div id="box" style="background-color:#2F2923">
    Stages a player can reach are numerated in ascending order from group stage one, to winner of the finale seven. Each stage is multiplied by two and the sum is divided by the number of participations.<br/>
    <br/>
    Example: Xaositect has won in 2002, became second in 2003 and reached the group stage in 2004. He has participated in three CWTs.<br/>
    Calculation: (7 * 2 + 6 * 2 + 1 * 2) / 3 = 9.33<br/>
    <br/>
    Users only appear when they’ve participated in at least two CWTs.<br/>
    If you want more information click on a user and see his CWT history timeline.
</div>
