import {Component} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {RequestService} from "../_services/request.service";
import {Comment, CommentDto, Game, JwtUser, User} from "../custom";
import {AuthService} from "../_services/auth.service";

@Component({
    selector: 'cwt-game-detail',
    template: require('./game-detail.component.html')
})
export class GameDetailComponent {

    public game: Game;
    public comment: CommentDto;
    public authenticatedUser: JwtUser;

    constructor(private requestService: RequestService, private route: ActivatedRoute,
                private authService: AuthService) {
    }

    public ngOnInit(): void {
        this.route.paramMap.subscribe(res => {
            this.requestService.get<Game>(`game/${+res.get('id')}`)
                .subscribe(res => this.game = res);
        });

        this.authenticatedUser = this.authService.getUserFromTokenPayload();
        if (this.authenticatedUser) {
            this.comment = {user: this.authenticatedUser.id, body: null};
        }
    }

    public get winningUser(): User {
        return this.game.scoreHome > this.game.scoreAway ? this.game.homeUser : this.game.awayUser;
    }

    public submitComment(): void {
        this.requestService.post<Comment>(`game/${this.game.id}/comment`, this.comment)
            .subscribe(res => this.game.comments.push(res));
    }
}
