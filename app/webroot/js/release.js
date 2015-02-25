$(document).ready(function () {
  $.ajax({
    url: 'https://img.shields.io/github/release/Zemke/cwt.json?nocache=' + $.now(),
    type: 'GET',
    cache: false,
    success: function (res) {
      $('#release-tag').html(res.value);
    }
  });
});
