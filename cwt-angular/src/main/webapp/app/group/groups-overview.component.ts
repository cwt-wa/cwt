import {Component, Input, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {GroupWithGamesDto} from "../custom";
import {ConfigurationService} from "../_services/configuration.service";
import {finalize} from "rxjs/operators";
import {Subject} from 'rxjs';

type ViewMode = "list" | "square";

@Component({
    selector: 'cwt-groups-overview',
    template: require('./groups-overview.component.html')
})
export class GroupsOverviewComponent implements OnInit {

    @Input() tournamentId: number;
    @Input("numberOfGroupMembersAdvancing") numberOfGroupMembersAdvancingInput: number;
    @Input() hideTitle: boolean;
    @Input() hideLoadingIndicator: boolean;

    public viewMode: ViewMode;
    public groups: GroupWithGamesDto[];
    public numberOfGroupMembersAdvancing: number;
    public loading: boolean = true;
    public highlightUserSubject: Subject<{ user: number, enter: boolean }> = new Subject();
    private readonly VIEW_MODE_STORAGE_KEY: 'groups-overview-view-mode' = 'groups-overview-view-mode';

    constructor(private requestService: RequestService, private configurationService: ConfigurationService) {
        this.viewMode = <ViewMode> localStorage.getItem('groups-overview-view-mode') || 'list';
    }

    public ngOnInit(): void {
        this.requestService.get<GroupWithGamesDto[]>(`tournament/${this.tournamentId || 'current'}/group`)
            .pipe(finalize(() => this.loading = false))
            .subscribe(res => this.groups = res);

        if (!this.numberOfGroupMembersAdvancingInput) {
            this.configurationService.requestByKeys("NUMBER_OF_GROUP_MEMBERS_ADVANCING")
                .subscribe(res => this.numberOfGroupMembersAdvancing = parseInt(res[0].value));
        } else {
            this.numberOfGroupMembersAdvancing = this.numberOfGroupMembersAdvancingInput
        }
    }

    public onMouseOverUser(event: { user: number, enter: boolean }) {
        this.highlightUserSubject.next(event);
    }

    public rememberViewMode(): void {
        localStorage.setItem(this.VIEW_MODE_STORAGE_KEY, this.viewMode);
    }
}
