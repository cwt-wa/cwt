import {Component, OnInit} from '@angular/core';
import {Application, Group, GroupDto, GroupStanding} from "../custom";
import {GroupService} from "../_services/group.service";
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
    manualDraw: boolean = true;

    public constructor(private requestService: RequestService) {
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
}
