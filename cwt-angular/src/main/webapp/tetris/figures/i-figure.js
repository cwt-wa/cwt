"use strict";
var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
var IFigure = /** @class */ (function (_super) {
    __extends(IFigure, _super);
    function IFigure() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    IFigure.prototype.createFigure = function () {
        var cells = new Array();
        var middleOfGrid = Math.ceil(grid.getNumberOfCellsHorizontal() / 2);
        for (var i = 0; i < 4; i++) {
            cells.push(new Cell(middleOfGrid - 1, i, this.getColor()));
        }
        return cells;
    };
    IFigure.prototype.rotateFigure = function (clone) {
        var counter;
        switch (this.getRotateCounter()) {
            case 0:
                counter = clone.cells[0].xPos - 2;
                for (var i = 0; i < this.getCells().length; i++, counter++) {
                    this.getCells()[i].setYPos(clone.cells[clone.indexOfHighestYPosition - 1].yPos);
                    this.getCells()[i].setXPos(counter);
                }
                break;
            case 1:
                counter = 2;
                for (var i = 0; i < this.getCells().length; i++, counter--) {
                    this.getCells()[i].setXPos(clone.cells[clone.indexOfHighestXPosition - 1].xPos);
                    this.getCells()[i].setYPos(clone.cells[i].yPos - counter);
                }
                this.setRotateCounter(-1);
                break;
        }
    };
    return IFigure;
}(Figure));
