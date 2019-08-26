import {Figure} from "./figure";
import {Grid} from "../grid/grid";
import {Cell} from "../grid/cell";

export class LittleSquareFigure extends Figure {

    createFigure(grid: Grid): Cell[] {
        return new Array(
            new Cell(0, 0, this.getColor(), grid.getCellWidth())
        );
    }

    //@ts-ignore
    rotateFigure(clone: any): void {
        //do nothing
    }
}