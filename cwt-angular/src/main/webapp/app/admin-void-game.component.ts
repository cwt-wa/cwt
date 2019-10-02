import {Component, OnInit} from '@angular/core';
import {RequestService} from "./_services/request.service";
import {GroupWithGamesDto} from "./custom";
import {finalize} from "rxjs/operators";
import {Toastr} from "./_services/toastr";
import {Router} from "@angular/router";

@Component({
    selector: 'cwt-admin-void-game',
    template: require('./admin-void-game.component.html')
})

export class AdminVoidGameComponent implements OnInit {

    loading: boolean = false;
    groups: GroupWithGamesDto[];
    gameToVoid: string;

    constructor(private requestService: RequestService,
                private toastr: Toastr,
                private router: Router) {
    }

    ngOnInit(): void {
        this.loading = true;

        this.requestService.get<GroupWithGamesDto[]>(`tournament/current/group`)
            .pipe(finalize(() => this.loading = false))
            .subscribe(res => this.groups = res);
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
                this.toastr.success("The game has been voided.");
                this.router.navigateByUrl(`/games/${this.gameToVoid}`);
            });
    }
}
