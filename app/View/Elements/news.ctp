<?php
	$news = $this->requestAction('/news/view/');
	$this->Helpers->load('Bbcode');
	$this->Helpers->load('Time');
?>

<?php if($news['News']['text'] != ''): ?>
	<!-- Don't forget that Admin News hint. -->
	<div id="box" style="background-color:#3E283E">
		<div id="adminNews">
			<?php
				echo $this->Html->link(
					$news['User']['username'],
					'/users/view/' . $news['User']['id']
				);
			?>
			<?php
				echo $this->Time->timeAgoInWords($news['News']['modified'], array(
					'format' => 'M j, H:i',
					'end' => '+1 day',
					'accuracy' => array('hour' => 'hour')
				));
			?>
		</div>
		<?php echo nl2br($this->Bbcode->parse($news['News']['text'])); ?>
		
		<!-- <br><br>
		<center>
		<iframe width="640" height="480" src="http://www.youtube.com/embed/11bDkLFkFDo" frameborder="0" allowfullscreen></iframe>
		</center> -->
	</div>
<?php endif; ?>