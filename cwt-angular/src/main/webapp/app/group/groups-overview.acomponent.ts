import {Component, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {Group} from "../custom";
import {ConfigurationService} from "../_services/configuration.service";

type ViewMode = "list" | "square";

@Component({
    selector: 'cwt-groups-overview',
    template: require('./groups-overview.component.html')
})
export class GroupsOverviewComponent implements OnInit {

    public viewMode: ViewMode;
    public groups: Group[];
    public numberOfGroupMembersAdvancing: number;
    private readonly VIEW_MODE_STORAGE_KEY: 'groups-overview-view-mode' = 'groups-overview-view-mode';

    constructor(private requestService: RequestService, private configurationService: ConfigurationService) {
        this.viewMode = <ViewMode> localStorage.getItem('groups-overview-view-mode') || 'list';
    }

    public ngOnInit(): void {
        this.requestService.get<Group[]>('tournament/current/group')
            .subscribe(res => this.groups = res);

        this.configurationService.requestByKeys<number>(["NUMBER_OF_GROUP_MEMBERS_ADVANCING"])
            .subscribe(res => this.numberOfGroupMembersAdvancing = res[0].value);
    }

    public rememberViewMode(): void {
        localStorage.setItem(this.VIEW_MODE_STORAGE_KEY, this.viewMode);
    }
}
