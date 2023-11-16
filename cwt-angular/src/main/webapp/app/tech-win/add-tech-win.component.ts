import {Component, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {GameCreationDto, GameTechWinDto, User, UserMinimalDto} from "../custom";
import {finalize} from "rxjs/operators";
import {Router} from "@angular/router";
import {Toastr} from "../_services/toastr";

@Component({
    selector: 'cwt-add-tech-win',
    template: require('./add-tech-win.component.html')
})
export class AddTechWinComponent implements OnInit {
    users: UserMinimalDto[];
    game: GameTechWinDto = {} as GameTechWinDto;
    remainingOpponents: User[];
    loadingRemainingOpponents: boolean = false;

    constructor(private requestService: RequestService, private router: Router, private toastr: Toastr) {
    }

    ngOnInit(): void {
        this.requestService.get<UserMinimalDto[]>("user/still-in-tournament")
            .subscribe(res => this.users = res);
    }

    onWinnerSelection() {
        this.loadingRemainingOpponents = true;
        this.requestService.get<User[]>(`user/${this.game.winner}/remaining-opponents`)
            .pipe(finalize(() => this.loadingRemainingOpponents = false))
            .subscribe(res => {
                this.remainingOpponents = res;
                if (this.remainingOpponents.length === 1) this.game.loser = this.remainingOpponents[0].id;
            });
    }

    submit() {
        this.requestService.post<GameCreationDto>('game/tech-win', this.game)
            .subscribe(res => {
                this.router.navigateByUrl(`/games/${res.id}`);
                this.toastr.success("Saved");
            });
    }
}
