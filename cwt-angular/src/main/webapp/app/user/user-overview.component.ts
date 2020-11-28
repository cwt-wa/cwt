import {Component, Inject, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {PageDto, UserOverviewDto} from "../custom";
import {APP_CONFIG, AppConfig} from "../app.config";

@Component({
    selector: 'cwt-user-overview',
    template: require('./user-overview.component.html')
})
export class UserOverviewComponent implements OnInit {
    pageOfUsers: PageDto<UserOverviewDto> = <PageDto<UserOverviewDto>> {size: 10, start: 0};
    loading: boolean;

    constructor(private requestService: RequestService, @Inject(APP_CONFIG) public appConfig: AppConfig) {
    }

    ngOnInit(): void {
        this.load();
    }

    sort(sortable: string, sortAscending: boolean) {
        this.pageOfUsers.sortBy = sortable;
        this.pageOfUsers.sortAscending = sortAscending;
        this.pageOfUsers.start = 0;
        this.load();
    }

    goTo(start: number) {
        this.pageOfUsers.start = start;
        this.load();
    }

    load() {
        this.loading = true;

        this.requestService.getPaged<UserOverviewDto>('user/page', this.pageOfUsers)
            .subscribe(pageOfUsers => {
                this.pageOfUsers = pageOfUsers;
            }, undefined, () => {
                this.loading = false;
            });
    }
}
