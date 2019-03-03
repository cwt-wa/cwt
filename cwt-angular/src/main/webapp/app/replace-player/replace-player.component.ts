import {Component, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {ReplacePlayerDto, User, UserMinimalDto} from "../custom";
import {Observable} from "rxjs";
import {debounceTime, distinctUntilChanged, map, switchMap, tap} from "rxjs/operators";
import {Router} from "@angular/router";

const toastr = require('toastr/toastr.js');

@Component({
    selector: 'cwt-replace-player',
    template: require('./replace-player.component.html')
})
export class ReplacePlayerComponent implements OnInit {

    users: UserMinimalDto[];
    replacement: ReplacePlayerDto = {} as ReplacePlayerDto;
    replacementSuggestions: User[];

    constructor(private requestService: RequestService, private router: Router) {
    }

    typeAheadToBeReplaced = (text$: Observable<string>) =>
        text$
            .pipe(distinctUntilChanged())
            .map(term => this.users
                .filter(u => u.username.toLowerCase().indexOf(term.toLowerCase()) !== -1)
                .map(u => u.id));

    typeAheadInputFormatter = (userId: number) => this.users.find(u => u.id === userId).username;
    typeAheadResultFormatter = (userId: number) => this.users.find(u => u.id === userId).username;

    typeAheadReplacement = (text$: Observable<string>) =>
        text$
            .pipe(
                distinctUntilChanged(),
                debounceTime(200),
                switchMap(term => this.requestService.get<User[]>("user", {term})
                    .map(users => users.filter(u => this.users.map(u1 => u1.id).indexOf(u.id) === -1))
                    .pipe(
                        tap(x => this.replacementSuggestions = x),
                        map(value => value.map(v => v.id))
                    ))
            );

    typeAheadReplacementInputFormatter = (userId: number) => this.replacementSuggestions.find(u => u.id === userId).username;
    typeAheadReplacementResultFormatter = (userId: number) => this.replacementSuggestions.find(u => u.id === userId).username;

    ngOnInit(): void {
        this.requestService.get<UserMinimalDto[]>("tournament/current/group/users")
            .subscribe(res => this.users = res);
    }

    submit() {
        this.requestService.post<void>('group/replace', this.replacement as ReplacePlayerDto)
            .subscribe(
                () => {
                    this.router.navigateByUrl('/groups');
                    toastr.success("Successfully saved.");
                });
    }
}
