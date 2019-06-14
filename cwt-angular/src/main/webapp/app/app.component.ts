import {AfterViewInit, Component, OnInit} from "@angular/core";
import {WebAppViewService} from "./_services/web-app-view.service";
import {GmtClockComponent} from "./_util/gmt-clock.component";
import {Router} from "@angular/router";
import {RequestService} from "./_services/request.service";
import {AuthService} from "./_services/auth.service";
import {JwtUser, TetrisDto} from "./custom";
import {Tetris} from "../tetris/sketch";
import {CanReportService} from "./_services/can-report.service";
import {Toastr} from "./_services/toastr";

@Component({
    selector: 'my-app',
    template: require('./app.component.html'),
    providers: [GmtClockComponent]
})
export class AppComponent implements AfterViewInit, OnInit {

    public isNavCollapsed: boolean = true;
    public isAppleStandalone: boolean;
    public isStandalone: boolean;
    public highscores: TetrisDto[];
    private authenticatedUser: JwtUser;
    private tetris: Tetris;

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

            this.tetris.onGameOver = (highscore: number) => {
                document.getElementById("tetris-gameover").classList.add("tetris-visible");
                document.body.classList.add('gameOver');

                const authenticatedUser: JwtUser = this.authService.getUserFromTokenPayload();

                if (authenticatedUser != null) {
                    this.requestService.post<number>(`user/${authenticatedUser.id}/tetris`, highscore)
                        .subscribe(() => this.toastr.success("Highscore saved."));
                } else {
                    this.requestService.post<number>(`tetris`, highscore)
                        .subscribe(() => this.toastr.success("Highscore saved."));
                }

                this.requestService.get<TetrisDto[]>(`tetris`)
                    .subscribe(res => this.highscores = res);
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

    closeTetris() {
        if (document.querySelector('canvas')) {
            this.tetris.close();
        }
        document.getElementById("tetris-gameover").classList.remove("tetris-visible");
        document.getElementById('tetris').classList.remove('tetris-visible');
        document.body.classList.remove('tetris');
        document.body.classList.remove('gameOver');
    }
}
