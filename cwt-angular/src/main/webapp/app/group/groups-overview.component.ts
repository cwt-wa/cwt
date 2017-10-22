import {Component, Input, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {Group} from "../custom";
import {ConfigurationService} from "../_services/configuration.service";

type ViewMode = "list" | "square";

@Component({
    selector: 'cwt-groups-overview',
    template: require('./groups-overview.component.html')
})
export class GroupsOverviewComponent implements OnInit {

    @Input()
    tournamentId: number;
    @Input()
    title: string; // TODO Normally I'd say the header shouldn't be part of the component, but since it isn't at the start but the second element—because of CSS float—this can't be done. Maybe one day I'll find a way to realize this.

    public viewMode: ViewMode;
    public groups: Group[];
    public numberOfGroupMembersAdvancing: number;
    private readonly VIEW_MODE_STORAGE_KEY: 'groups-overview-view-mode' = 'groups-overview-view-mode';

    constructor(private requestService: RequestService, private configurationService: ConfigurationService) {
        this.viewMode = <ViewMode> localStorage.getItem('groups-overview-view-mode') || 'list';
    }

    public ngOnInit(): void {
        this.title = this.title === undefined ? 'Groups' : this.title;

        this.requestService.get<Group[]>(`tournament/${this.tournamentId || 'current'}/group`)
            .subscribe(res => this.groups = res);

        this.configurationService.requestByKeys<number>("NUMBER_OF_GROUP_MEMBERS_ADVANCING")
            .subscribe(res => this.numberOfGroupMembersAdvancing = res[0].value);
    }

    public rememberViewMode(): void {
        localStorage.setItem(this.VIEW_MODE_STORAGE_KEY, this.viewMode);
    }
}
