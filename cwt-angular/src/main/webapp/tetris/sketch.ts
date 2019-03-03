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


let grid: Grid;
let randomFigure: Figure;
let fallenCells: Cell[];
let fallenDownInterval: number;
let highscore: number = 0;
let canvasWidth = Cell.WIDTH * 10 + 1;
let canvasHeight = window.innerHeight;
let levelVelocity: LevelVelocity = LevelVelocity.LEVEL_1;

function setup() {
    createCanvas(canvasWidth, canvasHeight);

    grid = new Grid(canvasWidth, canvasHeight);
    grid.draw();

    fallenCells = new Array();

    randomFigure = calculateRandomFigure();
    randomFigure.draw();

    fallenDownInterval = window.setInterval(() => {
        randomFigure.fallDown(grid, fallenCells);
    }, levelVelocity);

    window.addEventListener("keydown", (event) => {
        randomFigure.move(event.key, grid, fallenCells);
    });
}


function draw() {
    clear();

    nextFigure();

    grid.draw();

    dropFigures(deleteFullRows());

    showHighscore();
    showLevel("black");

    changeLevels();

    grid.updateGrid();

    randomFigure.draw();
}

function changeLevels() {
    if (highscore >= 300 && highscore < 600 && levelVelocity != LevelVelocity.LEVEL_2) {
        updateFallenDownInterval(LevelVelocity.LEVEL_2);
    } else if (highscore >= 600 && highscore < 900 && levelVelocity != LevelVelocity.LEVEL_3) {
        updateFallenDownInterval(LevelVelocity.LEVEL_3);
    } else if (highscore >= 900 && highscore < 1200 && levelVelocity != LevelVelocity.LEVEL_4) {
        updateFallenDownInterval(LevelVelocity.LEVEL_4);
    } else if (highscore >= 1200 && highscore < 1500 && levelVelocity != LevelVelocity.LEVEL_5) {
        updateFallenDownInterval(LevelVelocity.LEVEL_5);
    } else if (highscore >= 1500 && highscore < 1800 && levelVelocity != LevelVelocity.LEVEL_6) {
        updateFallenDownInterval(LevelVelocity.LEVEL_6);
    } else if (highscore >= 1800 && highscore < 2100 && levelVelocity != LevelVelocity.LEVEL_7) {
        updateFallenDownInterval(LevelVelocity.LEVEL_7);
    } else if (highscore >= 2100 && highscore < 2400 && levelVelocity != LevelVelocity.LEVEL_8) {
        updateFallenDownInterval(LevelVelocity.LEVEL_8);
    } else if (highscore >= 2400 && highscore < 2700 && levelVelocity != LevelVelocity.LEVEL_9) {
        updateFallenDownInterval(LevelVelocity.LEVEL_9);
    } else if (highscore >= 2700 && highscore < 3000 && levelVelocity != LevelVelocity.LEVEL_10) {
        updateFallenDownInterval(LevelVelocity.LEVEL_10);
    } else if (highscore >= 3000 && highscore < 3300 && levelVelocity != LevelVelocity.LEVEL_11) {
        updateFallenDownInterval(LevelVelocity.LEVEL_11);
    } else if (highscore >= 3300 && highscore < 3600 && levelVelocity != LevelVelocity.LEVEL_12) {
        updateFallenDownInterval(LevelVelocity.LEVEL_12);
    }
}

function showLevel(color: String) {
    textSize(12);
    fill(color);
    text(LevelVelocity[levelVelocity], 5, 54);
}

function updateFallenDownInterval(newLevelVelocity: LevelVelocity) {
    clearInterval(fallenDownInterval);
    levelVelocity = newLevelVelocity;
    fallenDownInterval = window.setInterval(() => {
        randomFigure.fallDown(grid, fallenCells);
    }, levelVelocity);
}

