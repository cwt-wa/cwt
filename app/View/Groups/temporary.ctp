<?php echo $this->Form->create('Report', array('type' => 'file')) ?>
<?php echo $this->Form->file('Report.replays'); ?>
<?php echo $this->Form->end('Report this Game'); ?>