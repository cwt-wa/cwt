import {Grid} from "./grid/grid";
import {Figure} from "./figures/figure";
import {Cell} from "./grid/cell";
import {LevelVelocity} from "./level";
import {TFigure} from "./figures/t-figure";
import {SquareFigure} from "./figures/square-figure";
import {IFigure} from "./figures/i-figure";
import {LFigure} from "./figures/l-figure";
import {JFigure} from "./figures/j-figure";
import {SFigure} from "./figures/s-figure";
import {ZFigure} from "./figures/z-figure";
import 'p5/lib/p5.js';
import * as p5 from "p5";

export class Tetris {

    private grid: Grid;
    private randomFigure: Figure;
    private fallenCells: Cell[];
    private fallenDownInterval: number;
    private highscore: number = 0;
    private canvasWidth = Cell.WIDTH * 10 + 1;
    private canvasHeight = window.innerHeight-85; //@TODO richtige Höhe berechnen (oder die Navigation ausblenden?)
    private levelVelocity: LevelVelocity = LevelVelocity.LEVEL_1;
    onGameOver?: (highscore: number) => void;

    constructor(private p5: p5) {
    }

    setup() {
        this.p5.createCanvas(this.canvasWidth, this.canvasHeight);

        this.grid = new Grid(this.canvasWidth, this.canvasHeight);
        this.grid.draw(this.p5);

        this.fallenCells = [];

        this.randomFigure = this.calculateRandomFigure();
        this.randomFigure.draw(this.grid);

        this.fallenDownInterval = window.setInterval(() => {
            this.randomFigure.fallDown(this.grid, this.fallenCells);
        }, this.levelVelocity);

        window.addEventListener("keydown", (event) => {
            this.randomFigure.move(event.key, this.grid, this.fallenCells);
        });
    }


    draw() {
        this.p5.clear();

        this.nextFigure();

        this.grid.draw(this.p5);

        this.dropFigures(this.deleteFullRows());

        this.showHighscore();
        this.showLevel("black");

        this.changeLevels();

        this.grid.updateGrid(this.fallenCells);

        this.randomFigure.draw(this.grid);
    }

    private changeLevels() {
        if (this.highscore >= 300 && this.highscore < 600 && this.levelVelocity != LevelVelocity.LEVEL_2) {
            this.updateFallenDownInterval(LevelVelocity.LEVEL_2);
        } else if (this.highscore >= 600 && this.highscore < 900 && this.levelVelocity != LevelVelocity.LEVEL_3) {
            this.updateFallenDownInterval(LevelVelocity.LEVEL_3);
        } else if (this.highscore >= 900 && this.highscore < 1200 && this.levelVelocity != LevelVelocity.LEVEL_4) {
            this.updateFallenDownInterval(LevelVelocity.LEVEL_4);
        } else if (this.highscore >= 1200 && this.highscore < 1500 && this.levelVelocity != LevelVelocity.LEVEL_5) {
            this.updateFallenDownInterval(LevelVelocity.LEVEL_5);
        } else if (this.highscore >= 1500 && this.highscore < 1800 && this.levelVelocity != LevelVelocity.LEVEL_6) {
            this.updateFallenDownInterval(LevelVelocity.LEVEL_6);
        } else if (this.highscore >= 1800 && this.highscore < 2100 && this.levelVelocity != LevelVelocity.LEVEL_7) {
            this.updateFallenDownInterval(LevelVelocity.LEVEL_7);
        } else if (this.highscore >= 2100 && this.highscore < 2400 && this.levelVelocity != LevelVelocity.LEVEL_8) {
            this.updateFallenDownInterval(LevelVelocity.LEVEL_8);
        } else if (this.highscore >= 2400 && this.highscore < 2700 && this.levelVelocity != LevelVelocity.LEVEL_9) {
            this.updateFallenDownInterval(LevelVelocity.LEVEL_9);
        } else if (this.highscore >= 2700 && this.highscore < 3000 && this.levelVelocity != LevelVelocity.LEVEL_10) {
            this.updateFallenDownInterval(LevelVelocity.LEVEL_10);
        } else if (this.highscore >= 3000 && this.highscore < 3300 && this.levelVelocity != LevelVelocity.LEVEL_11) {
            this.updateFallenDownInterval(LevelVelocity.LEVEL_11);
        } else if (this.highscore >= 3300 && this.highscore < 3600 && this.levelVelocity != LevelVelocity.LEVEL_12) {
            this.updateFallenDownInterval(LevelVelocity.LEVEL_12);
        }
    }

