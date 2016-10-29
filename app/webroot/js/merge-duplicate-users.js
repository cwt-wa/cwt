$(document).ready(function () {
    var legacyNumber = 0;
    var draft = $('#draft');

    $('#addLink').click(function (e) {
        e.preventDefault();

        legacyNumber++;
        var newLegacy = draft.clone();
        newLegacy.appendTo('#moreUsers');
        newLegacy.css('display', 'inline');
        newLegacy.attr('name', 'data[Merge][Legacy][' + legacyNumber + ']');
        newLegacy.attr('id', 'MergeLegacy' + legacyNumber);
        var removeLink = $('<a href="" id="remove" class="' + legacyNumber + '" >&nbsp;Remove</a><br>');
        removeLink.insertAfter(newLegacy);
        removeLink.click(function (e) {
            e.preventDefault();

            e.target.previousSibling.remove(); // input[type=select]
            e.target.nextSibling.remove(); // br
            e.target.remove(); // text["Remove"]
        });

    });
});