<?php if(is_array($newpms)): ?>
	<div id="senderPM"><?php echo $newpms['sender'] ?></div>
	<div id="messagePM"><?php echo $newpms['message'] ?></div>
	<div id="counterPM">1</div>
<?php elseif($newpms > 1): ?>
	<div id="counterPM"><?php echo $newpms ?></div>
<?php endif; ?>