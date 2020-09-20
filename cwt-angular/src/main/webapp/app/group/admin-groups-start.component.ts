import {Component, HostListener, OnInit} from '@angular/core';
import {Application, Group, GroupStanding} from "../custom";
import {GroupService} from "../_services/group.service";
import {RequestService} from "../_services/request.service";
import {ConfigurationService} from "../_services/configuration.service";
import {CanDeactivateGuard, Deactivatable} from "../_services/can-deactivate-guard";
import {Observable} from "rxjs";

@Component({
    selector: 'cwt-admin-groups-start',
    template: require('./admin-groups-start.component.html'),
    providers: [CanDeactivateGuard]
})
export class AdminGroupsStartComponent implements OnInit, Deactivatable {

    groups: Group[];
    applications: Application[];
    manualDraw: boolean = true;

    public constructor(private requestService: RequestService, private configurationService: ConfigurationService) {
    }

    public ngOnInit(): void {
        this.requestService.get<Application[]>('tournament/current/applications')
            .subscribe(res => this.applications = res);

        this.configurationService.requestByKeys("NUMBER_OF_GROUPS", "USERS_PER_GROUP")
            .subscribe(res => {
                const numberOfGroups = parseInt(res.find(c => c.key === "NUMBER_OF_GROUPS").value);
                const usersPerGroup = parseInt(res.find(c => c.key === "USERS_PER_GROUP").value);

                this.groups = [];

                let i;
                for (i = 0; i < numberOfGroups; i++) {
                    this.groups.push(<Group> {
                        label: GroupService.labels[i],
                        standings: [],
                    });

                    let j;
                    for (j = 0; j < usersPerGroup; j++) {
                        this.groups[this.groups.length - 1].standings.push(<GroupStanding> {});
                    }
                }
            });
    }

    canDeactivate(): Observable<boolean> | Promise<boolean> | boolean {
        return this.confirmLeave();
    }

    @HostListener('window:beforeunload', ['$event'])
    confirmPageLeave($event: Event) {
        $event.returnValue = this.confirmLeave();
        if (!$event.returnValue) $event.preventDefault();
    }

    confirmLeave() {
        return confirm('Do you really want to leave?');
    }
}
