$(document).ready(function() {
	counter = 0;
	$('#ApplyAgree').change(function() {
		if(counter % 2 == 0) {
			$('input[type=submit]').removeAttr('disabled');
		} else {
			$('input[type=submit]').attr('disabled', 'disabled');
		}		
		counter++;		
	});
});