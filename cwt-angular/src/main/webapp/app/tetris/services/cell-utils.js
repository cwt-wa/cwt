"use strict";
var CellUtils = /** @class */ (function () {
    function CellUtils() {
    }
    CellUtils.calculateIndexOfHighestYPosition = function (cells) {
        var indexOfHighestYPosition = 0;
        for (var i = 0; i < cells.length; i++) {
            if (cells[i].getYPos() > cells[indexOfHighestYPosition].getYPos()) {
                indexOfHighestYPosition = i;
            }
        }
        return indexOfHighestYPosition;
    };
    CellUtils.calculateIndexOfSmallestYPosition = function (cells) {
        var indexOfSmallestYPosition = 0;
        for (var i = 0; i < cells.length; i++) {
            if (cells[i].getYPos() < cells[indexOfSmallestYPosition].getYPos()) {
                indexOfSmallestYPosition = i;
            }
        }
        return indexOfSmallestYPosition;
    };
    CellUtils.calculateIndexOfSmallestXPosition = function (cells) {
        var indexOfSmallestXPosition = 0;
        for (var i = 0; i < cells.length; i++) {
            if (cells[i].getXPos() < cells[indexOfSmallestXPosition].getXPos()) {
                indexOfSmallestXPosition = i;
            }
        }
        return indexOfSmallestXPosition;
    };
    CellUtils.calculateIndexOfGreatestXPosition = function (cells) {
        var indexOfGreatestXPosition = 0;
        for (var i = 0; i < cells.length; i++) {
            if (cells[i].getXPos() > cells[indexOfGreatestXPosition].getXPos()) {
                indexOfGreatestXPosition = i;
            }
        }
        return indexOfGreatestXPosition;
    };
    return CellUtils;
}());
