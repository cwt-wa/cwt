$(document).ready(function() {	
	/*// Hover with JavaScript
	$('#up_apply.hover, #up_report.hover, #up_user.hover, #up_stream.hover').hover(function() {
		$(this).css('background-color', '#5D4D3C');
		$(this).css('cursor', 'pointer');
	}, function() {
		$(this).css('background-color', '#2F251E');
		$(this).css('cursor', 'default');
	});	*/

	/*
	 * Bunch of Togglers.
	 */

	 $(window).click(function(event) {
        var menu_items = ['#up_apply', '#up_report', '#up_user', '#up_stream'];

        if(!$.inArray(event.target.id, menu_items)) {
        	$.each(menu_items, function(key, val) { 
			 	$(val).fadeOut('slow'); 
			});
        }  
    });

	// User tab.
	$('#up_user').toggle(function() {
		$(this).css("background-color","#5D4D3C");
		$('#generaluser').slideDown('slow');
	}, function() {
		$(this).css("background-color","#2F251E");
		$('#generaluser').slideUp('middle');
	});

	// Report a Game tab.
	$('#up_report').toggle(function() {
		$(this).css("background-color","#5D4D3C");
		$('#report').slideDown('slow');

		$.ajax({
			url: '/games/add',
			beforeSend: function() {
				$('#report').html('<div id="progress"><img src="/img/loading.gif"></div>');
			},
			success: function(result) {
				$('#report').html(result);
			}
		});
	}, function() {
		$(this).css("background-color","#2F251E");
		$('#report').slideUp('middle');
	});

	// Entertaining Stream tab.
	$('#up_stream').toggle(function() {
		$(this).css("background-color","#5D4D3C");
		$('#stream').slideDown('slow');
	}, function() {
		$(this).css("background-color","#2F251E");
		$('#stream').slideUp('middle');
	});
});