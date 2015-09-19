function findStreams(gameId) {
  $.ajax({
    url: '/games/findStreams/' + gameId,
    //url: 'http://localhost:8080/json.json',
    type: 'GET',
    success: function (res) {
      res = JSON.parse(res);

      if (!res.length) {
        $('#find-streams').html('Sorry, I couldn\'t find any recorded live streams.<br/> That doesn\'t necessarily mean that there are none, I just wasn\'t able to find one.');
        return;
      }

      if (res.length === 1) {
        $('#find-streams').html('Okay, I\'ve found just one stream. This should hopefully be the one stream you were looking for...');
      } else if (res.length > 1) {
        $('#find-streams').html('Hmm... I\'ve found several streams that could potentially be streams of this game. You need to find out yourself...');
      }


      $('#find-streams').html($('#find-streams').html() + '<br/>');
      $('#find-streams').html($('#find-streams').html() + '<ul>');

      var i;
      for (i = 0; i < res.length; i++) {
        $('#find-streams').html($('#find-streams').html() + '<li><a href="/streams/view/' + res[i]._id + '" target="_blank">' + res[i].title + '</a></li>');
      }

      $('#find-streams').html($('#find-streams').html() + '</ul>');
    }
  });
}

function rate(rating, gameId) {
  $.ajax({
    url: '/ratings/add/',
    type: 'POST',
    data: {
      'rating': rating,
      'gameId': gameId
    },
    beforeSend: function () {
      if (rating == 'like' || rating == 'dislike') {
        $('.R').html('<div id="progress"><img src="/img/loading.gif"></div>');
      } else { // Dark-/Lightside
        $('.C').html('<div id="progress"><img src="/img/loading.gif"></div>');
      }
    },
    success: function () {
      $.ajax({
        url: '/ratings/view/' + gameId,
        success: function (result) {
          $('#ratings').html(result);
        }
      });
    }
  });
}

function submitComment(gameId) {
  $.ajax({
    url: '/comments/add/' + gameId,
    type: 'POST',
    data: {
      'text': $('#Comment').val(),
      'action': 'submit'
    },
    beforeSend: function () {
      submitBtn = $('#submitComment').detach();
      $('.submit').html('<div style="margin-left:599px; width:130px"><img height="15" width="15" src="/img/loading.gif"></div>');
    },
    success: function () {
      $.ajax({
        url: '/comments/view/' + gameId,
        success: function (result) {
          $('#commentBox').slideUp('slow', function () {
            $('.submit').html(submitBtn);
            $('#Comment').val('');

            $('#commentQuick').click();

            $('#commentsList').html(result);
          });
        }
      });
    }
  });
}

function writeAdvancedComment(gameId) {
  var currentCommentValue = $('#Comment').val();
  window.sessionStorage.setItem('currentCommentValue', currentCommentValue);
  window.location.href = '/comments/add/' + gameId;
}

function editComment(commentId) {
  window.location.href = '/comments/edit/' + commentId;
}