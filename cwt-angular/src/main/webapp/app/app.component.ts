import {AfterViewInit, Component, OnInit} from "@angular/core";
import {WebAppViewService} from "./_services/web-app-view.service";
import {GmtClockComponent} from "./_util/gmt-clock.component";
import {Router} from "@angular/router";
import {RequestService} from "./_services/request.service";
import {AuthService} from "./_services/auth.service";
import {JwtUser, TetrisDto, UserMinimalDto} from "./custom";
import {Tetris} from "../tetris/sketch";
import {CanReportService} from "./_services/can-report.service";
import {Toastr} from "./_services/toastr";
import * as p5 from "p5";
import {TetrisGuest} from "../tetris/tetrisguest";

@Component({
    selector: 'my-app',
    template: require('./app.component.html'),
    providers: [GmtClockComponent]
})
export class AppComponent implements AfterViewInit, OnInit {

    public isNavCollapsed: boolean = true;
    public isAppleStandalone: boolean;
    public isStandalone: boolean;
    public highscores: TetrisDto[] = [];
    private authenticatedUser: JwtUser;
    private tetris: Tetris;
    private tetrisGuestName: string;
    private highscore: number;
    //@ts-ignore
    private newTetrisEntryId: number;

    constructor(private webAppViewService: WebAppViewService, private requestService: RequestService,
                private router: Router, private authService: AuthService, private canReportService: CanReportService,
                private toastr: Toastr) {
        this.isAppleStandalone = this.webAppViewService.isAppleStandalone;
        this.isStandalone = this.webAppViewService.isStandalone;
    }

    public ngOnInit(): void {
        this.easterEgg();
        this.requestService.get<{ token: string }>('auth/refresh').subscribe(
            res => {
                if (res == null || res.token == null) return this.authService.voidToken();
                this.authService.storeToken(res.token);
                this.authenticatedUser = this.authService.getUserFromTokenPayload();
                this.canReportService.requestInitialValue(this.authenticatedUser.id);
            },
            () => this.authService.voidToken()
        );
    }

    public ngAfterViewInit(): void {
        this.router.setUpLocationChangeListener();
    }

    public logOut(): void {
        this.authService.voidToken();
        this.authenticatedUser = null;
        window.location.href = '/';
    }

    easterEgg() {
        require('../tetris/styles/tetris.scss');
        const p5 = require('p5/lib/p5.js');

        let tetrisDiv = document.getElementById('tetris');
        tetrisDiv.classList.add("tetris-visible");
        tetrisDiv.style.width = window.innerWidth.toString() + "px";
        tetrisDiv.style.height = window.innerHeight + document.body.scrollHeight + "px";
        document.body.classList.add("tetris");

        new p5((p: p5) => {
            this.tetris = new Tetris(p);
            window.onresize = () => this.tetris.resize();

            this.tetris.onGameOver = (highscore: number) => {
                this.highscore = highscore;
                document.getElementById("tetris-gameover").classList.add("tetris-visible");
                document.body.classList.add('gameOver');

                const authenticatedUser: JwtUser = this.authService.getUserFromTokenPayload();

                if (authenticatedUser != null) {
                    this.requestService.post<TetrisDto>(`user/${authenticatedUser.id}/tetris`, highscore)
                        .subscribe(res => {
                            this.toastr.success("Highscore saved.");
                            this.highscores.push(res);
                            this.highscores = this.sortTetrisHighscore(this.highscores);
                            this.newTetrisEntryId = res.id;
                        });
                } else {
                    document.getElementById("tetris-game-over-entry").classList.add("tetris-visible");
                }

                this.requestService.get<TetrisDto[]>(`tetris`)
                    .subscribe(res => {
                        this.highscores = this.highscores.concat(res);
                        this.highscores = this.sortTetrisHighscore(this.highscores);
                    });
            };

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
        }, document.getElementById('tetris'));
   }

    //@ts-ignore
    private saveTetrisGuest() {
        if (!this.tetrisGuestName) {
            return;
        }
        this.requestService.get<UserMinimalDto[]>(`user`, {"username": this.tetrisGuestName}).subscribe(res => {
            if (res.length !== 0) {
                this.toastr.error("Username is registered. Please use another one.");
                return;
            }
            this.requestService.post<TetrisDto>(`tetris`, new TetrisGuest(this.highscore, this.tetrisGuestName))
                .subscribe(res => {
                    this.toastr.success("Highscore saved.");
                    this.highscores.push(res);
                    this.highscores = this.sortTetrisHighscore(this.highscores);
                    this.newTetrisEntryId = res.id;
                    document.getElementById("tetris-game-over-entry").style.display = "none";
                });
        });
    }

    private sortTetrisHighscore(highscore: TetrisDto[]): TetrisDto[] {
        return highscore.sort(
            (n1, n2) => n1.highscore - n2.highscore || new Date(n1.created).getTime() - new Date(n2.created).getTime()
        ).reverse();
    }

    closeTetris() {
        if (document.querySelector('canvas')) {
            this.tetris.close();
        }
        document.getElementById("tetris-gameover").classList.remove("tetris-visible");
        document.getElementById('tetris').classList.remove('tetris-visible');
        document.body.classList.remove('tetris');
        document.body.classList.remove('gameOver');
        this.tetris.tearDown();
    }
}