function deleteFullRows(): number[] {

    let deletedRows: number[] = new Array();

    fallenCells.sort(function (a, b) {
        return a.getYPos() - b.getYPos();
    });

    let counterOfFallenCellsInOneRow = 0;
    const clone = JSON.parse(JSON.stringify(fallenCells));

    for (let i = 0; i < clone.length - 1; i++) {
        if (clone[i].yPos == clone[i + 1].yPos) {
            counterOfFallenCellsInOneRow++;
            if (counterOfFallenCellsInOneRow + 1 === grid.getNumberOfCellsHorizontal()) {
                let deletingYPos = clone[i].yPos;
                deletedRows.push(deletingYPos);

                for (let j = 0; j < fallenCells.length;) {
                    if (fallenCells[j].getYPos() == deletingYPos) {
                        fallenCells.splice(j, 1);
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

function dropFigures(deletedRows: number[]) {
    for (let cell of fallenCells) {
        for (let deleteRow of deletedRows) {
            if (cell.getYPos() < deleteRow) {
                let tmp = cell.getYPos();
                cell.setYPos(tmp + 1);
            }
        }
    }
    highscore += (deletedRows.length * 10);
}

function showHighscore() {
    textSize(30);
    fill("black");
    text(highscore.toString(), 5, 35);
}

function gameOver() {
    noLoop();
    let input = document.createElement("input");
    input.type = "Game over";

    let gameOverText = document.getElementById("gameOverText");
    if (gameOverText != null) {
        gameOverText.innerText = "Game over";
        gameOverText.setAttribute("style", "-webkit-animation: moveGameOverText 5s infinite; " +
            "animation: moveGameOverText 5s infinite; animation-iteration-count: 1;" +
            "display: block !important;");
    }

    let highscoreDiv = document.getElementById("highscore");
    if (highscoreDiv != null) {
        highscoreDiv.setAttribute("style", "display: block; -webkit-animation: moveBackground 5s infinite; " +
            "animation: moveBackground 5s infinite; animation-iteration-count: 1;");
    }

    /*window.setTimeout(function () {
        const name = prompt("What is your name?");

        $.post(
            {
                url: '/views/post-highscore.php',
                data: {
                    highscore: highscore,
                    name: name
                },
                dataType: 'html'
            }
        ).done(function (x, y, z) {
            debugger;
            alert("Success");
        }).fail(function (x, y, z) {
            debugger;
            alert("Success");
        });
    }, 5000);*/

}

function nextFigure() {
    if (randomFigure.isLanded()) {
        for (let cell of randomFigure.getCells()) {
            if (cell.getYPos() <= 0) {
                gameOver();
            }
        }
        for (let cell of randomFigure.getCells()) {
            fallenCells.push(cell);
            randomFigure = calculateRandomFigure();
        }
        highscore += 10;
    }
}


function keyPressed() {
    if (keyCode === DOWN_ARROW) {
        clearInterval(fallenDownInterval);
        fallenDownInterval = window.setInterval(() => {
            randomFigure.fallDown(grid, fallenCells);
        }, LevelVelocity.ARROW_DOWN);
    }
}

function keyReleased() {
    if (keyCode === DOWN_ARROW) {
        clearInterval(fallenDownInterval);
        fallenDownInterval = window.setInterval(() => {
            randomFigure.fallDown(grid, fallenCells);
        }, levelVelocity);
    }
}

function calculateRandomFigure(): Figure {

    let randomNumber = Math.floor(Math.random() * 7);

    switch (randomNumber) {
        case 0:
            return new SquareFigure("#FFD700", grid);
        case 1:
            return new TFigure("#DC143C", grid);
        case 2:
            return new IFigure("#1E90FF", grid);
        case 3:
            return new LFigure("#9400D3", grid);
        case 4:
            return new JFigure("#5F9EA0", grid);
        case 5:
            return new SFigure("#FA8072", grid);
        case 6:
            return new ZFigure("#C71585", grid);
    }

    return new SquareFigure("blue", grid);
}