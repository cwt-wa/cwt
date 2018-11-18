import {Component, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {GameDetailDto, PageDto} from "../custom";

@Component({
    selector: 'cwt-game-overiew',
    template: require('./game-overview.component.html')
})
export class GameOverviewComponent implements OnInit {
    pageOfGames: PageDto<GameDetailDto> = <PageDto<GameDetailDto>> {size: 10, start: 1};
    loading: boolean;

    constructor(private requestService: RequestService) {
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
}
