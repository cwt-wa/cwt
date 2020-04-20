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

@Component({
    selector: 'cwt-game-detail',
    styles: [`
      p.inGameChat {
        font-family: Verdana, Arial, Helvetica, sans-serif;
        line-height: 1.2rem;
        font-weight: bold;
        font-size: 1rem;
        color: white;
        margin-bottom: .5rem;
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
    showComments: boolean = true;
    statsAreLikelyBeingProcessed: boolean = false;
    private eventSource: EventSource;

    constructor(private requestService: RequestService, private route: ActivatedRoute,
                private authService: AuthService, private betService: BetService,
                private playoffService: PlayoffsService, @Inject(APP_CONFIG) private appConfig: AppConfig) {
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
            this.requestService.get<GameStats.GameStats[]>(`game/${gameId}/stats`)
                .subscribe(res => this.stats = res);
            this.requestService.get<GameDetailDto>(`game/${gameId}`)
                .subscribe(res => {
                    this.game = res;
                    this.gameWasPlayed = this.playoffService.gameWasPlayed(this.game);
                    this.statsAreLikelyBeingProcessed =
                        (this.game.scoreHome + this.game.scoreAway) * 15
                            <= (new Date(this.game.created).getTime() - Date.now()) / 1000;

                    if (!this.gameWasPlayed) {
                        return;
                    }

                    /*this.statsAreLikelyBeingProcessed &&*/ this.setupEventSource();
                    this.game.comments = this.game.comments.sort((c1, c2) => c1 > c2 ? 1 : -1);

                    if (this.game.playoff != null) {
                        this.requestService.get<PlayoffTreeBetDto[]>(`game/${gameId}/bets`)
                            .subscribe(res => this.betResult = this.betService.createBetResult(res));
                    }
                });
        });

        this.authenticatedUser = this.authService.getUserFromTokenPayload();
        if (this.authenticatedUser) this.initNewComment();
    }

    setupEventSource(): void {
        this.eventSource = new EventSource(`${this.appConfig.apiEndpoint}game/${this.game.id}/stats-listen`);
        this.eventSource.onmessage = console.log;
        this.eventSource.addEventListener('DONE', () => this.eventSource.close());
        this.eventSource.addEventListener('EVENT', e => this.stats.push(JSON.parse((<any>e).data)));
        this.eventSource.onerror = console.error;
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
