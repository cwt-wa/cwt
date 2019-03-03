import {Figure} from "./figure";
import {Grid} from "../grid/grid";
import {Cell} from "../grid/cell";

export class TFigure extends Figure {

    private centerIndex : number = 0;

    constructor(color: String, grid: Grid) {
        super(color, grid);
        this.initCenterIndex();
    }

    createFigure(grid: Grid): Cell[] {

        let cells = new Array();
        let middleOfGrid = Math.ceil(grid.getNumberOfCellsHorizontal() / 2);

        cells.push(new Cell(middleOfGrid, 0, this.getColor()));
        cells.push(new Cell(middleOfGrid -1, 1, this.getColor()));
        cells.push(new Cell(middleOfGrid, 1, this.getColor()));
        cells.push(new Cell(middleOfGrid + 1, 1, this.getColor()));

        return cells;
    }

    initCenterIndex(): void {
        this.setCenterIndex(0);
    }

    rotateFigure(clone: any): void {

        switch (this.getRotateCounter()) {
            case 0:
                this.changePositions(clone.cells[clone.indexOfHighestYPosition],
                    clone.cells[clone.indexOfSmallestXPosition], 0, undefined);
                break;

            case 1:
                this.changePositions(clone.cells[clone.indexOfSmallestYPosition],
                    clone.cells[clone.indexOfSmallestXPosition], undefined, 2);
                break;

            case 2:
                this.changePositions(clone.cells[clone.indexOfSmallestYPosition],
                    clone.cells[clone.indexOfHighestXPosition], 2, undefined);
                break;

            case 3:

                this.changePositions(clone.cells[clone.indexOfHighestYPosition],
                    clone.cells[clone.indexOfHighestXPosition], undefined, 0);

                this.setRotateCounter(-1);
                break;
        }
    }

    changePositions(cloneY: any, cloneX: any, yCounter ?: number, xCounter ?: number,): void {

        let calcXCounter = true;
        let calcYCounter = true;

        if (xCounter == undefined) {
            xCounter = 0;
            calcXCounter = false;
        }
        if (yCounter == undefined) {
            yCounter = 0;
            calcYCounter = false;
        }

        for (let i = 0; i < this.getCells().length; i++) {
            if (i == this.getCenterIndex()) continue;
            this.getCells()[i].setYPos(cloneY.yPos + yCounter);
            this.getCells()[i].setXPos(cloneX.xPos + xCounter);

            if (calcXCounter) xCounter--;
            if (calcYCounter) yCounter--;

        }
    }

    public setCenterIndex(index: number) {
        this.centerIndex = index;
    }

    public getCenterIndex(): number {
        return this.centerIndex;
    }
}