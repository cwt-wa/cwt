import {Component, Input, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {Group} from "../custom";
import {ConfigurationService} from "../_services/configuration.service";
import {finalize} from "rxjs/operators";

type ViewMode = "list" | "square";

@Component({
    selector: 'cwt-groups-overview',
    template: require('./groups-overview.component.html')
})
export class GroupsOverviewComponent implements OnInit {

    @Input() tournamentId: number;
    @Input() hideTitle: boolean;
    @Input() hideLoadingIndicator: boolean;

    public viewMode: ViewMode;
    public groups: Group[];
    public numberOfGroupMembersAdvancing: number;
    public loading: boolean = true;
    private readonly VIEW_MODE_STORAGE_KEY: 'groups-overview-view-mode' = 'groups-overview-view-mode';

    constructor(private requestService: RequestService, private configurationService: ConfigurationService) {
        this.viewMode = <ViewMode> localStorage.getItem('groups-overview-view-mode') || 'list';
    }

    public ngOnInit(): void {
        this.requestService.get<Group[]>(`tournament/${this.tournamentId || 'current'}/group`)
            .pipe(finalize(() => this.loading = false))
            .subscribe(res => this.groups = res);

        this.configurationService.requestByKeys("NUMBER_OF_GROUP_MEMBERS_ADVANCING")
            .subscribe(res => this.numberOfGroupMembersAdvancing = parseInt(res[0].value));
    }

    public rememberViewMode(): void {
        localStorage.setItem(this.VIEW_MODE_STORAGE_KEY, this.viewMode);
    }
}
