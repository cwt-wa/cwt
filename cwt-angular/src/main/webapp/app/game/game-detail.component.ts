import {Component} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {RequestService} from "../_services/request.service";
import {Game, Rating, RatingType, User} from "../custom";

@Component({
    selector: 'cwt-game-detail',
    template: require('./game-detail.component.html')
})
export class GameDetailComponent {

    public game: Game;

    constructor(private requestService: RequestService, private route: ActivatedRoute) {
    }

    public ngOnInit(): void {
        this.route.paramMap.subscribe(res => {
            this.requestService.get<Game>(`game/${+res.get('id')}`)
                .subscribe(res => this.game = res);
        });
    }

    public get winningUser(): User {
        return this.game.scoreHome > this.game.scoreAway ? this.game.homeUser : this.game.awayUser;
    }

    public rate(rating: RatingType): void {
        this.requestService.post(`game/${this.game.id}/rating`, <Rating> {type: rating})
            .subscribe();
    }
}
