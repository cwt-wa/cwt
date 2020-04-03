import {Component, Input, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {GameStats} from "../custom";
import {ActivatedRoute} from "@angular/router";

@Component({
    selector: 'cwt-game-stats',
    template: `
        <p>Here's a new component</p>
    `
})
export class GameStatsComponent implements OnInit {

    @Input() gameId: number;

    stats: GameStats;

    constructor(private requestService: RequestService, private route: ActivatedRoute) {
    }

    ngOnInit(): void {
        this.route.paramMap.subscribe(routeParam =>
            this.requestService.get<GameStats>(`game/${+routeParam.get('id')}/stats`)
                .subscribe(res => this.stats = res));
    }
}
