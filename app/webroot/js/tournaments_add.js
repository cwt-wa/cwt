$(document).ready(function () {
  helpers = new Array();
  helpers[0] = $('#take').html();

  addings = 0;
  substractions = 0;

  $('#addhelper').click(function () {
    var take = addings - substractions;
    var bring = take + 1;

    if (helpers[bring]) {
      helpers[bring].appendTo('#bring');
    } else {
      $('#StartHelper0').attr('name', 'data[Start][Helper' + bring + ']');
      $('#StartHelper0').attr('id', 'StartHelper' + bring);

      $('#bring').append($('#take').html());
    }

    $('#take').html(helpers[0]);
    addings++;

    $('#StartNumber').val(take + 1);
  });

  $('#removehelper').click(function () {
    var take = addings - substractions;

    var id = '#' + $('#StartHelper' + take).attr('id');
    helpers[take] = $(id).detach();

    substractions++;

    $('#StartNumber').val(take - 1);
  });
});
