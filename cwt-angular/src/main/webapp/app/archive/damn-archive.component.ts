import {Component, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {TournamentDto} from "../custom";

@Component({
    selector: 'cwt-damn-archive',
    template: require('./damn-archive.component.html'),
})
export class DamnArchiveComponent implements OnInit {

    public tournaments: TournamentDto[];

    constructor(private requestService: RequestService) {
    }

    ngOnInit(): void {
        this.requestService.get<TournamentDto[]>('tournament/archive')
            .subscribe(res => this.tournaments = res);
    }
}
