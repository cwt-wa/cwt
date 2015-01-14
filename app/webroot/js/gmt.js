// Anytime Anywhere Web Page Clock Generator
// Clock Script Generated at
// http://www.rainbow.arch.scriptmania.com/tools/clock

function timeSource() {
  x = new Date(timeNow().getUTCFullYear(), timeNow().getUTCMonth(), timeNow().getUTCDate(), timeNow().getUTCHours(), timeNow().getUTCMinutes(), timeNow().getUTCSeconds());
  x.setTime(x.getTime() + 0);
  return x;
}
function timeNow() {
  return new Date();
}
function leadingZero(x) {
  return (x > 9) ? x : '0' + x;
}
function dateEnding(x) {
  return '';
}
function displayTime() {
  if (fr == 0) {
    fr = 1;
    document.write('<span id="tP">' + eval(outputTime) + '</span>');
  }
  document.getElementById('tP').innerHTML = eval(outputTime);
  setTimeout('displayTime()', 1000);
}
function fixYear4(x) {
  return (x < 500) ? x + 1900 : x;
}
var dayNames = new Array('Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday');
var monthNames = new Array('January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December');
var fr = 0;
var outputTime = "dayNames[timeSource().getDay()]+','+' '+monthNames[timeSource().getMonth()]+' '+timeSource().getDate()+dateEnding(timeSource().getDate())+','+' '+fixYear4(timeSource().getYear())+', '+leadingZero(timeSource().getHours())+':'+leadingZero(timeSource().getMinutes())+':'+leadingZero(timeSource().getSeconds())+' '";
