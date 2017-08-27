import {Component, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {Game} from "../custom";

@Component({
    selector: 'cwt-playoffs-tree',
    template: require('./playoffs-tree.component.html')
})
export class PlayoffsTreeComponent implements OnInit {

    public games: Game[];

    constructor(private requestService: RequestService) {
    }

    public ngOnInit(): void {
        this.requestService.get<Game[]>('tournament/current/game/playoff')
            .subscribe(res => this.games = res);
    }
}
