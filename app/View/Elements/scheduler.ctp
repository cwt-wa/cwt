<div id="SchedulerWrapper">
<?php
	$schedules = $this->requestAction('/schedules/index/');
	$this->Helpers->load('Time');
	$days = $schedules['datetimes']['days'];
	$times = $schedules['datetimes']['times'];
	$opponents = $schedules['opponents'];
	unset($schedules['datetimes'], $schedules['opponents']);
?>
<script type="text/javascript">
	$(document).ready(function() {
		$('#SubmitSchedule').click(function() {
			when = $('#ScheduleDay').val() + ' ' + $('#ScheduleTime').val();

			$.ajax({
				url: '/schedules/add',
				type: 'POST',
				data: {
					'when': when,
					'home_id': $('#ScheduleHomeId').val(),
					'away_id': $('#ScheduleAwayId').val()
				},
				beforeSend: function() {
					$('.ScheduleList').html($('#ProcessScheduleAdd'));
					$('#ProcessScheduleAdd').fadeIn(0);;
				},
				success: function(result) {
					$('#SchedulerWrapper').html(result);
				}
			});
		});
	});

	function removeGame(id) {
		if(confirm("Are you sure?")) {
			$.ajax({
				url: '/schedules/delete/' + id,
				success: function(result) {
					$('#SchedulerWrapper').html(result);
				}
			});
		}
	}
</script>

<div id="boxFloat" class="ScheduleList" style="background-color:#3C3E28; width:475px; height:100px; overflow-y:scroll; padding: 2px 5px;">
	<div id="ProcessScheduleAdd" style="display:none; text-align:center; margin-top:25px;">
		<?php
			echo $this->Html->image('loading.gif');
		?>
	</div>
	<table border="0" cellpadding="2" cellspacing="0" align="center">
		<?php if(@$schedules[0]): ?>	
			<?php foreach($schedules as $schedule): ?>
				<tr>
					<td align="right">
						<?php if($schedule['Stream'][0]['online']): ?>
							<blink>
						<?php endif; ?>
						<?php if($current_user['id'] == $schedule['Scheduler']['id']): ?>
							<span title="Remove Game" style="color:red; cursor:pointer;" onClick="removeGame(<?php echo $schedule['Schedule']['id'] ?>);">
								x
						<?php else: ?>
							»
						<?php endif; ?>
						<?php if($schedule['Stream'][0]['online']): ?>
							</blink>
						<?php endif; ?>
					</td>
					<td align="right">
						<?php if($schedule['Stream'][0]['online']): ?>
							<blink>
						<?php endif; ?>
						<?php 
							echo $this->Time->format(
								'M j, H:i', $schedule['Schedule']['when']); 
						?>
						<?php if($schedule['Stream'][0]['online']): ?>
							</blink>
						<?php endif; ?>
					</td>
					<td align="right">
						<?php if($schedule['Stream'][0]['online']): ?>
							<blink>
						<?php endif; ?>
						<?php 
							echo $this->Html->link(
								$schedule['Scheduler']['username'],
								'/users/view/' . $schedule['Scheduler']['id'])
						?>
						<?php if($schedule['Stream'][0]['online']): ?>
							</blink>
						<?php endif; ?>
					</td>
					<td align="center">
						<?php if($schedule['Stream'][0]['online']): ?>
							<blink>
						<?php endif; ?>
						vs.
						<?php if($schedule['Stream'][0]['online']): ?>
							</blink>
						<?php endif; ?>
					</td>
					<td align="left">
						<?php if($schedule['Stream'][0]['online']): ?>
							<blink>
						<?php endif; ?>
						<?php 
							echo $this->Html->link(
								$schedule['Scheduled']['username'],
								'/users/view/' . $schedule['Scheduled']['id'])
						?>
						<?php if($schedule['Stream'][0]['online']): ?>
							</blink>
						<?php endif; ?>
					</td>
					<td align="left">
						<?php foreach($schedule['Stream'] as $stream): ?>
							<?php
								echo $this->Html->link(
									$this->Html->image('popcorn.png', array(
										'style' => 'height:17px; width:auto;',
										'alt' => $stream['title'],
										'title' => $stream['title']
									)),
									'/streams/view/' . $stream['id'], array(
										'escape' => false
									)
								);
							?>
						<?php endforeach; ?>
					</td>
				</tr>
			<?php endforeach; ?>
		<?php else: ?>
			<div style="margin-left:40px; margin-top:30px; font-style:italic;">
				No games have been scheduled.
			</div>
		<?php endif; ?>
	</table>
</div>

<div id="box" style="background-color:#3C3E28; margin-left:500px; padding-top:5px; width:438px; height:80px; text-align:center;">
	<?php if($logged_in): ?>
		<?php if($current_user['stage'] != $tourney['status']): ?>
			<div style="margin-left:30px; margin-top:27px; font-style:italic; text-align:left;">
				Hello <?php echo $current_user['username'] ?>, CWT is currently running.
				<br>I hope you will be taking part next year.
			</div>
		<?php elseif($opponents): ?>
			<?php
				echo $this->Form->create('Schedule', array(
					'inputDefaults' => array(
						'div' => false,
						'label' => false
					)
				));
			?>
			<table border="0" cellpadding="3" cellspacing="0" align="center">
				<tr>
					<td align="center">
						Day
					</td>
					<td align="center">
						Time
					</td>
					<td align="center">
						You
					</td>
					<td align="center">
						
					</td>
					<td align="center">
						Opponent
					</td>
				</tr>
				<tr>
					<td align="center">
						<?php
							echo $this->Form->select(
								'day',
								$days,
								array(
									'empty' => false
								)
							);
						?>
					</td>
					<td align="center">
						<?php
							echo $this->Form->select(
								'time',
								$times,
								array(
									'empty' => false
								)
							);
						?>
					</td>
					<td align="center">
						<?php
							echo $this->Form->select('home_id', array(
								$current_user['id'] => $current_user['username']
								), array(
									'empty' => false,
									'disabled' => true
								)
							);
						?>
					</td>
					<td align="center">
						vs.
					</td>
					<td align="center">
						<?php
							echo $this->Form->select('away_id', $opponents);
						?>
					</td>
				</tr>
			</table>
			<br>
			<?php
				echo $this->Form->button('Schedule this Game', array(
					'id' => 'SubmitSchedule',
					'type' => 'button'
				));
			?>
			<?php
				echo $this->Form->end();
			?>
		<?php else: ?>
			<div style="margin-left:30px; margin-top:27px; font-style:italic; text-align:left;">
				There are no games for you to schedule at the moment.
			</div>
		<?php endif; ?>
	<?php else: ?>
		<div style="margin-left:30px; margin-top:27px; font-style:italic; text-align:left;">
			Log in, if you want to schedule games.
		</div>
	<?php endif; ?>
</div>

<div style="float:clear;">&nbsp;</div>
</div>