<?php echo $this->Form->create('StreamAdd'); ?>
<div id="box"
     style="background-color:#<?php echo $stream['Stream']['color'] ?>; text-align:center; font-size:16pt; font-weight:bold;">
    You can schedule streams for these games:
</div>
<div id="box" class="ScheduleList" style="background-color:#<?php echo $stream['Stream']['color'] ?>; text-align:12pt;">
    <table border="0" cellpadding="10" cellspacing="0" align="center">
        <?php if ($schedules): ?>
            <?php foreach ($schedules as $schedule): ?>
                <tr>
                    <td align="right">
                        <?php
                        echo $this->Form->checkbox(
                            'schedule' . $schedule['Schedule']['id'],
                            array(
                                'value' => $schedule['Schedule']['id'],
                                'hiddenField' => false
                            ));
                        ?>
                    </td>
                    <td align="right">
                        <?php
                        echo $this->Time->format(
                            'M j, H:i', $schedule['Schedule']['when']);
                        ?>
                    </td>
                    <td align="right">
                        <?php
                        echo $this->Html->link(
                            $schedule['Scheduler']['username'],
                            '/users/' . $schedule['Scheduler']['id'])
                        ?>
                    </td>
                    <td align="center">
                        vs.
                    </td>
                    <td align="left">
                        <?php
                        echo $this->Html->link(
                            $schedule['Scheduled']['username'],
                            '/users/' . $schedule['Scheduled']['id'])
                        ?>
                    </td>
                    <td align="left">
                        <?php foreach ($schedule['Stream'] as $schedStream): ?>
                            <?php
                            echo $this->Html->link(
                                $this->Html->image('popcorn.png', array(
                                    'style' => 'height:17px; width:auto;',
                                    'alt' => $schedStream['title'],
                                    'title' => $schedStream['title']
                                )),
                                '/streams/view/' . $schedStream['id'], array(
                                    'escape' => false
                                )
                            );
                            ?>
                        <?php endforeach; ?>
                    </td>
                </tr>
            <?php endforeach; ?>
        <?php else: ?>
            <div style="margin-left:40px; margin-top:30px; font-style:italic;">
                There are no more games you could schedule a stream for.
            </div>
        <?php endif; ?>
    </table>
</div>
<div id="box" class="ScheduleList"
     style="background-color:#<?php echo $stream['Stream']['color'] ?>; text-align:center;">
    <?php echo $this->Form->end('Schedule Stream for selected Games') ?>
</div>
<hr>
<div id="box"
     style="background-color:#<?php echo $stream['Stream']['color'] ?>; text-align:center; font-size:16pt; font-weight:bold;">
    You have scheduled streams for these games:
</div>
<?php echo $this->Form->create('StreamDelete'); ?>
<div id="box" class="ScheduleList" style="background-color:#<?php echo $stream['Stream']['color'] ?>; text-align:12pt;">
    <table border="0" cellpadding="10" cellspacing="0" align="center">
        <?php if ($scheduleds): ?>
            <?php foreach ($scheduleds as $scheduled): ?>
                <tr>
                    <td align="right">
                        <?php
                        echo $this->Form->checkbox(
                            'schedule' . $scheduled['Schedule']['id'],
                            array(
                                'value' => $scheduled['Schedule']['id'],
                                'hiddenField' => false
                            ));
                        ?>
                    </td>
                    <td align="right">
                        <?php
                        echo $this->Time->format(
                            'M j, H:i', $scheduled['Schedule']['when']);
                        ?>
                    </td>
                    <td align="right">
                        <?php
                        echo $this->Html->link(
                            $scheduled['Scheduler']['username'],
                            '/users/' . $scheduled['Scheduler']['id'])
                        ?>
                    </td>
                    <td align="center">
                        vs.
                    </td>
                    <td align="left">
                        <?php
                        echo $this->Html->link(
                            $scheduled['Scheduled']['username'],
                            '/users/' . $scheduled['Scheduled']['id'])
                        ?>
                    </td>
                    <td align="left">
                        <?php foreach ($scheduled['Stream'] as $schedStream): ?>
                            <?php
                            echo $this->Html->link(
                                $this->Html->image('popcorn.png', array(
                                    'style' => 'height:17px; width:auto;',
                                    'alt' => $schedStream['title'],
                                    'title' => $schedStream['title']
                                )),
                                '/streams/view/' . $schedStream['id'], array(
                                    'escape' => false
                                )
                            );
                            ?>
                        <?php endforeach; ?>
                    </td>
                </tr>
            <?php endforeach; ?>
        <?php else: ?>
            <div style="margin-left:40px; margin-top:30px; font-style:italic;">
                You have not currently scheduled any games.
            </div>
        <?php endif; ?>
    </table>
</div>
<div id="box" class="ScheduleList"
     style="background-color:#<?php echo $stream['Stream']['color'] ?>; text-align:center;">
    <?php echo $this->Form->end('Remove Scheduled Streams for selected Games') ?>
</div>
