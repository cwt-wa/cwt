<div style="text-align:center; margin-left:auto; margin-right:auto; color:black; font-weight:bold; font-size:15pt;">
    <?php
    echo $this->Form->create('Upload', array('type' => 'file'));
    echo $this->Form->file('Upload.photo');
    echo '&nbsp;' . $this->Form->submit('Upload Photo', array(
            'div' => false
        ));
    echo $this->Form->end();
    ?>

    <?php if ($photo): ?>
        <?php
        echo $this->Form->create('Delete');
        echo $this->Form->submit('No photo on my profile!', array(
            'div' => false
        ));
        echo $this->Form->hidden('isset');
        echo $this->Form->end();
        ?>

        <br>


        <?php echo $this->Html->image('users/' . $photo, array(
            'style' => 'max-width:980px; height:auto;')); ?>
    <?php else: ?>
        <br>
        You have not uploaded a photo of you.
    <?php endif; ?>
</div>
