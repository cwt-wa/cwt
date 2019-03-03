class SquareFigure extends Figure {

    constructor(color: String) {
        super(color);
    }

    createFigure(): Cell[] {

        let cells = new Array();

        let middleOfGrid = Math.ceil(grid.getNumberOfCellsHorizontal() / 2);

        cells.push(new Cell(middleOfGrid, 1, this.getColor()));
        cells.push(new Cell(middleOfGrid, 0, this.getColor()));
        cells.push(new Cell(middleOfGrid - 1, 0, this.getColor()));
        cells.push(new Cell(middleOfGrid - 1, 1, this.getColor()));

        return cells;
    }

    // @ts-ignore
    rotateFigure(clone: any): void {
        //do nothing
    }
}