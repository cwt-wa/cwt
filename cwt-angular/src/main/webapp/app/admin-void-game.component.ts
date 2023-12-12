import {Component, OnInit} from '@angular/core';
import {RequestService} from "./_services/request.service";
import {GroupWithGamesDto, PlayoffGameDto, TournamentDetailDto} from "./custom";
import {finalize} from "rxjs/operators";
import {Toastr} from "./_services/toastr";
import {Router} from "@angular/router";
import {CurrentTournamentService} from "./_services/current-tournament.service";

@Component({
    selector: 'cwt-admin-void-game',
    template: require('./admin-void-game.component.html')
})

export class AdminVoidGameComponent implements OnInit {

    loading: boolean = false;
    groups: GroupWithGamesDto[];
    gameToVoid: string;
    tournament: TournamentDetailDto;
    playoffs: PlayoffGameDto[];
    isNeitherTournamentStatus: boolean = false;
    noCurrentTournament: boolean;

    constructor(private requestService: RequestService,
                private toastr: Toastr,
                private router: Router,
                private currentTournamentService: CurrentTournamentService) {
    }

    ngOnInit(): void {
        this.loading = true;

        this.currentTournamentService.value.then(res => {
            this.tournament = res;

            if (this.tournament == null) {
                this.noCurrentTournament = true;
            } else if (this.tournament.status === "GROUP") {
                this.requestService.get<GroupWithGamesDto[]>(`tournament/current/group`)
                    .pipe(finalize(() => this.loading = false))
                    .subscribe(res => this.groups = res);
            } else if (this.tournament.status === "PLAYOFFS") {
                this.requestService.get<PlayoffGameDto[]>(`tournament/current/game/playoff`, {voidable: "true"})
                    .pipe(finalize(() => this.loading = false))
                    .subscribe(res => this.playoffs = res);
            } else {
                this.isNeitherTournamentStatus = true;
            }
        });
    }

    onSubmit() {
        const gameToVoid = this.groups
            .reduce((prev, curr) => {
                prev.push(...curr.games);
                return prev;
            }, [])
            .filter(g => g.id === parseInt(this.gameToVoid))[0];

        const confirmation = confirm(`Do you really want to void this game?

${gameToVoid.homeUser.username} ${gameToVoid.scoreHome}–${gameToVoid.scoreAway} ${gameToVoid.awayUser.username}`);

        if (!confirmation) return;

        this.requestService
            .post(`game/${this.gameToVoid}/void`)
            .subscribe(() => {
                this.toastr.success("The game has been voided");
                this.router.navigateByUrl(`/games/${this.gameToVoid}`);
            });
    }
}
