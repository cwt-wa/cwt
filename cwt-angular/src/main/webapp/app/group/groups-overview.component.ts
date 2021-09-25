import {Component, Input, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {GroupWithGamesDto} from "../custom";
import {finalize} from "rxjs/operators";
import {Subject} from 'rxjs';
import {CurrentTournamentService} from "../_services/current-tournament.service";
import {Toastr} from "../_services/toastr";

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
    public groups: GroupWithGamesDto[];
    public numberOfGroupMembersAdvancing: number;
    public loading: boolean = true;
    public highlightUserSubject: Subject<{ user: number, enter: boolean }> = new Subject();
    private readonly VIEW_MODE_STORAGE_KEY: 'groups-overview-view-mode' = 'groups-overview-view-mode';

    constructor(private requestService: RequestService,
                private currentTournamentService: CurrentTournamentService,
                private toastr: Toastr) {
        this.viewMode = <ViewMode> localStorage.getItem('groups-overview-view-mode') || 'list';
    }

    public async ngOnInit() {
        const tournamentId = this.tournamentId || (await this.currentTournamentService.value)?.id;
        if (tournamentId == null) {
            this.toastr.info("There is currently no tournament.");
            this.loading = false;
        } else {
            this.requestService.get<GroupWithGamesDto[]>(`tournament/${tournamentId}/group`)
                .pipe(finalize(() => this.loading = false))
                .subscribe(res => {
                    this.groups = res;
                    this.numberOfGroupMembersAdvancing = res[0].tournament.numOfGroupAdvancing;
                });
        }
    }

    public onMouseOverUser(event: { user: number, enter: boolean }) {
        this.highlightUserSubject.next(event);
    }

    public rememberViewMode(): void {
        localStorage.setItem(this.VIEW_MODE_STORAGE_KEY, this.viewMode);
    }
}
