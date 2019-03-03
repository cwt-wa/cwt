"use strict";
var Cell = /** @class */ (function () {
    function Cell(xPos, yPos, color) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.color = color;
    }
    Cell.prototype.getXPos = function () {
        return this.xPos;
    };
    Cell.prototype.getYPos = function () {
        return this.yPos;
    };
    Cell.prototype.setYPos = function (yPos) {
        this.yPos = yPos;
    };
    Cell.prototype.setXPos = function (xPos) {
        this.xPos = xPos;
    };
    Cell.prototype.setColor = function (color) {
        this.color = color;
    };
    Cell.prototype.getColor = function () {
        return this.color;
    };
    Cell.WIDTH = 40;
    return Cell;
}());
