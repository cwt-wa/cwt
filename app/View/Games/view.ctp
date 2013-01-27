<?php echo $this->Html->script('game', array('inline' => false)); ?>

<div id="boxFloat" style="border:none; box-shadow:none; width:240px; padding:0px;">
	<?php if(isset($game['winner']['photo'])): ?>						
				<?php 
					echo $this->Html->image('users/' . $game['winner']['photo'], array(
						'style' => 'height:auto; max-width:236px; border:2px solid #A88B3F; padding:0px; margin-top:0px; box-shadow:4px 4px 3px #C4C4C4; text-align:center;'
					));
				?>		
	<?php endif; ?>
	<div id="box" style="height:30px; width:223px; float:left; margin-top:10px; background-color:#93886F; font-size:18pt; padding:5px; padding-left:10px;">
		<?php
			echo $this->Html->image('awesome.png', array(
				'style' => 'height:25px; width:auto;'
			));
		?>

		<div style="margin-left:30px; margin-top:-27px;">
			<?php	
				echo '&nbsp;' . $this->Html->link($game['winner']['username'],
					'/users/view/' . $game['winner']['id'],
					array(
						'class' => 'plain'
					)
				); 
			?>
		</div>
	</div>
	<div id="box" style="margin-top:60px; background-color:#93886F">
		Reported by 
		<?php 
			echo $this->Html->link($game['Report']['username'], 
				'/users/view/' . $game['Report']['id']);
		?><br>
		<?php
			echo $this->Time->timeAgoInWords($game['Game']['created'], array(
				'format' => 'M j, H:i',
				'end' => '+1 day',
				'accuracy' => array('hour' => 'hour')
			));
		?>
	</div>
</div>
<div id="box" style="box-shadow:none; border:none; margin-left:250px; padding:0px 0px 0px 0px">
	<div id="box" style="background-color:#3F2828; text-align:center; font-size:16pt; margin-top:0px">
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
	<?php if(isset($game['Playoff']['bets'])): ?>
		<div id="box" style="padding:0px; white-space:nowrap;">
			<div title="<?php echo $game['Playoff']['bet_h'] . ', ' . round($game['Playoff']['bets']['bet_h'], 1) ?>%<?php if($game['Playoff']['bet_h_traced']) echo ' [X]'; ?>" style="background-color:blue; display:inline-block; text-align:right; width:<?php echo $game['Playoff']['bets']['bet_h'] ?>%; font-weight:bold; margin:0px; padding:3px 0.5px 3px 0px;">
				<?php if($game['Playoff']['bets']['bet_h'] >= 10): ?>	
					<?php
						if($game['Playoff']['bet_h_traced']) {
							echo '[X] ';
						}
					?>
					<?php echo $game['Playoff']['bet_h'] ?>, <?php echo round($game['Playoff']['bets']['bet_h'], 1) ?>%
					&nbsp;
				<?php else: ?>
					<font color="blue">l</font>			
				<?php endif; ?>
			</div>
			<div title="<?php echo $game['Playoff']['bet_a'] . ', ' . round($game['Playoff']['bets']['bet_a'], 1) ?>%<?php if($game['Playoff']['bet_a_traced']) echo ' [X]'; ?>" style="background-color:orange; display:inline-block; text-align:left; width:<?php echo $game['Playoff']['bets']['bet_a'] ?>%; font-weight:bold; margin:0px 0px 0px -3px; padding:3px 0px 3px 0.5px;">
				<?php if($game['Playoff']['bets']['bet_a'] >= 10): ?>		
					&nbsp;
					<?php echo $game['Playoff']['bet_a'] ?>, <?php echo round($game['Playoff']['bets']['bet_a'], 1) ?>%
					<?php
						if($game['Playoff']['bet_a_traced']) {
							echo ' [X]';
						}
					?>
				<?php else: ?>
					<font color="orange">l</font>
				<?php endif; ?>
			</div>
		</div>
	<?php endif; ?>
	<div id="ratings" style="float:left;">
		<?php echo $this->element('ratings', array('gameId' => $game['Game']['id'])); ?>
	</div>
	<div id="boxFloat" style="background-color:#3F3829; height:90px; margin-top:10px; margin-left:5px; text-align:center; width:70px;">
		<?php 
			echo $this->Html->link($this->Html->image('replay.png'), '/games/download/' . $game['Game']['id'], array('escape'=>false));
		?><br><br>Downloads:<br>
		<b><?php echo $game['Game']['downloads']; ?></b>
	</div>
	<?php if($logged_in): ?>
		<div id="box" style="height:90px; text-align:center; background-color:#2F2B23; margin-left:352px">
			<br><?php 
				echo $this->Form->button('Write Quick Comment', array(
					'id' => 'commentQuick',
					'style' => 'width:190px; margin:0px 15px'
				)) . '<br><br>';
				echo $this->Form->button('Write Advanced Comment', array(
					'id' => 'commentAdvanced',
					'style' => 'width:190px; margin:0px 15px',
					'onClick' => 'window.location.href=\'/comments/add/' . $game['Game']['id'] . '\''
				));
			?>
		</div>
		<div id="commentBox" style="display:none;">
			<?php echo $this->Form->textarea('Comment', array('style' => 'width:100%; height:100px')); ?>
			<?php 
				echo $this->Form->submit('Submit Comment', array(
					'style' => 'margin-left:599px; width:130px',
					'id' => 'submitComment',
					'onClick' => 'submitComment(' . $game['Game']['id'] . ', this.value)'
				)); 
			?>
		</div>
	<?php else: ?>
		<div id="box" style="height:90px; text-align:center; background-color:#2F2B23; margin-left:235px">
			Log in to comment this game.
		</div>
	<?php endif; ?>
	<div id="commentsList">
		<?php echo $this->element('comments', array('gameId' => $game['Game']['id'])); ?>
	</div>
</div>