    private showLevel(color: String) {
        this.p5.textSize(12);
        this.p5.fill(color.toString());
        this.p5.text(LevelVelocity[this.levelVelocity], 5, 54);
    }

    private updateFallenDownInterval(newLevelVelocity: LevelVelocity) {
        clearInterval(this.fallenDownInterval);
        this.levelVelocity = newLevelVelocity;
        this.fallenDownInterval = window.setInterval(() => {
            this.randomFigure.fallDown(this.grid, this.fallenCells);
        }, this.levelVelocity);
    }

    private deleteFullRows(): number[] {

        let deletedRows: number[] = [];

        this.fallenCells.sort(function (a, b) {
            return a.getYPos() - b.getYPos();
        });

        let counterOfFallenCellsInOneRow = 0;
        const clone = JSON.parse(JSON.stringify(this.fallenCells));

        for (let i = 0; i < clone.length - 1; i++) {
            if (clone[i].yPos == clone[i + 1].yPos) {
                counterOfFallenCellsInOneRow++;
                if (counterOfFallenCellsInOneRow + 1 === this.grid.getNumberOfCellsHorizontal()) {
                    let deletingYPos = clone[i].yPos;
                    deletedRows.push(deletingYPos);

                    for (let j = 0; j < this.fallenCells.length;) {
                        if (this.fallenCells[j].getYPos() == deletingYPos) {
                            this.fallenCells.splice(j, 1);
                        } else {
                            j++;
                        }
                    }

                    counterOfFallenCellsInOneRow = 0;
                }
            } else {
                counterOfFallenCellsInOneRow = 0;
            }
        }

        return deletedRows;
    }

    private dropFigures(deletedRows: number[]) {
        for (let cell of this.fallenCells) {
            for (let deleteRow of deletedRows) {
                if (cell.getYPos() < deleteRow) {
                    let tmp = cell.getYPos();
                    cell.setYPos(tmp + 1);
                }
            }
        }
        this.highscore += (deletedRows.length * 10);
    }

    private showHighscore() {
        this.p5.textSize(30);
        this.p5.fill("black");
        this.p5.text(this.highscore.toString(), 5, 35);
    }

    private gameOver() {
        this.p5.noLoop();
        this.onGameOver != null && this.onGameOver(this.highscore);
        clearInterval(this.fallenDownInterval);
        this.p5.noCanvas();
    }

    private nextFigure() {
        if (this.randomFigure.isLanded()) {
            for (let cell of this.randomFigure.getCells()) {
                if (cell.getYPos() <= 0) {
                    this.gameOver();
                }
            }
            for (let cell of this.randomFigure.getCells()) {
                this.fallenCells.push(cell);
                this.randomFigure = this.calculateRandomFigure();
            }
            this.highscore += 10;
        }
    }


    public keyPressed() {
        if (this.p5.keyCode === 40) {
            clearInterval(this.fallenDownInterval);
            this.fallenDownInterval = window.setInterval(() => {
                this.randomFigure.fallDown(this.grid, this.fallenCells);
            }, LevelVelocity.ARROW_DOWN);
        }
    }

    public keyReleased() {
        if (this.p5.keyCode === 40) {
            clearInterval(this.fallenDownInterval);
            this.fallenDownInterval = window.setInterval(() => {
                this.randomFigure.fallDown(this.grid, this.fallenCells);
            }, this.levelVelocity);
        }
    }

    private calculateRandomFigure(): Figure {

        let randomNumber = Math.floor(Math.random() * 7);

        switch (randomNumber) {
            case 0:
                return new SquareFigure("#4e4133", this.grid);
            case 1:
                return new TFigure("#2A9D8F", this.grid);
            case 2:
                return new IFigure("#007bff", this.grid);
            case 3:
                return new LFigure("#6f42c1", this.grid);
            case 4:
                return new JFigure("#e83e8c", this.grid);
            case 5:
                return new SFigure("#E9C46A", this.grid);
            case 6:
                return new ZFigure("#B9BAA3", this.grid);
        }

        return new SquareFigure("blue", this.grid);
    }

    public close() : void {
        this.p5.noCanvas();
    }

    public tearDown() : void {
        this.p5.keyReleased = null;
        this.p5.keyPressed = null;
    }
}
