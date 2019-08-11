"use strict";
var Grid = /** @class */ (function () {
    function Grid(width, height) {
        this.width = Math.floor(width / Cell.WIDTH);
        this.height = Math.floor(height / Cell.WIDTH);
        this.grid = new Array();
        for (var heightIndex = 0; heightIndex < this.height; heightIndex++) { //so hoch
            this.grid.push(new Array());
            for (var widthIndex = 0; widthIndex < this.width; widthIndex++) {
                this.grid[heightIndex].push(new Cell(widthIndex, heightIndex, "white")); //x-, y, color
            }
        }
    }
    Grid.prototype.draw = function () {
        for (var i = 0; i < this.grid.length; i++) {
            for (var j = 0; j < this.grid[i].length; j++) {
                fill(this.grid[i][j].getColor());
                stroke("#303030");
                rect(j * Cell.WIDTH, i * Cell.WIDTH, Cell.WIDTH, Cell.WIDTH);
            }
        }
    };
    Grid.prototype.updateFallenCellsInGrid = function () {
        for (var _i = 0, _a = this.grid; _i < _a.length; _i++) {
            var matrix = _a[_i];
            for (var _b = 0, matrix_1 = matrix; _b < matrix_1.length; _b++) {
                var cell = matrix_1[_b];
                cell.setColor("white");
            }
        }
        for (var i = 0; i < fallenCells.length; i++) {
            this.grid[fallenCells[i].getYPos()][fallenCells[i].getXPos()].setColor(fallenCells[i].getColor());
        }
    };
    Grid.prototype.getGrid = function () {
        return this.grid;
    };
    Grid.prototype.getNumberOfCellsHorizontal = function () {
        return this.width;
    };
    Grid.prototype.getNumberOfCellsVertical = function () {
        return this.height;
    };
    return Grid;
}());
