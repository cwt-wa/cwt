import {Component, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {GameCreationDto, GameTechWinDto, UserMinimalDto} from "../custom";
import {Observable} from "rxjs";
import {distinctUntilChanged} from "rxjs/operators";
import {Router} from "@angular/router";

const toastr = require('toastr/toastr.js');

@Component({
    selector: 'cwt-add-tech-win',
    template: require('./add-tech-win.component.html')
})
export class AddTechWinComponent implements OnInit {
    users: UserMinimalDto[];
    game: GameTechWinDto = {} as GameTechWinDto;

    typeAhead: (text: Observable<string>) => Observable<any[]> = (text: Observable<string>) => text
        .pipe(distinctUntilChanged())
        .map(term => this.users.filter(u => u.username.toLowerCase().indexOf(term.toLowerCase()) !== -1).map(u => u.id));
    typeAheadInputFormatter = (userId: number) => this.users.find(u => u.id === userId).username;
    typeAheadResultFormatter = (userId: number) => this.users.find(u => u.id === userId).username;

    constructor(private requestService: RequestService, private router: Router) {
    }

    ngOnInit(): void {
        this.requestService.get<UserMinimalDto[]>("user/still-in-tournament")
            .subscribe(res => this.users = res);
    }

    submit() {
        this.requestService.post<GameCreationDto>('game/tech-win', this.game)
            .subscribe(res => {
                this.router.navigateByUrl(`/games/${res.id}`);
                toastr.success("Successfully saved.");
            });
    }
}
