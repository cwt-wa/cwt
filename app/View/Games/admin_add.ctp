<script type="text/javascript">
    $(document).ready(function() {
        $("#").change(function() {
            $.ajax({
                // Get opponents the home player can play against.
            });
        });
    });
</script>

<div id="box" style="background-color:#3F2828; text-align:center; width:785px; height:80px;">
    <h1 style="margin-top:0px;">Add a Game</h1>
</div>
<div id="box" style="background-color:#887059; text-align:center; width:785px; margin-top:10px; height:350px;">
    <?php
    echo $this->Form->create('Game', array(
        'inputDefaults' => array(
            'label' => false,
            'div' => false,
            'error' => false
        ),
        'type' => 'file'
    ));
    ?>

    <table align="center" cellpadding="5" cellspacing="0" border="0">
        <tr>
            <td align="right">
                <b>Home:</b>
            </td>
            <td align="left">
                <?php echo $this->Form->input('home_id', array('empty' => true)); ?>
            </td>
        </tr>
        <tr>
            <td align="right">
                <b>Away:</b>
            </td>
            <td align="left">
                <?php echo $this->Form->input('away_id', array('empty' => true)); ?>
            </td>
        </tr>
        <tr>
            <td align="right">
                <b>Tech. win:</b>
            </td>
            <td align="left">
                <?php echo $this->Form->input('techwin', array('value' => '1')); ?>
            </td>
        </tr>
        <tr>
            <td align="right">
                &nbsp;
            </td>
            <td align="left">
                <?php
                echo $this->Form->submit('Submit', array(
                    'div' => false
                ));
                ?>
            </td>
        </tr>
    </table>
    <?php echo $this->Form->end() ?>
</div>
