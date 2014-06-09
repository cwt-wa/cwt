$(document).ready(function () {
  var update = function (category) {
    $.ajax({
      url: '/infoboards/show/' + category,
      beforeSend: function () {
        $('#board').html('<div style="text-align: center; margin: 30px auto;"><img src="/img/loading.gif"></div>');
      },
      success: function (result) {
        $('#board').html(result);
      }
    });
  }

  var categories = function (selector) {
    $('#all').css('border-style', 'outset');
    $('#shoutbox').css('border-style', 'outset');
    $('#pm').css('border-style', 'outset');
    $('#tourney').css('border-style', 'outset');
    $('#guest').css('border-style', 'outset');
    $('#admin').css('border-style', 'outset');

    $('#all').css('border-bottom', 'none');
    $('#shoutbox').css('border-bottom', 'none');
    $('#pm').css('border-bottom', 'none');
    $('#tourney').css('border-bottom', 'none');
    $('#guest').css('border-bottom', 'none');
    $('#admin').css('border-bottom', 'none');

    $(selector).css('border-style', 'inset');
    $(selector).css('border-bottom', 'none');
  }

  // New category clicked.
  $('#all, #shoutbox, #pm, #tourney, #guest, #admin').click(function () {
    switch ($(this).attr('id')) {
      case 'all':
        category = 0;
        break;
      case 'shoutbox':
        category = 1;
        break;
      case 'pm':
        category = 2;
        break;
      case 'tourney':
        category = 3;
        break;
      case 'guest':
        category = 4;
        break;
      case 'admin':
        category = 6;
        break;
    }

    categories(this);
    update(category);
  });

  $('#all').click(); // Standard category is "All".

  // The "Help" button.
  $('.help_item').click(function () {
    $('#board').html($('#help_text').html());
    clearInterval(refresh);
  });

  /*
   keys['user'] = {};
   keys['user']['at'] = 64; // which
   keys['user']['A'] = 65; // which
   keys['user']['Z'] = 90; // which
   keys['user']['a'] = 97; // which
   keys['user']['z'] = 122; // which
   keys['user']['0'] = 48; // which
   keys['user']['9'] = 57; // which
   keys['user']['hyph'] = 45; // which
   keys['symbol'] = {};
   keys['symbol']['period'] = 46; // which
   keys['symbol']['comma'] = 44; // which
   keys['symbol']['excl'] = 33; // which
   keys['symbol']['quest'] = 63; // which
   keys['symbol']['colon'] = 58; // which
   keys['symbol']['space'] = 32; // which
   keys['action'] = {};
   keys['action']['left'] = 37; // keyCode
   keys['action']['right'] = 39; // keyCode
   keys['action']['backsp'] = 8; // keyCode, which
   keys['action']['return'] = 13; // keyCode, which
   */

  counter = false; // False when there's no nick being typed.

  // User is typing. Mainly this is for the nick suggestions.
  $('#message').keypress(function (e) {
    // User has already started typing a nick.
    if (counter !== false) {
      // User goes on typing a username
      // Upper- and lowercase letters, numbers or hyphen.
      if ((e.which >= 65 && e.which <= 90)
        || (e.which >= 97 && e.which <= 122)
        || (e.which >= 48 && e.which <= 57)
        || (e.which == 45)) {
        counter++;
      } else if (e.which == 8) {
        counter--;

        // When the user has backspaced more
        // than the length of the nick.
        if (counter <= 0) {
          counter = false;
        }
      } else {
        counter = false;
      }
    } else if (e.which == 64) {
      counter = 0; // When an @ is typed it all starts.
    }

    // Return alias message submitted.
    if (e.which == 13) {
      if (category != 4) {
        var guest = 'undefined';
      } else {
        var guest = prompt('Your nickname:'); // Ask for nick.

        // if no nick was given return to category "All".
        if (guest == null
          || guest == ''
          || guest.length > 16
          || guest.length < 3
          || guest.match(/[^-A-Za-z0-9]/g) != null) {
          alert('Your nickname may only contain letters, numbers and a hyphen and must not be longer than 16 and shorter than 3 characters.');

          var guest = 'undefined';
          return;
        }
      }

      $.ajax({
        url: '/infoboards/submit/',
        type: 'POST',
        data: {
          'message': $('#message').val(),
          'guest': guest
        },
        beforeSend: function () {
          $('#board').html('<div style="text-align: center; margin: 30px auto;"><img src="/img/loading.gif"></div>');
        },
        success: function (result) {
          // That means it's a PM with an invalid recipient.
          if (result != '') {
            $('#nick_suggest').html(result);
            $('#nick_suggest').slideDown('fast');
          } else {
            $('#message').val('');
            $('#message').focus();
            counter = false;
            update(category);
          }
        }
      });
    }
  });

  $('#message').keyup(function () {
    if (counter > 0) {
      // Differ between end of input and within input string.
      if ($('#message').caret().start == $('#message').val().length) {
        var str = $('#message').val();
      } else {
        var str = $('#message').val().substring(0, $('#message').caret().start);
      }

      $.ajax({
        url: '/infoboards/nick_suggest',
        type: 'POST',
        data: {
          'str': str,
          'count': counter
        },
        success: function (result) {
          $('#nick_suggest').html(result);
        }
      });

      $('#nick_suggest').slideDown('fast');
    } else {
      $('#nick_suggest').slideUp('fast');
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

function inputNick(nick) {
  if ($('#message').caret().start == $('#message').val().length) {
    var str = $('#message').val();
    var end = '';
  } else {
    var str = $('#message').val().substring(0, $('#message').caret().start);
    var end = $('#message').val().substring($('#message').caret().start);
  }

  var clength = str.length - counter;
  var reduced = str.substring(0, clength);
  var completed = reduced + nick + end;

  $('#message').val(completed);
  $('#nick_suggest').slideUp('fast');
  $('#message').focus();
}

/*
 *  AUTO REFRESH
 */

function refreshIB() {
  $.ajax({
    url: '/infoboards/show/' + category,
    success: function (result) {
      $('#board').html(result);
    }
  });
}

refresh = setInterval('refreshIB()', 10000);
