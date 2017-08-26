import {Component, OnInit} from '@angular/core';
import {Application, Group, GroupDto, GroupStanding, User} from "../custom";
import {GroupService} from "../_services/group.service";
import {Observable} from "rxjs/Observable";
import {RequestService} from "../_services/request.service";

@Component({
    selector: 'cwt-admin-groups-start',
    template: require('./admin-groups-start.component.html')
})
export class AdminGroupsStartComponent implements OnInit {

    // TODO Maybe allow num of groups and users per group to be dynamic?
    private static readonly NUMBER_OF_GROUPS: number = 8;
    private static readonly USERS_PER_GROUP: number = 4;
    private groups: Group[];
    private applications: Application[];
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

    public ngOnInit(): void {
        this.requestService.get<Application[]>('tournament/current/applications')
            .subscribe(res => this.applications = res);

        this.groups = [];

        let i;
        for (i = 0; i < AdminGroupsStartComponent.NUMBER_OF_GROUPS; i++) {
            this.groups.push(<Group> {
                label: GroupService.labels[i],
                standings: [],
            });

            let j;
            for (j = 0; j < AdminGroupsStartComponent.USERS_PER_GROUP; j++) {
                this.groups[this.groups.length - 1].standings.push(<GroupStanding> {});
            }
        }
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
