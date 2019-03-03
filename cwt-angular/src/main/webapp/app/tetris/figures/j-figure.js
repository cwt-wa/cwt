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
var JFigure = /** @class */ (function (_super) {
    __extends(JFigure, _super);
    function JFigure(color) {
        var _this = _super.call(this, color) || this;
        _this.centerIndex = 0;
        _this.initCenterIndex();
        return _this;
    }
    JFigure.prototype.createFigure = function () {
        var cells = new Array();
        var middleOfGrid = Math.ceil(grid.getNumberOfCellsHorizontal() / 2);
        for (var i = 0; i < 4; i++) {
            if (i == 3) {
                cells.push(new Cell(middleOfGrid - 1, i - 1, this.getColor()));
            }
            else {
                cells.push(new Cell(middleOfGrid, i, this.getColor()));
            }
        }
        return cells;
    };
    JFigure.prototype.initCenterIndex = function () {
        this.setCenterIndex(1);
    };
    JFigure.prototype.rotateFigure = function (clone) {
        switch (this.getRotateCounter()) {
            case 0:
                for (var i = 0; i < this.getCells().length - 1; i++) {
                    if (i === clone.centerIndex)
                        continue;
                    this.getCells()[i].setXPos(clone.cells[clone.indexOfSmallestXPosition].xPos + i);
                    this.getCells()[i].setYPos(clone.cells[clone.indexOfSmallestYPosition].yPos + 1);
                }
                this.getCells()[this.getCells().length - 1].setXPos(clone.cells[clone.cells.length - 1].xPos);
                this.getCells()[this.getCells().length - 1].setYPos(clone.cells[clone.indexOfSmallestYPosition].yPos);
                break;
            case 1:
                for (var i = 0; i < this.getCells().length - 1; i++) {
                    if (i === clone.centerIndex)
                        continue;
                    this.getCells()[i].setXPos(clone.cells[clone.indexOfSmallestXPosition].xPos + 1);
                    this.getCells()[i].setYPos(clone.cells[clone.indexOfSmallestYPosition].yPos + i);
                }
                this.getCells()[this.getCells().length - 1].setXPos(clone.cells[clone.indexOfHighestXPosition].xPos);
                this.getCells()[this.getCells().length - 1].setYPos(clone.cells[clone.cells.length - 1].yPos);
                break;
            case 2:
                for (var i = 0; i < this.getCells().length - 1; i++) {
                    if (i === clone.centerIndex)
                        continue;
                    this.getCells()[i].setXPos(clone.cells[clone.indexOfHighestXPosition].xPos - i);
                    this.getCells()[i].setYPos(clone.cells[clone.indexOfHighestYPosition].yPos - 1);
                }
                this.getCells()[this.getCells().length - 1].setXPos(clone.cells[clone.cells.length - 1].xPos);
                this.getCells()[this.getCells().length - 1].setYPos(clone.cells[clone.indexOfHighestYPosition].yPos);
                break;
            case 3:
                for (var i = 0; i < this.getCells().length - 1; i++) {
                    if (i === clone.centerIndex)
                        continue;
                    this.getCells()[i].setXPos(clone.cells[clone.indexOfHighestXPosition].xPos - 1);
                    this.getCells()[i].setYPos(clone.cells[clone.indexOfHighestYPosition].yPos - i);
                }
                this.getCells()[this.getCells().length - 1].setXPos(clone.cells[clone.indexOfSmallestXPosition].xPos);
                this.getCells()[this.getCells().length - 1].setYPos(clone.cells[clone.cells.length - 1].yPos);
                this.setRotateCounter(-1);
                break;
        }
    };
    JFigure.prototype.setCenterIndex = function (index) {
        this.centerIndex = index;
    };
    JFigure.prototype.getCenterIndex = function () {
        return this.centerIndex;
    };
    return JFigure;
}(Figure));
