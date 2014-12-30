<?php echo $this->Html->css('groups', null, array('inline' => false)) ?>

<div id="box" style="background-color:#2F2923">
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
            <td class="bottomBorder" align="right">
                <?php echo $user['User']['participations']; ?>
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