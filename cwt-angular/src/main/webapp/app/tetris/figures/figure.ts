abstract class Figure {

    private cells: Cell[];
    private color: String;
    private landed: boolean;
    private indexOfSmallestXPosition: number = 0;
    private indexOfHighestXPosition: number = 0;
    private indexOfHighestYPosition: number = 0;
    private indexOfSmallestYPosition: number = 0;
    private rotateCounter: number = 0;

    constructor(color: String) {
        this.color = color;
        this.landed = false;
        this.cells = this.createFigure();
        this.setIndexOfHighestYPosition(CellUtils.calculateIndexOfHighestYPosition(this.cells));
        this.setIndexOfSmallestYPosition(CellUtils.calculateIndexOfSmallestYPosition(this.cells));
        this.setIndexOfSmallestXPosition(CellUtils.calculateIndexOfSmallestXPosition(this.cells));
        this.setIndexOfHighestXPosition(CellUtils.calculateIndexOfGreatestXPosition(this.cells));
    }

    abstract createFigure(): Cell[];

    abstract rotateFigure(clone: any): void;

    protected rotate(): void {

        const clone = JSON.parse(JSON.stringify(this));

        this.rotateFigure(clone);

        for (let cell of this.getCells()) {
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

    }

    protected reset(clone: any) {
        for (let i = 0; i < this.getCells().length; i++) {
            this.getCells()[i].setYPos(clone.cells[i].yPos);
            this.getCells()[i].setXPos(clone.cells[i].xPos);
        }
        if (this.getRotateCounter() == -1) {
            this.setRotateCounter(3);
        }
    }

    public draw() {
        for (let cell of this.getCells()) {
            grid.getGrid()[cell.getYPos()][cell.getXPos()].setColor(cell.getColor());
        }
    }

    public fallDown(grid: Grid, fallenCells: Cell[]) {

        for (let fallenCell of fallenCells) {
            for (let cell of this.getCells()) {
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

        for (let cell of this.getCells()) {
            cell.setYPos(cell.getYPos() + 1);
        }
    }

    public move(keyEvent: String, grid: Grid, fallenCells: Cell[]): void {

        if (this.isLanded() == true) return;

        switch (keyEvent) {
            case "ArrowRight":
                if (this.getCells()[this.getIndexOfHighestXPosition()].getXPos() >= grid.getGrid()[0].length - 1) return;

                for (let fallenCell of fallenCells) {
                    for (let cell of this.getCells()) {
                        if (fallenCell.getXPos() - 1 == cell.getXPos()) {
                            if (fallenCell.getYPos() == cell.getYPos()) {
                                return;
                            }
                        }
                    }
                }

                for (let cell of this.getCells()) {
                    cell.setXPos(cell.getXPos() + 1);
                }
                break;

            case "ArrowLeft":
                if (this.getCells()[this.getIndexOfSmallestXPosition()].getXPos() <= 0) return;

                for (let fallenCell of fallenCells) {
                    for (let cell of this.getCells()) {
                        if (fallenCell.getXPos() + 1 == cell.getXPos()) {
                            if (fallenCell.getYPos() == cell.getYPos()) {
                                return;
                            }
                        }
                    }
                }

                for (let cell of this.getCells()) {
                    cell.setXPos(cell.getXPos() - 1);
                }
                break;

            case "ArrowUp":
                this.rotate();
                break;
        }

    }

    public getCells(): Cell[] {
        return this.cells;
    }

    public setCells(cells: Cell[]) {
        this.cells = cells;
    }

    public getColor(): String {
        return this.color;
    }

    public setColor(color: String) {
        this.color = color;
    }

    public isLanded(): boolean {
        return this.landed;
    }

    public setLanded(landed: boolean) {
        this.landed = landed;
    }

    public setIndexOfSmallestXPosition(index: number) {
        this.indexOfSmallestXPosition = index;
    }

    public getIndexOfSmallestXPosition(): number {
        return this.indexOfSmallestXPosition;
    }

    public setIndexOfHighestXPosition(index: number) {
        this.indexOfHighestXPosition = index;
    }

    public getIndexOfHighestXPosition(): number {
        return this.indexOfHighestXPosition;
    }

    public getIndexOfHighestYPosition(): number {
        return this.indexOfHighestYPosition;
    }

    public setIndexOfHighestYPosition(index: number) {
        this.indexOfHighestYPosition = index;
    }

    public getRotateCounter(): number {
        return this.rotateCounter;
    }

    public setRotateCounter(counter: number) {
        this.rotateCounter = counter;
    }

    public getIndexOfSmallestYPosition(): number {
        return this.indexOfSmallestYPosition;
    }

    public setIndexOfSmallestYPosition(value: number) {
        this.indexOfSmallestYPosition = value;
    }

}
