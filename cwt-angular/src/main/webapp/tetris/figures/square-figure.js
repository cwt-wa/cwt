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
var SquareFigure = /** @class */ (function (_super) {
    __extends(SquareFigure, _super);
    function SquareFigure(color) {
        return _super.call(this, color) || this;
    }
    SquareFigure.prototype.createFigure = function () {
        var cells = new Array();
        var middleOfGrid = Math.ceil(grid.getNumberOfCellsHorizontal() / 2);
        cells.push(new Cell(middleOfGrid, 1, this.getColor()));
        cells.push(new Cell(middleOfGrid, 0, this.getColor()));
        cells.push(new Cell(middleOfGrid - 1, 0, this.getColor()));
        cells.push(new Cell(middleOfGrid - 1, 1, this.getColor()));
        return cells;
    };
    SquareFigure.prototype.rotateFigure = function (clone) {
        //do nothing
    };
    return SquareFigure;
}(Figure));
