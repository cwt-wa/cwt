<div id="box" style="background-color:#3F2828; text-align:center;">
	<h1>
		<?php if (@$link !== false): ?>
			<span style="font-weight:normal;">Archive of</span> <?php
			echo $this->Html->link($tournamentYear, '/archive/' . $tournamentYear);
			?>
		<?php else: ?>
			<span style="font-weight:normal;">Archive of</span> <?php echo $tournamentYear; ?>
		<?php endif; ?>
	</h1>
</div>
