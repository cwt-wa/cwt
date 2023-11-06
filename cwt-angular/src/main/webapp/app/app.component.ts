import {AfterViewInit, Component, HostBinding, OnInit} from "@angular/core";
import {WebAppViewService} from "./_services/web-app-view.service";
import {Router} from "@angular/router";
import {RequestService} from "./_services/request.service";
import {AuthService} from "./_services/auth.service";
import {JwtUser, TetrisDto, UserMinimalDto} from "./custom";
import {Tetris} from "../tetris/sketch";
import {CanReportService} from "./_services/can-report.service";
import {Toastr} from "./_services/toastr";
import {TetrisGuest} from "../tetris/tetrisguest";
import {ConfigurationService} from "./_services/configuration.service";
import {map} from "rxjs/operators";
import {forkJoin} from "rxjs";
import {CurrentTournamentService} from "./_services/current-tournament.service";
import {GoldWinnerService} from "./_services/gold-winner.service";

@Component({
    selector: 'my-app',
    template: require('./app.component.html'),
})
export class AppComponent implements AfterViewInit, OnInit {

    @HostBinding('class.nav-is-expanded') navIsExpandedHostClass: boolean = false;

    public isNavCollapsed: boolean = true;
    public isAppleStandalone: boolean;
    public isStandalone: boolean;
    public highscores: TetrisDto[];
    public eventSourceTwitchWebhook: boolean;
    private authenticatedUser: JwtUser;
    private tetris: Tetris;
    private tetrisGuestName: string;
    private highscore: number;
    //@ts-ignore
    private newTetrisEntryId: number;

    constructor(private webAppViewService: WebAppViewService, private requestService: RequestService,
                private router: Router, private authService: AuthService, private canReportService: CanReportService,
                private toastr: Toastr, private configurationService: ConfigurationService,
                private currentTournamentService: CurrentTournamentService,
                private goldWinnerService: GoldWinnerService) {
        this.isAppleStandalone = this.webAppViewService.isAppleStandalone;
        this.isStandalone = this.webAppViewService.isStandalone;
    }

    public ngOnInit(): void {
        this.authService.authState
            .then(async user => {
                this.authenticatedUser = user;
                !!this.authenticatedUser && this.canReportService.requestInitialValue(this.authenticatedUser.id);
                this.goldWinnerService.doHighlight(
                    await this.currentTournamentService.value,
                    this.authenticatedUser);
            });

        if (this.authService.validateToken()) {
            this.requestService.get<{ token: string }>('auth/refresh')
                .subscribe(res => {
                    this.authService.resolveAuthState(res ? res.token : null);
                    res.token && this.authService.storeToken(res.token);
                });
        } else {
            this.authService.resolveAuthState(null);
            this.authService.voidToken();
        }

        forkJoin([
            this.currentTournamentService.value,
            this.configurationService.requestByKeys('EVENT_SOURCE_TWITCH_WEBHOOK')
        ])
            .pipe(map(([tournament, config]) => ({
                tournament,
                config: this.eventSourceTwitchWebhook = config[0].value === 'true'
            })))
            .subscribe(({tournament, config}) =>
                this.eventSourceTwitchWebhook =
                    config && tournament && ["OPEN", "GROUP", "PLAYOFFS"].includes(tournament.status));

      // TODO from button click (perhaps user panel notification settings)
      Notification.requestPermission();
    }

    public ngAfterViewInit(): void {
        this.router.setUpLocationChangeListener();
    }

    public logOut(): void {
        this.authService.voidToken();
        this.authenticatedUser = null;
        window.location.href = '/';
    }

    async easterEgg() {
        this.highscores = [];

        document.body.classList.add("tetris");
        document.documentElement.classList.add("tetris");
        document.getElementById('tetris').classList.add("tetris-visible");
        document.getElementById("tetris-heading").classList.add("tetris-visible");

        //@ts-ignore
        let listener = document.addEventListener('keydown', (e) => {
            if (e.key === "Escape") {
                this.closeTetris();
                listener = null;
            }
        });

        const tetrisEntryPointScript = await import(/* webpackChunkName: "tetris" */ '../tetris/tetris');
        const tetrisEntryPoint = new tetrisEntryPointScript.TetrisEntryPoint((highscore: number) => {
            this.highscore = highscore;
            this.tetris.tearDown();
            document.getElementById("tetris-gameover").classList.add("tetris-visible");
            document.getElementById("tetris-heading").classList.remove("tetris-visible");
            document.body.classList.add('gameOver');
            if (this.authenticatedUser != null) {

                this.requestService.post<TetrisDto>(`user/${this.authenticatedUser.id}/tetris`, highscore)
                    .subscribe(res => {
                        this.toastr.success("Highscore saved.");
                        this.highscores.push(res);
                        this.newTetrisEntryId = res.id;
                        this.highscores = this.sortTetrisHighscore(this.highscores);
                    });
            } else {
                document.getElementById("tetris-game-over-entry").classList.add("tetris-visible");
            }

            this.requestService.get<TetrisDto[]>(`tetris`)
                .subscribe(res => {
                    this.highscores = this.highscores.concat(res);
                    this.highscores = this.sortTetrisHighscore(this.highscores);
                });
        });
        this.tetris = tetrisEntryPoint.tetris;
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
                    document.getElementById("tetris-game-over-entry").classList.remove("tetris-visible");
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
        window.onresize = null;
        document.getElementById("tetris-gameover").classList.remove("tetris-visible");
        document.getElementById('tetris').classList.remove('tetris-visible');
        document.body.classList.remove('tetris');
        document.documentElement.classList.remove('tetris');
        document.body.classList.remove('gameOver');
        this.highscores = null;
    }

    collapseNav() {
        this.isNavCollapsed = !this.isNavCollapsed;
        this.navIsExpandedHostClass = !this.isNavCollapsed;
    }
}
