"use strict";
var Figure = /** @class */ (function () {
    function Figure(color) {
        this.indexOfSmallestXPosition = 0;
        this.indexOfHighestXPosition = 0;
        this.indexOfHighestYPosition = 0;
        this.indexOfSmallestYPosition = 0;
        this.rotateCounter = 0;
        this.color = color;
        this.landed = false;
        this.cells = this.createFigure();
        this.setIndexOfHighestYPosition(CellUtils.calculateIndexOfHighestYPosition(this.cells));
        this.setIndexOfSmallestYPosition(CellUtils.calculateIndexOfSmallestYPosition(this.cells));
        this.setIndexOfSmallestXPosition(CellUtils.calculateIndexOfSmallestXPosition(this.cells));
        this.setIndexOfHighestXPosition(CellUtils.calculateIndexOfGreatestXPosition(this.cells));
    }
    Figure.prototype.rotate = function () {
        var clone = JSON.parse(JSON.stringify(this));
        this.rotateFigure(clone);
        for (var _i = 0, _a = this.getCells(); _i < _a.length; _i++) {
            var cell = _a[_i];
            if (cell.getXPos() < 0 || cell.getYPos() < 0
                || cell.getXPos() >= grid.getNumberOfCellsHorizontal()
                || cell.getYPos() >= grid.getNumberOfCellsVertical()) {
                this.reset(clone);
                return;
            }
        }
        this.setIndexOfHighestYPosition(CellUtils.calculateIndexOfHighestYPosition(this.getCells()));
        this.setIndexOfSmallestXPosition(CellUtils.calculateIndexOfSmallestXPosition(this.getCells()));
        this.setIndexOfHighestXPosition(CellUtils.calculateIndexOfGreatestXPosition(this.getCells()));
        this.setIndexOfSmallestYPosition(CellUtils.calculateIndexOfSmallestYPosition(this.getCells()));
        this.setRotateCounter(this.getRotateCounter() + 1);
    };
    Figure.prototype.reset = function (clone) {
        for (var i = 0; i < this.getCells().length; i++) {
            this.getCells()[i].setYPos(clone.cells[i].yPos);
            this.getCells()[i].setXPos(clone.cells[i].xPos);
        }
        if (this.getRotateCounter() == -1) {
            this.setRotateCounter(3);
        }
    };
    Figure.prototype.draw = function () {
        for (var _i = 0, _a = this.getCells(); _i < _a.length; _i++) {
            var cell = _a[_i];
            grid.getGrid()[cell.getYPos()][cell.getXPos()].setColor(cell.getColor());
        }
    };
    Figure.prototype.fallDown = function (grid, fallenCells) {
        for (var _i = 0, fallenCells_1 = fallenCells; _i < fallenCells_1.length; _i++) {
            var fallenCell = fallenCells_1[_i];
            for (var _a = 0, _b = this.getCells(); _a < _b.length; _a++) {
                var cell = _b[_a];
                if ((fallenCell.getYPos() - 1) == cell.getYPos()) {
                    if (fallenCell.getXPos() == cell.getXPos()) {
                        this.setLanded(true);
                        return;
                    }
                }
            }
        }
        if (this.getCells()[this.indexOfHighestYPosition].getYPos() >= grid.getGrid().length - 1) {
            this.setLanded(true);
            return;
        }
        for (var _c = 0, _d = this.getCells(); _c < _d.length; _c++) {
            var cell = _d[_c];
            cell.setYPos(cell.getYPos() + 1);
        }
    };
    Figure.prototype.move = function (keyEvent, grid, fallenCells) {
        if (this.isLanded() == true)
            return;
        switch (keyEvent) {
            case "ArrowRight":
                if (this.getCells()[this.getIndexOfHighestXPosition()].getXPos() >= grid.getGrid()[0].length - 1)
                    return;
                for (var _i = 0, fallenCells_2 = fallenCells; _i < fallenCells_2.length; _i++) {
                    var fallenCell = fallenCells_2[_i];
                    for (var _a = 0, _b = this.getCells(); _a < _b.length; _a++) {
                        var cell = _b[_a];
                        if (fallenCell.getXPos() - 1 == cell.getXPos()) {
                            if (fallenCell.getYPos() == cell.getYPos()) {
                                return;
                            }
                        }
                    }
                }
                for (var _c = 0, _d = this.getCells(); _c < _d.length; _c++) {
                    var cell = _d[_c];
                    cell.setXPos(cell.getXPos() + 1);
                }
                break;
            case "ArrowLeft":
                if (this.getCells()[this.getIndexOfSmallestXPosition()].getXPos() <= 0)
                    return;
                for (var _e = 0, fallenCells_3 = fallenCells; _e < fallenCells_3.length; _e++) {
                    var fallenCell = fallenCells_3[_e];
                    for (var _f = 0, _g = this.getCells(); _f < _g.length; _f++) {
                        var cell = _g[_f];
                        if (fallenCell.getXPos() + 1 == cell.getXPos()) {
                            if (fallenCell.getYPos() == cell.getYPos()) {
                                return;
                            }
                        }
                    }
                }
                for (var _h = 0, _j = this.getCells(); _h < _j.length; _h++) {
                    var cell = _j[_h];
                    cell.setXPos(cell.getXPos() - 1);
                }
                break;
            case "ArrowUp":
                this.rotate();
                break;
        }
    };
    /*public fallDownFast(keyEvent: String, grid: Grid, fallenCells: Cell[]): void {
        if (keyEvent != " ") return;

        setInterval(() => {
            this.fallDown(grid, fallenCells);
        }, LevelVelocity.ARROW_DOWN);

    }*/
    Figure.prototype.getCells = function () {
        return this.cells;
    };
    Figure.prototype.setCells = function (cells) {
        this.cells = cells;
    };
    Figure.prototype.getColor = function () {
        return this.color;
    };
    Figure.prototype.setColor = function (color) {
        this.color = color;
    };
    Figure.prototype.isLanded = function () {
        return this.landed;
    };
    Figure.prototype.setLanded = function (landed) {
        this.landed = landed;
    };
    Figure.prototype.setIndexOfSmallestXPosition = function (index) {
        this.indexOfSmallestXPosition = index;
    };
    Figure.prototype.getIndexOfSmallestXPosition = function () {
        return this.indexOfSmallestXPosition;
    };
    Figure.prototype.setIndexOfHighestXPosition = function (index) {
        this.indexOfHighestXPosition = index;
    };
    Figure.prototype.getIndexOfHighestXPosition = function () {
        return this.indexOfHighestXPosition;
    };
    Figure.prototype.getIndexOfHighestYPosition = function () {
        return this.indexOfHighestYPosition;
    };
    Figure.prototype.setIndexOfHighestYPosition = function (index) {
        this.indexOfHighestYPosition = index;
    };
    Figure.prototype.getRotateCounter = function () {
        return this.rotateCounter;
    };
    Figure.prototype.setRotateCounter = function (counter) {
        this.rotateCounter = counter;
    };
    Figure.prototype.getIndexOfSmallestYPosition = function () {
        return this.indexOfSmallestYPosition;
    };
    Figure.prototype.setIndexOfSmallestYPosition = function (value) {
        this.indexOfSmallestYPosition = value;
    };
    return Figure;
}());
