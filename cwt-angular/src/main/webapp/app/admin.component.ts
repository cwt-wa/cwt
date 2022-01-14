import {Component, OnInit} from '@angular/core';
import {TournamentDetailDto, TournamentUpdateDto} from "./custom";
import {CurrentTournamentService} from "./_services/current-tournament.service";
import {RequestService} from "./_services/request.service";
import {Toastr} from "./_services/toastr";

@Component({
    selector: 'cwt-admin',
    template: require('./admin.component.html')
})

export class AdminComponent implements OnInit {

    tournament?: TournamentDetailDto;

    constructor(private currentTournamentService: CurrentTournamentService,
                private toastr: Toastr,
                private requestService: RequestService) {
    }

    ngOnInit(): void {
        this.currentTournamentService.value.then(res => this.tournament = res);
    }

    archive(): void {
        const msg = "Do you really want to archive the current tournament?";
        if (!confirm(msg)) {
            return;
        }
        this.requestService.put<TournamentDetailDto>(
            `tournament/${this.tournament.id}`,
            {status: "ARCHIVED"} as TournamentUpdateDto)
                .subscribe((res: TournamentDetailDto) => {
                    this.toastr.success("Tournament has been archived.");
                    this.tournament = res;
                });
    }
}
