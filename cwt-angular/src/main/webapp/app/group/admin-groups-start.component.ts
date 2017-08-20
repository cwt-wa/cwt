import {Component, OnInit} from '@angular/core';
import {ConfigurationService} from "../_services/configuration.service";
import {Application, Configuration, Group, GroupStanding, User} from "../custom";
import {GroupService} from "../_services/group.service";
import {Observable} from "rxjs/Observable";
import {RequestService} from "../_services/request.service";

@Component({
    selector: 'cwt-admin-groups-start',
    template: require('./admin-groups-start.component.html')
})
export class AdminGroupsStartComponent implements OnInit {

    private groups: Group[];
    private applications: Application[];
    private typeAheadForGroupMember: (text$: Observable<string>) => Observable<any>;
    private typeAheadInputFormatter: (value: User) => string;
    private typeAheadResultFormatter: (value: User) => string;

    constructor(private configurationService: ConfigurationService, private requestService: RequestService) {
        this.typeAheadForGroupMember = (text$: Observable<string>) =>
            text$
                .debounceTime(200)
                .distinctUntilChanged()
                .map(term =>
                    this.applications
                        .map(a => a.applicant)
                        .filter(a => a.username.toLowerCase().indexOf(term.toLowerCase()) !== -1));
        this.typeAheadInputFormatter = (value: User) => value.username;
        this.typeAheadResultFormatter = (value: User) => value.username + ''; // TODO Warning that alrady drawn.
    }

    public ngOnInit(): void {
        this.requestService.get<Application[]>('tournament/current/applications')
            .subscribe(res => this.applications = res);

        this.configurationService.requestByKeys<number>(["NUMBER_OF_GROUPS", "USERS_PER_GROUP"])
            .subscribe(configs => {
                const numberOfGroups: Configuration<number> = configs.find(c => c.key === "NUMBER_OF_GROUPS");
                const usersPerGroup: Configuration<number> = configs.find(c => c.key === "USERS_PER_GROUP");

                this.groups = [];

                let i;
                for (i = 0; i < numberOfGroups.value; i++) {
                    this.groups.push(<Group> {
                        label: GroupService.labels[i],
                        standings: [{}],
                    });

                    let j;
                    for (j = 0; j < usersPerGroup.value; j++) {
                        this.groups[this.groups.length - 1].standings.push(<GroupStanding> {});
                    }
                }
            });
    }
}
