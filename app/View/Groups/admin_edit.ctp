<script type="text/javascript">
    $(document).ready(function () {
        $('#GroupReplace').click(function () {
            newPlayer = $('#GroupActive :selected').text();
            oldPlayer = $('#GroupInactive :selected').text();

            msg = 'Do you really want to replace ' + oldPlayer + ' with ' + newPlayer;
            msg += '?\nThe games and points of ' + oldPlayer + ' will be voided ';
            msg += 'and ' + newPlayer + ' will have a fresh start.'

            if (confirm(msg)) {
                return true;
            }
            return false;
        });
    });
</script>

<div id="box" style="background-color:#3F2828; text-align:center;">
    <h1>Replace a player</h1>
</div>
<div id="box" style="background-color:#3F2828; text-align:center;">
    <?php
    echo $this->Form->create('Group');
    ?>Replace
    <?php
    echo $this->Form->select('Inactive', $inactive);
    ?> with
    <?php
    echo $this->Form->select('Active', $active);
    ?><br><br>
    <?php
    echo $this->Form->submit('Replace', array('id' => 'GroupReplace'));
    ?>
    <?php
    echo $this->Form->end();    ?>
</div>
