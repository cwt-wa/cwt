import {Figure} from "./figure";
import {Cell} from "../grid/cell";
import {Grid} from "../grid/grid";

export class SquareFigure extends Figure {

    constructor(color: String, grid: Grid) {
        super(color, grid);
    }

    createFigure(grid: Grid): Cell[] {

        let cells = new Array();

        let middleOfGrid = Math.ceil(grid.getNumberOfCellsHorizontal() / 2);

        cells.push(new Cell(middleOfGrid, 1, this.getColor(), grid.getCellWidth()));
        cells.push(new Cell(middleOfGrid, 0, this.getColor(), grid.getCellWidth()));
        cells.push(new Cell(middleOfGrid - 1, 0, this.getColor(), grid.getCellWidth()));
        cells.push(new Cell(middleOfGrid - 1, 1, this.getColor(), grid.getCellWidth()));

        return cells;
    }

    // @ts-ignore
    rotateFigure(clone: any): void {
        //do nothing
    }
}