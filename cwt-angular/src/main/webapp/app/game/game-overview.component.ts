import {Component, Inject, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {GameDetailDto, PageDto, Rating, RatingType} from "../custom";
import {APP_CONFIG, AppConfig} from "../app.config";
import {finalize} from "rxjs/operators";

@Component({
    selector: 'cwt-game-overiew',
    template: require('./game-overview.component.html')
})
export class GameOverviewComponent implements OnInit {
    pageOfGames: PageDto<GameDetailDto> = <PageDto<GameDetailDto>> {size: 10, start: 0};
    loading: boolean;

    constructor(private requestService: RequestService, @Inject(APP_CONFIG) public appConfig: AppConfig) {
    }

    ngOnInit(): void {
        this.load();
    }

    sort(sortable: string, sortAscending: boolean) {
        this.pageOfGames.sortBy = sortable;
        this.pageOfGames.sortAscending = sortAscending;
        this.pageOfGames.start = 0;
        this.load();
    }

    goTo(start: number) {
        this.pageOfGames.start = start;
        this.load();
    }

    load() {
        this.loading = true;

        this.requestService.getPaged<GameDetailDto>('game', this.pageOfGames)
            .pipe(finalize(() => this.loading = false))
            .subscribe(pageOfGames => this.pageOfGames = pageOfGames);
    }

    filterRatings(ratings: Rating[], type: RatingType): Rating[] {
        return ratings.filter(r => r.type === type);
    }
}
