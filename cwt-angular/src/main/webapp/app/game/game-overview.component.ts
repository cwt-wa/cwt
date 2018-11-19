import {Component, Inject, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {GameDetailDto, PageDto, Rating, RatingType} from "../custom";
import {APP_CONFIG, AppConfig} from "../app.config";

@Component({
    selector: 'cwt-game-overiew',
    template: require('./game-overview.component.html')
})
export class GameOverviewComponent implements OnInit {
    pageOfGames: PageDto<GameDetailDto> = <PageDto<GameDetailDto>> {size: 10, start: 1};
    loading: boolean;

    constructor(private requestService: RequestService, @Inject(APP_CONFIG) public appConfig: AppConfig) {
    }

    ngOnInit(): void {
        this.load();
    }

    load() {
        this.loading = true;

        this.requestService.getPaged<GameDetailDto>('game', this.pageOfGames)
            .subscribe(pageOfGames => {
                this.pageOfGames = pageOfGames;
            }, undefined, () => {
                this.loading = false;
            });
    }

    filterRatings(ratings: Rating[], type: RatingType): Rating[] {
        return ratings.filter(r => r.type === type);
    }
}
