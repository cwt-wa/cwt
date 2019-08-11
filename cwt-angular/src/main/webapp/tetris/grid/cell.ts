export class Cell {
    private xPos: number;
    private yPos: number;
    private color: String;
    private width: number;

    constructor(xPos: number, yPos: number, color: String, width: number) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.color = color;
        this.width = width;
    }

    getWidth() : number {
        return this.width;
    }

    setWidth(width: number) {
        this.width = width;
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