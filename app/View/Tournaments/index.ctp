<?php if (empty($tournament)): ?>
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
        <div style="text-align:center; font-size:15pt; font-weight:bold;">
            <?php
            echo $tournament['Tournament']['year']
            ?>
            -
            <?php
            echo $this->Html->link('Results', '/archive/' . $tournament['Tournament']['year'])
            ?>
        </div><br />
        <table cellpadding="10">
            <tr>
                <td align="left" style="white-space:nowrap;">
                    <?php
                    echo $this->Html->image('/img/medals/gold1.png', array(
                        'style' => 'height:20px;'
                    ));
                    ?>&nbsp;
                    <?php
                    echo $this->Html->link(
                            $tournament['Gold']['username'], '/users/view/' . $tournament['Gold']['id'], array(
                        'class' => 'plain',
                        'style' => 'font-size:12pt;'
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
                        'class' => 'plain',
                        'style' => 'font-size:12pt;'
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
                        'class' => 'plain',
                        'style' => 'font-size:12pt;'
                    ));
                    ?>
                </td>
                <td align="left" style="border-left:1px solid gray; white-space:nowrap;">
            <u>Head Mod</u><br />
            <?php
            echo $this->Html->link($tournament['User']['username'], '/users/view/' . $tournament['User']['id'], array(
                'class' => 'plain'
            ));
            ?><br />
            <br />
            <u>Helping Mods</u><br />
            <?php
            if (empty($tournament['Helper'])) {
                echo '<i>none</i>';
            } else {
                $helpers = array();

                foreach ($tournament['Helper'] as $helper) {
                    $helpers[] = $this->Html->link(
                            $helper['username'], '/users/view/' . $helper['id'], array(
                        'class' => 'plain'
                            ));
                }

                echo $this->Text->toList($helpers);
            }
            ?>
            </td>
            <td align="left" style="border-left:1px solid gray;">
                <?php echo $tournament['Tournament']['review']; ?>
                <?php
                if (!empty($tournament['Tournament']['review'])) {
                    echo ' - ' . $this->Html->link($tournament['User']['username'], '/users/view/' . $tournament['User']['id'], array(
                        'class' => 'plain',
                        'style' => 'font-style:italic;'
                    ));
                } else {
                    echo '<i>Unfortunately the head mod has not written a review.</i>';
                }
                ?>
            </td>
            </tr>
        </table>
    </div>
<?php endforeach; ?>
