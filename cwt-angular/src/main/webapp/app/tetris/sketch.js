"use strict";
var grid;
var randomFigure;
var fallenCells;
var fallenDownInterval;
var highscore = 0;
var canvasWidth = Cell.WIDTH * 10 + 1;
var canvasHeight = window.innerHeight;
var levelVelocity = LevelVelocity.LEVEL_1;
function setup() {
    createCanvas(canvasWidth, canvasHeight);
    grid = new Grid(canvasWidth, canvasHeight);
    grid.draw();
    fallenCells = new Array();
    randomFigure = calculateRandomFigure();
    randomFigure.draw();
    fallenDownInterval = setInterval(function () {
        randomFigure.fallDown(grid, fallenCells);
    }, levelVelocity);
    window.addEventListener("keydown", function (event) {
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
    }
    else if (highscore >= 600 && highscore < 900 && levelVelocity != LevelVelocity.LEVEL_3) {
        updateFallenDownInterval(LevelVelocity.LEVEL_3);
    }
    else if (highscore >= 900 && highscore < 1200 && levelVelocity != LevelVelocity.LEVEL_4) {
        updateFallenDownInterval(LevelVelocity.LEVEL_4);
    }
    else if (highscore >= 1200 && highscore < 1500 && levelVelocity != LevelVelocity.LEVEL_5) {
        updateFallenDownInterval(LevelVelocity.LEVEL_5);
    }
    else if (highscore >= 1500 && highscore < 1800 && levelVelocity != LevelVelocity.LEVEL_6) {
        updateFallenDownInterval(LevelVelocity.LEVEL_6);
    }
    else if (highscore >= 1800 && highscore < 2100 && levelVelocity != LevelVelocity.LEVEL_7) {
        updateFallenDownInterval(LevelVelocity.LEVEL_7);
    }
    else if (highscore >= 2100 && highscore < 2400 && levelVelocity != LevelVelocity.LEVEL_8) {
        updateFallenDownInterval(LevelVelocity.LEVEL_8);
    }
    else if (highscore >= 2400 && highscore < 2700 && levelVelocity != LevelVelocity.LEVEL_9) {
        updateFallenDownInterval(LevelVelocity.LEVEL_9);
    }
    else if (highscore >= 2700 && highscore < 3000 && levelVelocity != LevelVelocity.LEVEL_10) {
        updateFallenDownInterval(LevelVelocity.LEVEL_10);
    }
    else if (highscore >= 3000 && highscore < 3300 && levelVelocity != LevelVelocity.LEVEL_11) {
        updateFallenDownInterval(LevelVelocity.LEVEL_11);
    }
    else if (highscore >= 3300 && highscore < 3600 && levelVelocity != LevelVelocity.LEVEL_12) {
        updateFallenDownInterval(LevelVelocity.LEVEL_12);
    }
}
function showLevel(color) {
    textSize(12);
    fill(color);
    text(LevelVelocity[levelVelocity], 5, 54);
}
function updateFallenDownInterval(newLevelVelocity) {
    clearInterval(fallenDownInterval);
    levelVelocity = newLevelVelocity;
    fallenDownInterval = setInterval(function () {
        randomFigure.fallDown(grid, fallenCells);
    }, levelVelocity);
}
function deleteFullRows() {
    var deletedRows = new Array();
    fallenCells.sort(function (a, b) {
        return a.getYPos() - b.getYPos();
    });
    var counterOfFallenCellsInOneRow = 0;
    var clone = JSON.parse(JSON.stringify(fallenCells));
    for (var i = 0; i < clone.length - 1; i++) {
        if (clone[i].yPos == clone[i + 1].yPos) {
            counterOfFallenCellsInOneRow++;
            if (counterOfFallenCellsInOneRow + 1 === grid.getNumberOfCellsHorizontal()) {
                var deletingYPos = clone[i].yPos;
                deletedRows.push(deletingYPos);
                for (var j = 0; j < fallenCells.length;) {
                    if (fallenCells[j].getYPos() == deletingYPos) {
                        fallenCells.splice(j, 1);
                    }
                    else {
                        j++;
                    }
                }
                counterOfFallenCellsInOneRow = 0;
            }
        }
        else {
            counterOfFallenCellsInOneRow = 0;
        }
    }
    return deletedRows;
}
function dropFigures(deletedRows) {
    for (var _i = 0, fallenCells_1 = fallenCells; _i < fallenCells_1.length; _i++) {
        var cell = fallenCells_1[_i];
        for (var _a = 0, deletedRows_1 = deletedRows; _a < deletedRows_1.length; _a++) {
            var deleteRow = deletedRows_1[_a];
            if (cell.getYPos() < deleteRow) {
                var tmp = cell.getYPos();
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
    var input = document.createElement("input");
    input.type = "Game over";
    var gameOverText = document.getElementById("gameOverText");
    if (gameOverText != null) {
        gameOverText.innerText = "Game over";
        gameOverText.setAttribute("style", "-webkit-animation: moveGameOverText 5s infinite; " +
            "animation: moveGameOverText 5s infinite; animation-iteration-count: 1;" +
            "display: block !important;");
    }
    var highscoreDiv = document.getElementById("highscore");
    if (highscoreDiv != null) {
        highscoreDiv.setAttribute("style", "display: block; -webkit-animation: moveBackground 5s infinite; " +
            "animation: moveBackground 5s infinite; animation-iteration-count: 1;");
    }
    window.setTimeout(function () {
        var name = prompt("What is your name?");
        $.post({
            url: '/views/post-highscore.php',
            data: {
                highscore: highscore,
                name: name
            },
            dataType: 'html'
        }).done(function (x, y, z) {
            debugger;
            alert("Success");
        }).fail(function (x, y, z) {
            debugger;
            alert("Success");
        });
    }, 5000);
}
function nextFigure() {
    if (randomFigure.isLanded()) {
        for (var _i = 0, _a = randomFigure.getCells(); _i < _a.length; _i++) {
            var cell = _a[_i];
            if (cell.getYPos() <= 0) {
                gameOver();
            }
        }
        for (var _b = 0, _c = randomFigure.getCells(); _b < _c.length; _b++) {
            var cell = _c[_b];
            fallenCells.push(cell);
            randomFigure = calculateRandomFigure();
        }
        highscore += 10;
    }
}
function keyPressed() {
    if (keyCode === DOWN_ARROW) {
        clearInterval(fallenDownInterval);
        fallenDownInterval = setInterval(function () {
            randomFigure.fallDown(grid, fallenCells);
        }, LevelVelocity.ARROW_DOWN);
    }
}
function keyReleased() {
    if (keyCode === DOWN_ARROW) {
        clearInterval(fallenDownInterval);
        fallenDownInterval = setInterval(function () {
            randomFigure.fallDown(grid, fallenCells);
        }, levelVelocity);
    }
}
function calculateRandomFigure() {
    var randomNumber = Math.floor(Math.random() * 7);
    switch (randomNumber) {
        case 0:
            return new SquareFigure("#FFD700");
        case 1:
            return new TFigure("#DC143C");
        case 2:
            return new IFigure("#1E90FF");
        case 3:
            return new LFigure("#9400D3");
        case 4:
            return new JFigure("#5F9EA0");
        case 5:
            return new SFigure("#FA8072");
        case 6:
            return new ZFigure("#C71585");
    }
    return new SquareFigure("blue");
}
