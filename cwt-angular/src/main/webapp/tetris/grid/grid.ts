import {Cell} from "./cell";
import * as p5 from "p5";

export class Grid {

    private width: number;
    private height: number;
    private grid: Array<Array<Cell>>;

    constructor(width: number, height: number) {

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

    draw(canvas : p5): void {

        for (let i = 0; i < this.grid.length; i++) {
            for (let j = 0; j < this.grid[i].length; j++) {
                canvas.fill(this.grid[i][j].getColor().toString());
                canvas.stroke("#303030");
                canvas.rect(j * Cell.WIDTH, i * Cell.WIDTH, Cell.WIDTH, Cell.WIDTH);
            }
        }
    }

    updateGrid(fallenCells : Cell[]) : Cell[] {

        for (let matrix of this.grid) {
            for (let cell of matrix) {
                cell.setColor("white");
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
}