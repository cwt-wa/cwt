$(document).ready(function () {
    var allTargets = document.querySelectorAll('.highlight');

    var i = 0;
    for (; i < allTargets.length; i++) {
        var target = allTargets[i];

        target.addEventListener('mouseenter', function (e) {
            var allTargetsOfUser = getAllTargetsOfUser(e.target);
            highlightAllTargetsOfUser(allTargetsOfUser, true);
        });
        target.addEventListener('mouseleave', function (e) {
            var allTargetsOfUser = getAllTargetsOfUser(e.target);
            highlightAllTargetsOfUser(allTargetsOfUser, false);
        });
    }

    function getAllTargetsOfUser(elem) {
        var j = 0;
        for (; j < elem.classList.length; j++) {
            if (elem.classList[j].indexOf('user') === -1) {
                continue;
            }

            return document.querySelectorAll('.' + elem.classList[j]);
        }

        return [];
    }

    function highlightAllTargetsOfUser(elems, enable) {
        var j = 0;
        for (; j < elems.length; j++) {
            elems[j].style.backgroundColor = !!enable ? '#887059' : '';
        }
    }
});
