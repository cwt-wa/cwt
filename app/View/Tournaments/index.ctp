<?php if (empty($tournaments)): ?>
    <div id="box" style="text-align: center; font-size: 12pt; font-weight: bold; background-color: #0A0E1C">
        There are no archived tournaments.
    </div>
<?php endif; ?>

<?php foreach ($tournaments as $tournament): ?>
    <?php
    if ($tournament['Tournament']['year'] % 2 == 0) {
        $backgroundColor = '#4D3C5D';
    } else {
        $backgroundColor = '#33283E';
    }
    ?>

    <div id="box" style="background-color:<?php echo $backgroundColor ?>;">
        <div id="year">
            <?php
            echo $tournament['Tournament']['year']
            ?>
        </div><br />
        <table cellpadding="10">
            <tr>
                <td align="left" width="300" style="white-space: nowrap; font-size: 18pt;">
                    <?php
                    echo $this->Html->image('/img/medals/gold1.png', array(
                        'style' => 'height:20px;'
                    ));
                    ?>&nbsp;
                    <?php
                    echo $this->Html->link(
                            $tournament['Gold']['username'], '/users/view/' . $tournament['Gold']['id'], array(
                        'class' => 'plain'
                    ));
                    ?><br />
                    <?php
                    echo $this->Html->image('/img/medals/silver2.png', array(
                        'style' => 'height:20px;'
                    ));
                    ?>&nbsp;
                    <?php
                    echo $this->Html->link(
                            $tournament['Silver']['username'], '/users/view/' . $tournament['Silver']['id'], array(
                        'class' => 'plain'
                    ));
                    ?><br />
                    <?php
                    echo $this->Html->image('/img/medals/bronze3.png', array(
                        'style' => 'height:20px;'
                    ));
                    ?>&nbsp;
                    <?php
                    echo $this->Html->link(
                            $tournament['Bronze']['username'], '/users/view/' . $tournament['Bronze']['id'], array(
                        'class' => 'plain'
                    ));
                    ?>
                </td>
                <td align="left" width="300" style="border-left:1px solid gray;">
            Organized by<br />
            <?php
            if (empty($tournament['Moderator'])) {
                echo '<i>none</i>';
            } else {
                $helpers = array();

                foreach ($tournament['Moderator'] as $moderator) {
                    $helpers[] = $this->Html->link(
                            $moderator['username'], '/users/view/' . $moderator['id'], array(
                        'class' => 'plain'
                            ));
                }

                echo $this->Text->toList($helpers);
            }
            ?>
            </td>
            <td align="left" style="border-left:1px solid gray;">
                <?php
                echo $this->Html->link(
                    'Games, Standings<br/>and more',
                    '/archive/' . $tournament['Tournament']['year'],
                    array('escape' => false));
                ?>
            </td>
            </tr>
        </table>
        <br/>
        <div style="text-align: center;">
            <?php echo nl2br($this->Text->autoLinkUrls($tournament['Tournament']['review'])); ?>
            <?php
            if (empty($tournament['Tournament']['review'])) {
                echo '<i>Unfortunately no review was written</i>';
            }
            ?>
        </div>
    </div>
<?php endforeach; ?>
