import {Cell} from "../grid/cell";
import {Figure} from "./figure";
import {Grid} from "../grid/grid";

export class IFigure extends Figure {

    createFigure(grid : Grid): Cell[] {
        let cells = new Array();
        let middleOfGrid = Math.ceil(grid.getNumberOfCellsHorizontal() / 2);

        for (let i = 0; i < 4; i++) {
            cells.push(new Cell(middleOfGrid -1, i, this.getColor(), grid.getCellWidth()));
        }
        return cells;
    }

    rotateFigure(clone: any): void {

        let counter: number;

        switch (this.getRotateCounter()) {
            case 0:

                counter = clone.cells[0].xPos - 2;
                for (let i = 0; i < this.getCells().length; i++, counter++) {
                    this.getCells()[i].setYPos(clone.cells[clone.indexOfHighestYPosition - 1].yPos);
                    this.getCells()[i].setXPos(counter);
                }
                break;

            case 1:

                counter = 2;
                for (let i = 0; i < this.getCells().length; i++, counter--) {
                    this.getCells()[i].setXPos(clone.cells[clone.indexOfHighestXPosition - 1].xPos);
                    this.getCells()[i].setYPos(clone.cells[i].yPos - counter);
                }
                this.setRotateCounter(-1);
                break;
        }
    }

}