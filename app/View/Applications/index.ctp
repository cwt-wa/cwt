<?php foreach($applicants as $key => $val): ?>	
	<?php
		if($key % 2 == 0) {
			$color = '#3F3429';
		} else {
			$color = '#26323B';
		}
	?>

	<div id="boxFloat" style="background-color:<?php echo $color ?>; width:20px; text-align:center;">	
		#<?php echo $key + 1 ?>
	</div>
	<div id="boxFloat" style="background-color:<?php echo $color ?>; width:615px; text-align:center; margin-left: 8px;">
		<?php
			echo $this->Html->link($val['User']['username'], 
	            '/users/view/' . $val['User']['id'],
	            array('style' => 'color:white;'));
		?>
	</div>
	<div id="box" style="background-color:<?php echo $color ?>; width:200px; text-align:center; margin-left:735px; margin-top:0px;">
		<?php
			echo $this->Time->format('F jS, H:i', $val['Application']['created']);
		?>
	</div>
	<div style="clear:both;">&nbsp;</div>
<?php endforeach; ?>
