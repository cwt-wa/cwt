import {Component, OnInit} from '@angular/core';
import {TournamentDetailDto} from "./custom";
import {CurrentTournamentService} from "./_services/current-tournament.service";

@Component({
    selector: 'cwt-admin',
    template: require('./admin.component.html')
})

export class AdminComponent implements OnInit {

    tournament?: TournamentDetailDto;

    constructor(private currentTournamentService: CurrentTournamentService) {
    }

    ngOnInit(): void {
        this.currentTournamentService.value.then(res => this.tournament = res);
    }
}
