$(document).ready(function () {
  $('#willing').toggle(function () {
    q = $(this).html();
    $(this).html('Close');
    $('#addStream').slideDown('fast');
  }, function () {
    $(this).html(q);
    $('#addStream').slideUp('fast');
  });

  $('#color1, #color2, #color3, #color4, #color5').click(function () {
    $('#color1').css('border', 'none');
    $('#color2').css('border', 'none');
    $('#color3').css('border', 'none');
    $('#color4').css('border', 'none');
    $('#color5').css('border', 'none');
    $(this).css('border', '1px solid red');
    $('#box').css('background-color', $(this).css('background-color'));
    $('#StreamColor').val($(this).css('background-color'));
  });
});
