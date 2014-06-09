<div style="color:black;">
    <?php foreach ($users as $user): ?>
        <script type="text/javascript">
            $(document).ready(function () {
                $('#<?php echo $user['User']['id']; ?>').change(function () {
                    $.ajax({
                        url: '/profiles/quickCountry',
                        type: 'POST',
                        data: {
                            'country': $('#<?php echo $user['User']['id']; ?>').val(),
                            'user_id': '<?php echo $user['User']['id']; ?>'
                        },
                        beforeSend: function () {
                            statusMSG = '<img src="/img/loading.gif" style="height:18px; width:18px;">';
                            $('#status_<?php echo $user['User']['id']; ?>').html(statusMSG);
                        },
                        complete: function () {
                            statusMSG = '<img src="/img/tick.png" style="height:18px; width:auto;">';
                            $('#status_<?php echo $user['User']['id']; ?>').html(statusMSG);
                        }
                    });
                });
            });
        </script>

        <?php echo $user['User']['username']; ?>
        <?php
        echo $this->Form->select('country', $country, array('id' => $user['User']['id']))
        ?>
        <span id="status_<?php echo $user['User']['id']; ?>"></span>
        <br>
    <?php endforeach; ?>
</div>
