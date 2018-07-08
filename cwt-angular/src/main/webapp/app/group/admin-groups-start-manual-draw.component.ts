import {Component, Input} from '@angular/core';
import {Application, Group, GroupDto, User} from "../custom";
import {Observable} from "rxjs/Observable";
import {RequestService} from "../_services/request.service";

@Component({
    selector: 'cwt-admin-groups-start-manual-draw',
    template: require('./admin-groups-start-manual-draw.component.html')
})
export class AdminGroupsStartManualDrawComponent {

    @Input()
    groups: Group[];

    @Input()
    applications: Application[];

    private typeAheadForGroupMember: (text$: Observable<string>) => Observable<User[]>;
    private typeAheadInputFormatter: (value: User) => string;
    private typeAheadResultFormatter: (value: User) => string;

    public constructor(private requestService: RequestService) {
        this.typeAheadForGroupMember = (text$: Observable<string>) =>
            text$
                .distinctUntilChanged()
                .map(term =>
                    this.applications
                        .map(a => a.applicant)
                        .filter(u => !this.userIsDrawn(u))
                        .filter(a => a.username.toLowerCase().indexOf(term.toLowerCase()) !== -1));
        this.typeAheadInputFormatter = (value: User) => value.username;
        this.typeAheadResultFormatter = (value: User) => value.username;
    }

    public get drawnApplicants(): User[] {
        return this.applications
            .map(a => a.applicant)
            .filter(u => this.userIsDrawn(u));
    }

    public get undrawnApplicants(): User[] {
        return this.applications
            .map(a => a.applicant)
            .filter(u => !this.userIsDrawn(u));
    }

    public submit(): void {
        const body: GroupDto[] = this.groups
            .map(g => <GroupDto> {label: g.label, users: g.standings.map(s => s.user.id)});

        this.requestService.post('tournament/current/group/many', body)
            .subscribe();
    }

    private userIsDrawn(user: User): boolean {
        return !!this.groups
            .map(g => g.standings)
            .reduce((previousValue, currentValue) => previousValue.concat(currentValue))
            .map(s => s.user)
            .find(u => u && u.id === user.id);
    }
}
