function action(action, user) {
	$('#' + action).toggle('fast');

	$.ajax({
		url: '../users/' + action + '/' + user,
		beforeSend: function() {
			$('#' + action).html('<div id="loading"><img src="img/loading.gif"></div>');
		},
		success: function(result) {
			$('#' + action).html(result);
		}
	});
}