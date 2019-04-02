import {AfterViewInit, Component, OnInit} from "@angular/core";
import {WebAppViewService} from "./_services/web-app-view.service";
import {GmtClockComponent} from "./_util/gmt-clock.component";
import {Router} from "@angular/router";
import {RequestService} from "./_services/request.service";
import {AuthService} from "./_services/auth.service";
import {JwtUser} from "./custom";
import {Tetris} from "../tetris/sketch";
import {CanReportService} from "./_services/can-report.service";

@Component({
    selector: 'my-app',
    template: require('./app.component.html'),
    providers: [GmtClockComponent]
})
export class AppComponent implements AfterViewInit, OnInit {

    public isNavCollapsed: boolean = true;
    public isAppleStandalone: boolean;
    public isStandalone: boolean;
    public tetrisHighscore: number;
    private authenticatedUser: JwtUser;

    constructor(private webAppViewService: WebAppViewService, private requestService: RequestService,
                private router: Router, private authService: AuthService, private canReportService: CanReportService) {
        this.isAppleStandalone = this.webAppViewService.isAppleStandalone;
        this.isStandalone = this.webAppViewService.isStandalone;
    }

    public ngOnInit(): void {
        //this.easterEgg();
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

        require('../tetris/styles/testTetris.css');
        const p5 = require('p5/lib/p5.js');
        document.body.classList.add("tetris");

        new p5((p: p5) => {
            const tetris = new Tetris(p);

            tetris.onGameOver = (highscore: number) => {
                this.tetrisHighscore = highscore;
            };

            p.setup = () => {
                tetris.setup()
            };

            p.draw = () => {
                tetris.draw()
            };

            p.keyPressed = () => {
                tetris.keyPressed();
            };

            p.keyReleased = () => {
                tetris.keyReleased();
            }
        }, document.getElementById('tetris'));
   }
}
