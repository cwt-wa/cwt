import {AfterViewInit, Component, OnInit} from "@angular/core";
import {WebAppViewService} from "./_services/web-app-view.service";
import {GmtClockComponent} from "./_util/gmt-clock.component";
import {Router} from "@angular/router";
import {RequestService} from "./_services/request.service";
import {AuthService} from "./_services/auth.service";
import {JwtUser} from "./custom";
import {Tetris} from "../tetris/sketch";

@Component({
    selector: 'my-app',
    template: require('./app.component.html'),
    providers: [GmtClockComponent]
})
export class AppComponent implements AfterViewInit, OnInit {

    public isNavCollapsed: boolean = true;
    public isAppleStandalone: boolean;
    public isStandalone: boolean;
    public canReport: boolean;
    private authenticatedUser: JwtUser;

    constructor(private webAppViewService: WebAppViewService, private requestService: RequestService,
                private router: Router, private authService: AuthService) {
        this.isAppleStandalone = this.webAppViewService.isAppleStandalone;
        this.isStandalone = this.webAppViewService.isStandalone;
    }

    public ngOnInit(): void {
        this.easterEgg();
        this.requestService.get<{ token: string }>('auth/refresh').subscribe(
            res => {
                this.authService.storeToken(res.token);
                this.authenticatedUser = this.authService.getUserFromTokenPayload();

                this.requestService.get<boolean>(`user/${this.authenticatedUser.id}/can-report`)
                    .subscribe(res => this.canReport = res);
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

    public tetrisHtml: string;

    easterEgg() {
        this.tetrisHtml = require('../tetris/views/tetris.html');
        const p5 = require('p5/lib/p5.js');

        new p5(function (p: p5) {
            const tetris = new Tetris(p);

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
        });
   }
}
