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
var TFigure = /** @class */ (function (_super) {
    __extends(TFigure, _super);
    function TFigure(color) {
        var _this = _super.call(this, color) || this;
        _this.centerIndex = 0;
        _this.initCenterIndex();
        return _this;
    }
    TFigure.prototype.createFigure = function () {
        var cells = new Array();
        var middleOfGrid = Math.ceil(grid.getNumberOfCellsHorizontal() / 2);
        cells.push(new Cell(middleOfGrid, 0, this.getColor()));
        cells.push(new Cell(middleOfGrid - 1, 1, this.getColor()));
        cells.push(new Cell(middleOfGrid, 1, this.getColor()));
        cells.push(new Cell(middleOfGrid + 1, 1, this.getColor()));
        return cells;
    };
    TFigure.prototype.initCenterIndex = function () {
        this.setCenterIndex(0);
    };
    TFigure.prototype.rotateFigure = function (clone) {
        switch (this.getRotateCounter()) {
            case 0:
                this.changePositions(clone.cells[clone.indexOfHighestYPosition], clone.cells[clone.indexOfSmallestXPosition], 0, undefined);
                break;
            case 1:
                this.changePositions(clone.cells[clone.indexOfSmallestYPosition], clone.cells[clone.indexOfSmallestXPosition], undefined, 2);
                break;
            case 2:
                this.changePositions(clone.cells[clone.indexOfSmallestYPosition], clone.cells[clone.indexOfHighestXPosition], 2, undefined);
                break;
            case 3:
                this.changePositions(clone.cells[clone.indexOfHighestYPosition], clone.cells[clone.indexOfHighestXPosition], undefined, 0);
                this.setRotateCounter(-1);
                break;
        }
    };
    TFigure.prototype.changePositions = function (cloneY, cloneX, yCounter, xCounter) {
        var calcXCounter = true;
        var calcYCounter = true;
        if (xCounter == undefined) {
            xCounter = 0;
            calcXCounter = false;
        }
        if (yCounter == undefined) {
            yCounter = 0;
            calcYCounter = false;
        }
        for (var i = 0; i < this.getCells().length; i++) {
            if (i == this.getCenterIndex())
                continue;
            this.getCells()[i].setYPos(cloneY.yPos + yCounter);
            this.getCells()[i].setXPos(cloneX.xPos + xCounter);
            if (calcXCounter)
                xCounter--;
            if (calcYCounter)
                yCounter--;
        }
    };
    TFigure.prototype.setCenterIndex = function (index) {
        this.centerIndex = index;
    };
    TFigure.prototype.getCenterIndex = function () {
        return this.centerIndex;
    };
    return TFigure;
}(Figure));
