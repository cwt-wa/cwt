import {debounceTime, distinctUntilChanged, map} from 'rxjs/operators';
import {Component, OnInit} from '@angular/core';
import {Observable} from "rxjs";
import {RequestService} from "./_services/request.service";
import {User} from "./custom";
import {Toastr} from "./_services/toastr";

@Component({
    selector: 'cwt-admin-tournament-start',
    template: require('./admin-tournament-start.component.html')
})
export class AdminTournamentStartComponent implements OnInit {
    public usernameTypeAhead: (text$: Observable<string>) => Observable<string[]>;
    public allUsers: User[];
    public moderators: User[];
    public usernameOfUserToAdd: string;

    constructor(private requestService: RequestService, private toastr: Toastr) {
        this.moderators = [];

        this.usernameTypeAhead = (text$: Observable<string>) =>
            text$
                .pipe(debounceTime(200))
                .pipe(distinctUntilChanged()).pipe(
                map(term =>
                    term.length < 2
                        ? []
                        : this.allUsers
                            .map(u => u.username)
                            .filter(v => v.toLowerCase().indexOf(term.toLowerCase()) > -1)
                            .slice(0, 10)));
    }

    ngOnInit(): void {
        this.requestService.get('user').subscribe(
            (res: User[]) => this.allUsers = res);
    }

    public onUsernameInputKeyPress(e: KeyboardEvent): void {
        if (e.key !== 'Enter') {
            return;
        }

        e.preventDefault();
        this.addModerator();
    }

    public addModerator() {
        const userToAdd: any = this.allUsers.find(u => u.username === this.usernameOfUserToAdd);

        if (!userToAdd) {
            return;
        }

        this.moderators.push(userToAdd);
    }

    public removeModerator(user: any) {
        this.moderators.splice(this.moderators.indexOf(user), 1);
    }

    public submit(): void {
        const payload: { moderatorIds: number[] } = {moderatorIds: this.moderators.map(m => m.id)};
        this.requestService.post('tournament', payload).subscribe(
            () => this.toastr.success('Tournament has been started'));
    }
}
