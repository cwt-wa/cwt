import {Component, OnInit} from '@angular/core';
import {RequestService} from "./_services/request.service";
import {TournamentDetailDto} from "./custom";

@Component({
    selector: 'cwt-admin',
    template: require('./admin.component.html')
})

export class AdminComponent implements OnInit {

    tournament?: TournamentDetailDto;

    constructor(private requestService: RequestService) {
    }

    ngOnInit(): void {
        this.requestService.get<TournamentDetailDto>('tournament/current')
            .subscribe(res => this.tournament = res)
    }
}
