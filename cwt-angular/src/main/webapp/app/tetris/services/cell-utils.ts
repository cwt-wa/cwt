class CellUtils {

    public static calculateIndexOfHighestYPosition(cells: Cell[]): number {
        let indexOfHighestYPosition = 0;
        for (let i = 0; i < cells.length; i++) {
            if (cells[i].getYPos() > cells[indexOfHighestYPosition].getYPos()) {
                indexOfHighestYPosition = i;
            }
        }
        return indexOfHighestYPosition;
    }

    public static calculateIndexOfSmallestYPosition(cells: Cell[]): number {
        let indexOfSmallestYPosition = 0;
        for (let i = 0; i < cells.length; i++) {
            if (cells[i].getYPos() < cells[indexOfSmallestYPosition].getYPos()) {
                indexOfSmallestYPosition = i;
            }
        }
        return indexOfSmallestYPosition;
    }

    public static calculateIndexOfSmallestXPosition(cells: Cell[]): number {
        let indexOfSmallestXPosition = 0;
        for (let i = 0; i < cells.length; i++) {
            if (cells[i].getXPos() < cells[indexOfSmallestXPosition].getXPos()) {
                indexOfSmallestXPosition = i;
            }
        }
        return indexOfSmallestXPosition;
    }

    public static calculateIndexOfGreatestXPosition(cells: Cell[]): number {
        let indexOfGreatestXPosition = 0;
        for (let i = 0; i < cells.length; i++) {
            if (cells[i].getXPos() > cells[indexOfGreatestXPosition].getXPos()) {
                indexOfGreatestXPosition = i;
            }
        }
        return indexOfGreatestXPosition;
    }

}