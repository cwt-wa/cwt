import {Component, Inject, Input, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {GameDetailDto, PageDto, RatingDto, RatingType, StreamDto} from "../custom";
import {APP_CONFIG, AppConfig} from "../app.config";
import {finalize} from "rxjs/operators";
import {Location} from "@angular/common";

@Component({
    selector: 'cwt-game-overview',
    template: require('./game-overview.component.html'),
    styles: [`
        .twitch-active {
            opacity: .25;
            transition: opacity .8s;
        }
    `]
})
export class GameOverviewComponent implements OnInit {

    @Input() user: number;

    pageOfGames: PageDto<GameDetailDto> = <PageDto<GameDetailDto>>{size: 10, start: 0};
    loading: boolean;
    twitchActive = false
    twitchies: StreamDto[];
    unlinkedTwitchies: StreamDto[];

    constructor(private requestService: RequestService,
                @Inject(APP_CONFIG) public appConfig: AppConfig,
                private location: Location) {
    }

    ngOnInit(): void {
        this.twitchActive = this.location.path(false).endsWith('/streams');
        if (this.twitchActive) {
            this.loadTwitchies();
        } else {
            this.load();
        }
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
        const queryParam = {
            ...(this.user != null && {user: this.user}),
            ...this.pageOfGames
        };
        this.requestService.getPaged<GameDetailDto>('game', queryParam)
            .pipe(finalize(() => this.loading = false))
            .subscribe(pageOfGames => this.pageOfGames = pageOfGames);
    }

    toggleTwitchies() {
        if (this.twitchActive) {
            this.location.replaceState('/streams')
            this.loadTwitchies();
        } else {
            this.location.replaceState('/games');
            this.load();
        }
    }


    private loadTwitchies() {
        if (this.twitchies?.length) return;
        this.loading = true
        this.requestService.get<StreamDto[]>('stream')
            .pipe(finalize(() => this.loading = false))
            .subscribe(res => {
                this.twitchies = res
                    .filter(s => s.game != null)
                    .sort((s1, s2) => (new Date(s2.game.reportedAt).getTime()
                        - new Date(s1.game.reportedAt).getTime()))
                this.unlinkedTwitchies = res
                    .filter(s => s.game == null)
                    .sort((s1, s2) => (new Date(s2.createdAt).getTime()
                        - new Date(s1.createdAt).getTime()))
            });
    }

    filterRatings(ratings: RatingDto[], type: RatingType): RatingDto[] {
        return ratings.filter(r => r.type === type);
    }
}
