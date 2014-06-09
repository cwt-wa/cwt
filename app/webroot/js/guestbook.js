$(document).ready(function () {
  var update = function () {
    $.ajax({
      url: '/infoboards/show/4',
      beforeSend: function () {
        $('#board').html('<div id="loading"><img src="/img/loading.gif"></div>');
      },
      success: function (result) {
        $('#board').html(result);
      }
    });
  }

  $('#guest').css('border-style', 'inset');
  $('#guest').css('border-bottom', 'none');
  category = 4;
  update();

  // The "Help" button.
  $('.help_item').click(function () {
    $('#board').html('Please log in.');
    clearInterval(refresh);
  });

  // User is typing. Mainly this is for the nick suggestions.
  $('#message').keypress(function (e) {
    // Return alias message submitted.
    if (e.which == 13) {
      var guest = prompt('Your nickname:'); // Ask for nick.

      // if no nick was given return to category "All".
      if (guest == null
        || guest == ''
        || guest.length > 16
        || guest.length < 3
        || guest.match(/[^-A-Za-z0-9]/g) != null) {
        alert('Your nickname may only contain letters, numbers and a hyphen and must not be longer than 16 and shorter than 3 characters.');

        var guest = 'undefined';
      } else {
        $.ajax({
          url: '../infoboards/submit/',
          type: 'POST',
          data: {
            'message': $('#message').val(),
            'guest': guest
          },
          success: function (result) {
            $('#message').val('');
            $('#message').focus();
          }
        });

        update();
      }
    }
  });

  // Don't auto refresh the IB when user's mouse is on it.
  $('#board').mouseenter(function () {
    clearInterval(refresh);
  });

  // Now you can go on.
  $('#board').mouseleave(function () {
    refresh = setInterval('refreshIB()', 5000);
  });
});

/*
 *  AUTO REFRESH
 */

function refreshIB() {
  $.ajax({
    url: '../infoboards/show/' + category,
    success: function (result) {
      $('#board').html(result);
    }
  });
}

refresh = setInterval('refreshIB()', 5000); // Every 5 seconds.
