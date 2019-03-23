import {Component, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {Tournament} from "../custom";
import {CanDeactivateGuard, Deactivatable} from "../_services/can-deactivate-guard";
import {Observable} from "rxjs";

const toastr = require('toastr/toastr.js');

@Component({
    selector: 'cwt-admin-tournament-review',
    template: require('./admin-tournament-review.component.html'),
    providers: [CanDeactivateGuard]
})
export class AdminTournamentReviewComponent implements OnInit, Deactivatable {

    tournaments: Tournament[];
    tournamentBeingEdited: number;
    reviewBeingEdited: String;

    constructor(private requestService: RequestService) {
    }

    ngOnInit(): void {
        this.requestService.get<Tournament[]>("tournament")
            .subscribe(res => this.tournaments = res.sort((a, b) => new Date(b.created).getFullYear() - new Date(a.created).getFullYear()))
    }

    canDeactivate(): Observable<boolean> | Promise<boolean> | boolean {
        return this.tournaments.find(t => t.id === this.tournamentBeingEdited).review !== this.reviewBeingEdited
            ? confirm("You have unsaved changes. Do you really want to leave?")
            : true;
    }

    selectTournament(tournamentId: string) {
        this.tournamentBeingEdited = parseInt(tournamentId);
        this.reviewBeingEdited = this.tournaments.find(t => t.id === this.tournamentBeingEdited).review;
    }

    submit() {
        this.requestService.put<Tournament>(`tournament/${this.tournamentBeingEdited}`, {review: this.reviewBeingEdited} as Tournament)
            .subscribe(() => {
                toastr.success("Successfully saved.");
                this.tournaments[this.tournaments.findIndex(t => t.id === this.tournamentBeingEdited)].review =
                    this.reviewBeingEdited;
            })
    }
}
