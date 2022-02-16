import {Figure} from "./figure";
import {Cell} from "../grid/cell";
import {Grid} from "../grid/grid";

export class SFigure extends Figure {

    createFigure(grid: Grid): Cell[] {

        let cells = new Array();
        let middleOfGrid = Math.ceil(grid.getNumberOfCellsHorizontal() / 2);

        cells.push(new Cell(middleOfGrid, 0, this.getColor(), grid.getCellWidth()));
        cells.push(new Cell(middleOfGrid + 1, 0, this.getColor(), grid.getCellWidth()));
        cells.push(new Cell(middleOfGrid, 1, this.getColor(), grid.getCellWidth()));
        cells.push(new Cell(middleOfGrid - 1, 1, this.getColor(), grid.getCellWidth()));

        return cells;

    }

    rotateFigure(clone: any): void {
        let counterX = 0;
        switch (this.getRotateCounter()) {
            case 0:
                let counterY = -1;

                for (let i = 0; i < this.getCells().length; i++) {
                    if (clone.cells[i].yPos === clone.cells[clone.indexOfHighestYPosition].yPos) continue;
                    this.getCells()[i].setXPos(clone.cells[clone.indexOfSmallestXPosition].xPos + counterX);
                    this.getCells()[i].setYPos(clone.cells[clone.indexOfHighestYPosition].yPos + counterY);
                    counterY = counterY + 2;
                    counterX++;
                }
                break;

            case 1:
                for (let i = 0; i < this.getCells().length; i++) {
                    if (clone.cells[i].yPos === (clone.cells[clone.indexOfHighestYPosition].yPos - 1)) continue;

                    this.getCells()[i].setXPos(clone.cells[clone.indexOfHighestXPosition].xPos + counterX);
                    this.getCells()[i].setYPos(clone.cells[clone.indexOfSmallestYPosition].yPos);
                    counterX++;
                }
                this.setRotateCounter(-1);
                break;
        }
    }


    protected reset(clone: any) {
        for (let i = 0; i < this.getCells().length; i++) {
            this.getCells()[i].setYPos(clone.cells[i].yPos);
            this.getCells()[i].setXPos(clone.cells[i].xPos);
        }
        if (this.getRotateCounter() == -1) {
            this.setRotateCounter(1);
        }
    }

}
