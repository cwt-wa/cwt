$(document).ready(function () {
  var liveStreamNotifications = function () {
    $.ajax({
      url: '/streams/live_stream_notification',
      success: function (res) {
        $('#liveStreamNotification').html(res)
      }
    });
  };

  liveStreamNotifications();
  setInterval(function () {
    liveStreamNotifications();
  }, 60000); // every minute
});
