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
var SFigure = /** @class */ (function (_super) {
    __extends(SFigure, _super);
    function SFigure() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    SFigure.prototype.createFigure = function () {
        var cells = new Array();
        var middleOfGrid = Math.ceil(grid.getNumberOfCellsHorizontal() / 2);
        cells.push(new Cell(middleOfGrid, 0, this.getColor()));
        cells.push(new Cell(middleOfGrid + 1, 0, this.getColor()));
        cells.push(new Cell(middleOfGrid, 1, this.getColor()));
        cells.push(new Cell(middleOfGrid - 1, 1, this.getColor()));
        return cells;
    };
    SFigure.prototype.rotateFigure = function (clone) {
        switch (this.getRotateCounter()) {
            case 0:
                var counterY = -1;
                var counterX = 0;
                for (var i = 0; i < this.getCells().length; i++) {
                    if (clone.cells[i].yPos === clone.cells[clone.indexOfHighestYPosition].yPos)
                        continue;
                    this.getCells()[i].setXPos(clone.cells[clone.indexOfSmallestXPosition].xPos + counterX);
                    this.getCells()[i].setYPos(clone.cells[clone.indexOfHighestYPosition].yPos + counterY);
                    counterY = counterY + 2;
                    counterX++;
                }
                break;
            case 1:
                counterX = 0;
                for (var i = 0; i < this.getCells().length; i++) {
                    if (clone.cells[i].yPos === (clone.cells[clone.indexOfHighestYPosition].yPos - 1))
                        continue;
                    this.getCells()[i].setXPos(clone.cells[clone.indexOfHighestXPosition].xPos + counterX);
                    this.getCells()[i].setYPos(clone.cells[clone.indexOfSmallestYPosition].yPos);
                    counterX++;
                }
                this.setRotateCounter(-1);
                break;
        }
    };
    SFigure.prototype.reset = function (clone) {
        for (var i = 0; i < this.getCells().length; i++) {
            this.getCells()[i].setYPos(clone.cells[i].yPos);
            this.getCells()[i].setXPos(clone.cells[i].xPos);
        }
        if (this.getRotateCounter() == -1) {
            this.setRotateCounter(1);
        }
    };
    return SFigure;
}(Figure));
