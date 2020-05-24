import {Tetris} from "./sketch";
import {TetrisDto} from "../app/custom";
import * as p5 from "p5";

export class TetrisEntryPoint {

    public tetris: Tetris;
    public highscores: TetrisDto[];

    constructor(onGameOver: (highscore: number) => void) {
        const p5 = require('p5/lib/p5.js');
        new p5((p: p5) => {
            this.tetris = new Tetris(p);
            window.onresize = () => this.tetris.resize();

            this.tetris.onGameOver = onGameOver;

            p.setup = () => {
                this.tetris.setup()
            };

            p.draw = () => {
                this.tetris.draw()
            };

            p.keyPressed = () => {
                this.tetris.keyPressed();
            };

            p.keyReleased = () => {
                this.tetris.keyReleased();
            }
        }, document.getElementById('tetris'))
    }
}

