$(document).ready(function() {
	$('#size, #color').change(function() {
		$(this).val('standard')
	});

	$('#text').focus();

	$('#toolbar').click(function() {
		$('#text').focus();
	});

	$('#margin').change(function() {
		$('#text').focus();
	});
});

function preview(obj, path) {
	$.ajax({
		url: path,
		type: 'POST',
		data: {
			'text': document.getElementById(obj).value,
			'action': 'preview'
		},
		beforeSend: function() {
			$('#show_preview').html('<div id="loading"><img src="/img/loading.gif"></div>');
		},
		success: function(result) {
			$('#show_preview').html('<div id="box" style="background-color:#3F3429">' + result + '</div>');
		}
	});
}

function submit(obj, path, redirect) {
	$.ajax({
		url: path,
		type: 'POST',
		data: {
			'text': $('#text').val(),
			'action': 'submit'
		},
		beforeSend: function() {
			BBsubmit = $('#BBsubmit').detach();

			$('.submitArea').html('<img style="margin-bottom:-6px" id="progressbar" src="/img/loading.gif">');
		},
		success: function(result) {
			$('.submitArea').html('<img style="margin-bottom:-6px" id="progressbar" src="/img/tick.png">'); 

			window.setTimeout(function() {$('.submitArea').html(BBsubmit)}, 5000);

			if(redirect) {
				window.location.href = redirect;
			}
		}
	});
}

function doImage(obj) {
	textarea = document.getElementById(obj);
	var url = prompt('Enter the Image URL:','http://');
	var scrollTop = textarea.scrollTop;
	var scrollLeft = textarea.scrollLeft;

	if(url != '' && url != null) {
		if(document.selection) {
			textarea.focus();
			var sel = document.selection.createRange();
			sel.text = '[img]' + url + '[/img]';
		} else {
			var len = textarea.value.length;
	    	var start = textarea.selectionStart;
			var end = textarea.selectionEnd;
			
        	var sel = textarea.value.substring(start, end);
	    	//alert(sel);
			var rep = '[img]' + url + '[/img]';
        	textarea.value =  textarea.value.substring(0,start) + rep + textarea.value.substring(end,len);
			
				
			textarea.scrollTop = scrollTop;
			textarea.scrollLeft = scrollLeft;
		}
	}
}

function doURL(obj) {
	textarea = document.getElementById(obj);
	var url = prompt('Enter the URL:','http://');
	var scrollTop = textarea.scrollTop;
	var scrollLeft = textarea.scrollLeft;

	if(url != '' && url != null) {
		if(document.selection) {
			textarea.focus();
			var sel = document.selection.createRange();
					
			if(sel.text=="") {
				sel.text = '[url]'  + url + '[/url]';
			} else {
				sel.text = '[url=' + url + ']' + sel.text + '[/url]';
			}			

			//alert(sel.text);
				
		} else {
			var len = textarea.value.length;
		    var start = textarea.selectionStart;
			var end = textarea.selectionEnd;
			
	        var sel = textarea.value.substring(start, end);
			
			if(sel=="") {
				var rep = '[url]' + url + '[/url]';
			} else {
				var rep = '[url=' + url + ']' + sel + '[/url]';
			}

		    //alert(sel);
			
	        textarea.value =  textarea.value.substring(0,start) + rep + textarea.value.substring(end,len);
						
			textarea.scrollTop = scrollTop;
			textarea.scrollLeft = scrollLeft;
		}
	}
}

function doAddTags(tag1, tag2, obj) {
	textarea = document.getElementById(obj);
	// Code for IE
	if(document.selection) {
		textarea.focus();
		var sel = document.selection.createRange();
		//alert(sel.text);
		sel.text = tag1 + sel.text + tag2;
	} else { // Code for Mozilla Firefox
		var len = textarea.value.length;
	    var start = textarea.selectionStart;
		var end = textarea.selectionEnd;
		
		
		var scrollTop = textarea.scrollTop;
		var scrollLeft = textarea.scrollLeft;

		
        var sel = textarea.value.substring(start, end);
	    //alert(sel);
		var rep = tag1 + sel + tag2;
        textarea.value =  textarea.value.substring(0,start) + rep + textarea.value.substring(end,len);
		
		textarea.scrollTop = scrollTop;
		textarea.scrollLeft = scrollLeft;		
	}
}

function doList(tag1, tag2, obj){
	textarea = document.getElementById(obj);
	// Code for IE
	if(document.selection) {
		textarea.focus();
		var sel = document.selection.createRange();
		var list = sel.text.split('\n');
		
		for(i=0;i<list.length;i++) {
			list[i] = '[li]' + list[i] + '[/li]';
		}
		//alert(list.join("\n"));
		sel.text = tag1 + '\n' + list.join("\n") + '\n' + tag2;
	} else { // Code for Firefox
		var len = textarea.value.length;
	    var start = textarea.selectionStart;
		var end = textarea.selectionEnd;
		var i;
		
		var scrollTop = textarea.scrollTop;
		var scrollLeft = textarea.scrollLeft;

		
        var sel = textarea.value.substring(start, end);
	    //alert(sel);
		
		var list = sel.split('\n');
		
		for(i=0;i<list.length;i++) {
			list[i] = '[li]' + list[i] + '[/li]';
		}
		
		//alert(list.join("<br>"));
        	
		var rep = tag1 + '\n' + list.join("\n") + '\n' +tag2;
		textarea.value =  textarea.value.substring(0,start) + rep + textarea.value.substring(end,len);
		
		textarea.scrollTop = scrollTop;
		textarea.scrollLeft = scrollLeft;
 	}
}