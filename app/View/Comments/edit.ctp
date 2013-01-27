<?php 
	echo $this->Html->script('bbcode', array('inline' => false));
	echo $this->Html->css('bbcode', null, array('inline' => false));
	//debug($game);
?>

<div id="box" style="background-color:#3F2828; text-align:center;">
	<h1>Edit your comment for this game:</h1>
	<span style="font-size:8pt;">
		Submit an empty modification to delete the comment entirely.
	</span>
</div>

<div id="box" style="background-color:#1A2427; text-align:center; font-size:16pt; margin-top:0px">
	<font color="lightgray"><?php echo $game['stage'] ?>:</font>	
	<?php 
		echo $this->Html->link($game['Home']['username'],
			'/users/view/' . $game['Home']['id'],
			array(
				'class' => 'plain'
			)
		); 
	?>
	 <?php echo $game['Game']['score_h']; ?>-<?php echo $game['Game']['score_a']; ?>
	 <?php 
	 	echo $this->Html->link($game['Away']['username'],
			'/users/view/' . $game['Away']['id'],
			array(
				'class' => 'plain'
			)
		); 
	 ?>	
</div>

<?php
	echo $this->element('bbcode', array(
		'style' => 'width:958px; height:500px;',
		'destination' => '/comments/edit/' . $comment['Comment']['id'],
		'redirect' => '/games/view/' . $comment['Game']['id'],
		'value' => $comment['Comment']['message']
	));
?>