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
