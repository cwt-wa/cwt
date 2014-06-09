$(document).ready(function () {
  $('#ReportAddForm').change(function () {
    var userScore = $('#ReportUserScore').val();
    var opponentScore = $('#ReportOpponentScore').val();
    var user = $('#ReportUser option:selected').text();
    var opponent = $('#ReportOpponent option:selected').text();
    var replays = $('#ReportReplays').val();
    var allowedResults = [
      '3-0', '3-1', '3-2', '0-3', '1-3', '2-3',
      '4-0', '4-1', '4-2', '4-3', '0-4', '1-4', '2-4', '3-4'];

    if (userScore != '' && opponentScore != '' && opponent != '' && replays != '') {
      if ($.inArray(userScore + '-' + opponentScore, allowedResults) === -1) {
        $('#preview').html('Invalid result.');
        $('#reportSubmit').attr('disabled', 'disabled');
      } else if (replays.substring(replays.length - 4) != '.rar'
        && replays.substring(replays.length - 4) != '.zip') {
        $('#preview').html('Only RAR or Zip files.');
        $('#reportSubmit').attr('disabled', 'disabled');
      } else {
        $('#preview').html(user + ' ' + userScore + '-' + opponentScore + ' ' + opponent);
        $('#reportSubmit').removeAttr('disabled');
      }
    } else {
      $('#reportSubmit').attr('disabled', 'disabled');
    }
  });

  $('#ReportAddForm').submit(function () {
    var userScore = $('#ReportUserScore').val();
    var opponentScore = $('#ReportOpponentScore').val();
    var user = $('#ReportUser option:selected').text();
    var opponent = $('#ReportOpponent option:selected').text();

    if (confirm('Are you sure?\n' + user + ' ' + userScore + '-' + opponentScore + ' ' + opponent)) {
      $('#report').css('visibility', 'hidden');
      $('#report').css('position', 'absolute');
      $('#stages').append('<div id="progress"><i>Game is being reported...</i></div>');
      return true;
    }
    return false;
  });
});
