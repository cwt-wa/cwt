import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {RequestService} from "../_services/request.service";
import {Comment, CommentDto, GameDetailDto, JwtUser, PlayoffTreeBetDto, Rating, RatingDto, RatingType} from "../custom";
import {AuthService} from "../_services/auth.service";
import {finalize} from "rxjs/operators";
import {BetResult, BetService} from "../_services/bet.service";
import {PlayoffsService} from "../_services/playoffs.service";
import {GameStats} from "./game-stats.component";
import {APP_CONFIG, AppConfig} from "../app.config";
import {ConfigurationService} from "../_services/configuration.service";

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
      img.map {
        width: 100%;
        border-radius: .3rem;
      }
    `],
    template: require('./game-detail.component.html')
})
export class GameDetailComponent implements OnInit, OnDestroy {

    submittingComment: boolean;
    submittingRating: RatingType;
    game: GameDetailDto;
    newComment: CommentDto;
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

    constructor(private requestService: RequestService, private route: ActivatedRoute,
                private authService: AuthService, private betService: BetService,
                private playoffService: PlayoffsService, @Inject(APP_CONFIG) private appConfig: AppConfig,
                private configurationService: ConfigurationService) {
    }

    get authenticatedUserRatings(): RatingType[] {
        return this.authenticatedUser
            ? this.game.ratings.filter(r => r.user.id === this.authenticatedUser.id).map(r => r.type)
            : [];
    }

    get ratings(): { [key in RatingType]: Rating[] } {
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
                    this.loadingStats = false;
                    this.gameWasPlayed = this.playoffService.gameWasPlayed(this.game);

                    if (!this.gameWasPlayed) {
                        return;
                    }

                    this.setupEventSource();
                    this.game.comments = this.game.comments.sort((c1, c2) => c1 > c2 ? -1 : 1);

                    if (this.game.playoff != null) {
                        this.requestService.get<PlayoffTreeBetDto[]>(`game/${gameId}/bets`)
                            .subscribe(res => this.betResult = this.betService.createBetResult(res, this.authenticatedUser));
                    }
                });
        });

        this.authService.authState.then(user => {
            this.authenticatedUser = user;
            user && this.initNewComment();
        });

        this.configurationService.requestByKeys("WA_3_8_WARNING")
            .subscribe(res => this.waVersionWarn = res[0].value === 'true');
    }

    setupEventSource(): void {
        this.loadingStats = true;
        this.statsAreBeingProcessed = true;
        this.eventSource = new EventSource(`${this.appConfig.apiEndpoint}game/${this.game.id}/stats-listen`);
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
        this.requestService.post<Comment>(`game/${this.game.id}/comment`, this.newComment)
            .pipe(finalize(() => this.submittingComment = false))
            .subscribe(res => {
                this.game.comments.unshift(res);
                this.initNewComment();
            });
    }

    rate(ratingType: RatingType): void {
        this.submittingRating = ratingType;
        const payload: RatingDto = {type: ratingType, user: this.authenticatedUser.id};
        this.requestService.post<Rating>(`game/${this.game.id}/rating`, payload)
            .pipe(finalize(() => this.submittingRating = null))
            .subscribe(res => this.game.ratings.push(res));
    }

    private initNewComment() {
        this.newComment = {user: this.authenticatedUser.id, body: null};
    }
}
