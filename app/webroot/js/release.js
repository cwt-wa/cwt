$(document).ready(function () {
  $.ajax({
    url: 'https://img.shields.io/github/release/Zemke/cwt.json',
    type: 'GET',
    success: function (res) {
      console.log(res.value);
      $('#release-tag').html(res.value);
    }
  });
});
