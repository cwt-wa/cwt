import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {RequestService} from "../_services/request.service";
import {
    CommentCreationDto,
    CommentDto,
    GameDetailDto,
    JwtUser,
    PlayoffTreeBetDto,
    RatingCreationDto,
    RatingDto,
    RatingType,
    StreamDto
} from "../custom";
import {AuthService} from "../_services/auth.service";
import {finalize} from "rxjs/operators";
import {BetResult, BetService} from "../_services/bet.service";
import {PlayoffsService} from "../_services/playoffs.service";
import {GameStats} from "./game-stats.component";
import {APP_CONFIG, AppConfig} from "../app.config";
import {ConfigurationService} from "../_services/configuration.service";
import {Toastr} from "../_services/toastr";

const rnd = (max: number) => Math.ceil(Math.random() * max) - (max / 2);

@Component({
    selector: 'cwt-game-detail',
    styles: [`
      p.inGameChat {
        font-family: Verdana, Arial, Helvetica, sans-serif;
        line-height: 1.2rem;
        font-weight: bold;
        font-size: 1rem;
        color: #aaa;
        margin-bottom: .5rem;
      }
      .ratings i {
        width: 1rem;
        display: inline-block;
      }
      .ratings .vote {
        width: 2.5rem;
        font-weight: bold;
        display: inline-block;
      }
      img.map {
        width: 100%;
        border-radius: .3rem;
      }
      .mt-children > * {
        margin-top: 1.5rem;
        margin-bottom: 1.5rem;
      }
      .rate {
        display: flex;
        justify-content: space-between;
        flex-wrap: nowrap;
        align-items: center;
        margin-left: 2rem;
        margin-right: 2rem;
        width: 11.5rem;
        font-size: 2rem;
        white-space: nowrap;
      }
      .rate .circles * {
        border-width: 1px;
        position: relative;
        z-index: 0;
      }
      .rate .circles *[disabled] {
        opacity: 1;
     }
      .rate .circles *.more {
        transform: scale(1.3);
        z-index: 1;
      }
      .rate .circles *.pressing::after,
      .rate .circles *.pressed::after {
        position: absolute;
        z-index: 2;
        top: -15px;
        background: #322a21;
        border-radius: 50%;
        height: 1.7rem;
        width: 1.7rem;
      }
      .rate .circles *:first-child.pressed::after,
      .rate .circles *:first-child.pressing::after {
        right: 30px;
      }
      .rate .circles *:nth-child(2).pressed::after,
      .rate .circles *:nth-child(2).pressing::after {
        right: -10px;
      }
      .rate .circles *.pressed::after {
        font-family: 'Font Awesome 5 Free';
        content: "\\f007";
        font-weight: 900;
        font-size: 1rem;
        transform: rotate(${rnd(15)}deg);
      }
      .rate .circles *.pressing::after {
        content: "";
        background-image: url("/loading.gif");
        background-position: center;
        background-size: 1rem;
        background-repeat: no-repeat;
      }
      .rate .circles *:first-child i {
        transform: rotate(${rnd(30)}deg);
      }
      .rate .circles *:nth-child(2) i {
        position: relative;
        transform: rotate(${rnd(30)}deg);
      }
      .rate i {
        width: 1rem;
      }
      .rate .circles .bet-result {
        font-size: 1rem;
        padding: 0;
        padding-top: .6rem;
        height: 2.8rem;
        width: 2.8rem;
      }
      .rate .circles .bet-result:first-child {
        background-color: #a69179;
      }
      .rate .circles .bet-result:nth-child(2) {
        background-color: #8e775e;
      }
      .rate .circles *:not(.bet-result):nth-child(2) {
        margin-left: -5px;
      }
    `],
    template: require('./game-detail.component.html')
})
export class GameDetailComponent implements OnInit, OnDestroy {

    submittingComment: boolean;
    submittingRating: RatingType;
    game: GameDetailDto;
    streams: StreamDto[];
    newComment: CommentCreationDto;
    authenticatedUser: JwtUser;
    betResult: BetResult;
    gameWasPlayed: boolean = true;
    stats: GameStats.GameStats[] = [];
    statsForRound?: number = 1;
    rightColumnViewToggle: 'MAPS' | 'IN_GAME_CHAT' | 'COMMENTS' = 'COMMENTS';
    statsAreBeingProcessed: boolean = false;
    private eventSource: EventSource;
    loadingStats: boolean = true;
    hasOlderVersion: boolean = false;
    waVersionWarn: boolean = false;
    homeUserAbbr: string;
    awayUserAbbr: string;

    constructor(private requestService: RequestService, private route: ActivatedRoute,
                private authService: AuthService, private betService: BetService,
                private playoffService: PlayoffsService, @Inject(APP_CONFIG) private appConfig: AppConfig,
                private configurationService: ConfigurationService,
                private toastr: Toastr) {
    }

