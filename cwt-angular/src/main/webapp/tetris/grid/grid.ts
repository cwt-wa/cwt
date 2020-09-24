import {Cell} from "./cell";
import * as p5 from "p5";
import {grid_background, grid_stroke} from "../color-scheme";

export class Grid {

    public static readonly CELL_LINES_HORIZONTAL = 10;
    public static readonly CELL_LINES_VERTICAL = 16;

    private width: number;
    private height: number;
    private grid: Array<Array<Cell>>;
    private cellWidth: number;

    constructor(width: number, height: number, cellWidth: number) {
        this.cellWidth = cellWidth;
        this.width = Math.floor(width / cellWidth);
        this.height = Math.floor(height / cellWidth);

        this.grid = [];

        for (var heightIndex = 0; heightIndex < this.height; heightIndex++) {
            this.grid.push([]);
            for (var widthIndex = 0; widthIndex < this.width; widthIndex++) {
                this.grid[heightIndex].push(new Cell(widthIndex, heightIndex, grid_background, cellWidth));
            }
        }
    }

    draw(canvas : p5): void {

        for (let i = 0; i < this.grid.length; i++) {
            for (let j = 0; j < this.grid[i].length; j++) {
                canvas.fill(this.grid[i][j].getColor().toString());
                canvas.stroke(grid_stroke);
                canvas.rect(j * this.cellWidth, i * this.cellWidth, this.cellWidth, this.cellWidth);
            }
        }
    }

    updateFallenCellsAndFallenAnimationCellsInGrid(fallenCells: Cell[], fallenAnimationCells: Cell[]) {
        this.updateFallenCellsInGrid(fallenCells);
        for (let i = 0; i < fallenAnimationCells.length; i++) {
            this.grid[fallenAnimationCells[i].getYPos()][fallenAnimationCells[i].getXPos()].setColor(fallenAnimationCells[i].getColor());
        }

    }

    updateFallenCellsInGrid(fallenCells : Cell[]) : Cell[] {
        for (let matrix of this.grid) {
            for (let cell of matrix) {
                cell.setColor(grid_background);
            }
        }

        for (let i = 0; i < fallenCells.length; i++) {
            this.grid[fallenCells[i].getYPos()][fallenCells[i].getXPos()].setColor(fallenCells[i].getColor());
        }

        return fallenCells;

    }

    getGrid(): Cell[][] {
        return this.grid;
    }

    getNumberOfCellsHorizontal(): number {
        return this.width;
    }

    getNumberOfCellsVertical(): number {
        return this.height;
    }

    getCellWidth(): number {
        return this.cellWidth;
    }

    updateGridSize(cellWidth: number) {
        this.cellWidth = cellWidth;
        for (let cells of this.grid) {
            for (let cell of cells) {
                cell.setWidth(cellWidth);
            }
        }
    }
}