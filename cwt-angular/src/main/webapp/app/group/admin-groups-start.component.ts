import {Component, Inject, OnInit} from '@angular/core';
import {Application, Group, GroupStanding} from "../custom";
import {GroupService} from "../_services/group.service";
import {RequestService} from "../_services/request.service";
import {APP_CONFIG, AppConfig} from "../app.config";

@Component({
    selector: 'cwt-admin-groups-start',
    template: require('./admin-groups-start.component.html')
})
export class AdminGroupsStartComponent implements OnInit {

    private groups: Group[];
    private applications: Application[];
    manualDraw: boolean = true;

    public constructor(private requestService: RequestService, @Inject(APP_CONFIG) private appConfig: AppConfig) {
    }

    public ngOnInit(): void {
        this.requestService.get<Application[]>('tournament/current/applications')
            .subscribe(res => this.applications = res);

        this.groups = [];

        let i;
        for (i = 0; i < this.appConfig.tournament.numberOfGroups; i++) {
            this.groups.push(<Group> {
                label: GroupService.labels[i],
                standings: [],
            });

            let j;
            for (j = 0; j < this.appConfig.tournament.usersPerGroup; j++) {
                this.groups[this.groups.length - 1].standings.push(<GroupStanding> {});
            }
        }
    }
}
