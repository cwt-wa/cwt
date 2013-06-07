<?php echo $this->Html->css('livevalidation'); ?>

<div id="box" style="background-color: #3E283E; text-align: center;">
    <h1>Get a new password here</h1>
</div>

<div id="box" style="background-color:#635240;">

    <?php
    echo $this->Form->create('User', array(
        'inputDefaults' => array(
            'label' => false,
            'style' => 'text-align: center; display: inline;',
            'div' => false
        )
    ));
    ?>

    <table align="center" cellpadding="5">
        <tr>
            <td align="right">
                Your username:
            </td>
            <td>
                <?php
                echo $this->Form->input('User.userWhoForgot');
                ?>
            </td>
        </tr>
        <trd>
            <td>
                &nbsp;
            </td>
            <td>
                <?php
                echo $this->Form->submit('Send a new password', array(
                    'div' => false
                ));
                ?>
            </td>
        </trd>
    </table>

    <?php
    echo $this->Form->end();
    ?>

</div>
