export class Cell {

    static readonly WIDTH = 40;

    private xPos: number;
    private yPos: number;
    private color: String;

    constructor(xPos: number, yPos: number, color: String) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.color = color;
    }

    getXPos() {
        return this.xPos;
    }

    getYPos() {
        return this.yPos;
    }

    setYPos(yPos: number) {
        this.yPos = yPos;
    }

    setXPos(xPos: number) {
        this.xPos = xPos;
    }

    setColor(color: String) {
        this.color = color;
    }

    getColor() {
        return this.color;
    }
}