    get authenticatedUserRatings(): RatingType[] {
        return this.authenticatedUser
            ? this.game.ratings.filter(r => r.user.id === this.authenticatedUser.id).map(r => r.type)
            : [];
    }

    get ratings(): { [key in RatingType]: RatingDto[] } {
        return {
            DARKSIDE: this.game.ratings.filter(r => r.type === "DARKSIDE"),
            LIGHTSIDE: this.game.ratings.filter(r => r.type === "LIGHTSIDE"),
            LIKE: this.game.ratings.filter(r => r.type === "LIKE"),
            DISLIKE: this.game.ratings.filter(r => r.type === "DISLIKE"),
        };
    }

    public ngOnInit(): void {
        this.route.paramMap.subscribe(routeParam => {
            const gameId = +routeParam.get('id');
            this.requestService.get<GameDetailDto>(`game/${gameId}`)
                .subscribe(res => {
                    this.game = res;
                    this.homeUserAbbr = this.game.homeUser.username.toUpperCase().slice(0, 3);
                    this.awayUserAbbr = this.game.awayUser.username.toUpperCase().slice(0, 3);
                    this.loadingStats = false;
                    this.gameWasPlayed = this.playoffService.gameWasPlayed(this.game);

                    if (!this.gameWasPlayed) {
                        return;
                    }

                    this.retrieveGameStats();
                    this.game.comments = this.game.comments.sort((c1, c2) =>
                        new Date(c2.created).getTime() - new Date(c1.created).getTime());

                    if (this.game.playoff != null) {
                        this.requestService.get<PlayoffTreeBetDto[]>(`game/${gameId}/bets`)
                            .subscribe(res => this.betResult = this.betService.createBetResult(res, this.authenticatedUser));
                    }
                });
            this.requestService.get<StreamDto[]>(`game/${gameId}/stream`)
                .subscribe(res => this.streams = res)
        });

        this.authService.authState.then(user => {
            this.authenticatedUser = user;
            user && this.initNewComment();
        });

        this.configurationService.requestByKeys("WA_3_8_WARNING")
            .subscribe(res => this.waVersionWarn = res[0].value === 'true');
    }

    retrieveGameStats() {
        const now = Date.now();
        const reportedAt = new Date(this.game.reportedAt).getTime();
        const reportedAgo = now - reportedAt;
        if (reportedAgo <= 1000 * 60 * 4) {
            this.setupEventSource();
        } else {
            this.requestService.get<GameStats.GameStats[]>(`game/${this.game.id}/stats`)
                .subscribe(res => this.stats = res);
        }
    }

    setupEventSource(): void {
        this.loadingStats = true;
        this.statsAreBeingProcessed = true;
        this.eventSource = new EventSource(
            `${this.appConfig.apiEndpoint}game/${this.game.id}`
                + `/stats-listen?=quantity=${this.game.replayQuantity}`);
        this.eventSource.addEventListener('DONE', () => {
            this.eventSource.close();
            this.statsAreBeingProcessed = false;
            this.loadingStats = false;
        });
        this.eventSource.addEventListener('EVENT', e => {
            const newStats: GameStats.GameStats = JSON.parse((<any>e).data);
            if (!this.stats.find(s => s.startedAt === newStats.startedAt)) {
                this.stats.push(newStats);
                this.hasOlderVersion = this.stats.find(s => s.engineVersion != null && !s.engineVersion.startsWith('3.8')) != null;
            }
            this.loadingStats = false;
        });
    }

    ngOnDestroy() {
        this.eventSource && this.eventSource.close();
    }

    public submitComment(): void {
        this.submittingComment = true;
        this.requestService.post<CommentDto>(`game/${this.game.id}/comment`, this.newComment)
            .pipe(finalize(() => this.submittingComment = false))
            .subscribe(res => {
                this.game.comments.unshift(res);
                this.initNewComment();
            });
    }

    rate(ratingType: RatingType): void {
        const payload: RatingCreationDto = {type: ratingType, user: this.authenticatedUser.id};
        const alreadyRated = this.game.ratings.find(r => r.type === payload.type && r.user.id === payload.user);
        if (alreadyRated) {
            this.toastr.success(`You’ve already ${ratingType.toLowerCase()}d this game.`);
            return;
        }
        this.submittingRating = ratingType;
        this.requestService.post<RatingDto>(`game/${this.game.id}/rating`, payload)
            .pipe(finalize(() => this.submittingRating = null))
            .subscribe(res => {
                const idx = this.game.ratings.findIndex(r => r.id === res.id);
                if (idx !== -1) {
                    this.game.ratings[idx] = res;
                    this.toastr.success("Updated your rating.");
                } else {
                    this.game.ratings.push(res);
                    this.toastr.success(`${ratingType[0]}${ratingType.slice(1).toLowerCase()}d this game.`);
                }
            });
    }

    private initNewComment() {
        this.newComment = {author: this.authenticatedUser.id, body: null};
    }
}
