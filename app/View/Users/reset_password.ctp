<div id="box" style="background-color:#635240; width:270px; text-align:center;">
    <?php
    echo $this->Form->create('Password', array(
        'inputDefaults' => array(
            'label' => false
        )
    ));
    ?>

    <fieldset>
        <legend><b>Enter your new password twice:</b></legend>

        <?php echo $this->Form->password('new1', array('style' => 'text-align:center; margin-bottom:3px')) ?>
        <?php echo $this->Form->password('new2', array('style' => 'text-align:center;')) ?>
    </fieldset>
    <br>
    <?php echo $this->Form->end('Reset Password'); ?>
</div>
