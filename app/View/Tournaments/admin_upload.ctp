<script type="text/javascript">
	setInterval(function() {
		var filename = $('.qq-upload-file').html();
		
		if(filename != null) {
			var link =
				'<a href="/files/fms/'+filename+'" target="_blank">'+filename+'</a>';
			var filesize = $('.qq-upload-size').html();

			$('#linkableList').append('<li>'+link+' '+filesize+'</li>');

			$('.qq-upload-success').remove();
		}
	}, 1000);
</script>

<div id="fms">
	<div id="box" style="background-color:#3F2828;">
		<h1 align="center">Admin Files</h1>
	</div>
	<div id="box" style="background-color:#3F2828; text-align:center;">
		You can also upload multiple files at once or take advantage of Drag & Drop!<br>
		Files are saved in http://www.cwtsite.com/files/fms/<br>
	</div>
	<div id="box" style="background-color:#3F2828;">
		<?php
			echo $this->Upload->edit(
				'Tournament', @$this->Form->fields['Tournament.id']);
		?>

		<ul id="linkableList">
		</ul>	
	</div>
</div>