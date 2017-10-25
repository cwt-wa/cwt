import {Component} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {RequestService} from "../_services/request.service";
import {Comment, CommentDto, Game, JwtUser, Rating, RatingDto, RatingType, User} from "../custom";
import {AuthService} from "../_services/auth.service";

@Component({
    selector: 'cwt-game-detail',
    template: require('./game-detail.component.html')
})
export class GameDetailComponent {

    submittingComment: boolean;
    submittingRating: RatingType;
    game: Game;
    newComment: CommentDto;
    authenticatedUser: JwtUser;

    constructor(private requestService: RequestService, private route: ActivatedRoute,
                private authService: AuthService) {
    }

    public get winningUser(): User {
        return this.game.scoreHome > this.game.scoreAway ? this.game.homeUser : this.game.awayUser;
    }

    get likes(): Rating[] {
        return this.game.ratings.filter(r => r.type === 'LIKE');
    }

    get dislikes(): Rating[] {
        return this.game.ratings.filter(r => r.type === 'DISLIKE');
    }

    get lightsides(): Rating[] {
        return this.game.ratings.filter(r => r.type === 'LIGHTSIDE');
    }

    get darksides(): Rating[] {
        return this.game.ratings.filter(r => r.type === 'DARKSIDE');
    }

    get authenticatedUserRatings(): RatingType[] {
        return this.game.ratings.filter(r => r.user.id === this.authenticatedUser.id).map(r => r.type);
    }

    public ngOnInit(): void {
        this.route.paramMap.subscribe(res => {
            this.requestService.get<Game>(`game/${+res.get('id')}`)
                .subscribe(res => {
                    this.game = res;
                    this.game.comments = this.game.comments.sort((c1, c2) => c1 > c2 ? 1 : -1);
                });
        });

        this.authenticatedUser = this.authService.getUserFromTokenPayload();
        if (this.authenticatedUser) {
            this.initNewComment();
        }
    }

    public submitComment(): void {
        this.submittingComment = true;
        this.requestService.post<Comment>(`game/${this.game.id}/comment`, this.newComment)
            .finally(() => this.submittingComment = false)
            .subscribe(res => {
                this.game.comments.unshift(res);
                this.initNewComment();
            });
    }

    rate(ratingType: RatingType): void {
        this.submittingRating = ratingType;
        const payload: RatingDto = {type: ratingType, user: this.authenticatedUser.id};
        this.requestService.post<Rating>(`game/${this.game.id}/rating`, payload)
            .finally(() => this.submittingRating = null)
            .subscribe(res => this.game.ratings.push(res));
    }

    private initNewComment() {
        this.newComment = {user: this.authenticatedUser.id, body: null};
    }
}
