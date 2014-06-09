<script type="text/javascript">
    $(document).ready(function () {
        $("select#GameHomeId").change(function () {
            $.ajax({
                url: '/admin/games/techwin',
                type: 'POST',
                data: {
                    'getAways': true,
                    'home_id': $('select#GameHomeId').val()
                },
                success: function (response) {
                    $('select#GameAwayId').html($(response).find('select#GameAwayId').html());
                }
            });
        });
    });
</script>

<div id="box" style="background-color:#3F2828; text-align:center;">
    <h1 style="margin-top:0px;">Add a Game</h1>
</div>
<div id="box" style="background-color:#887059;">
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
                <b>Winner:</b>
            </td>
            <td align="left">
                <?php echo $this->Form->input('home_id', array('empty' => true)); ?>
            </td>
        </tr>
        <tr>
            <td align="right">
                <b>Loser:</b>
            </td>
            <td align="left">
                <?php echo $this->Form->input('away_id', array('empty' => false)); ?>
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
