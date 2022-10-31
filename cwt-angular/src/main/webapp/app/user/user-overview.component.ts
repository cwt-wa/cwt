import {Component, Inject, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {Router} from "@angular/router";
import {PageDto, UserOverviewDto, UserMinimalDto} from "../custom";
import {Observable, OperatorFunction} from 'rxjs';
import {debounceTime, distinctUntilChanged, map} from 'rxjs/operators';
import {APP_CONFIG, AppConfig} from "../app.config";
import {NgbTypeaheadSelectItemEvent} from "@ng-bootstrap/ng-bootstrap";

@Component({
    selector: 'cwt-user-overview',
    template: require('./user-overview.component.html')
})
export class UserOverviewComponent implements OnInit {
    pageOfUsers: PageDto<UserOverviewDto> = <PageDto<UserOverviewDto>> {size: 10, start: 0};
    loading: boolean;
    suggestions: UserMinimalDto[] = null;
	search: OperatorFunction<string, string[]> = (text$: Observable<string>) =>
		text$.pipe(
			debounceTime(200),
			distinctUntilChanged(),
			map((term) => this.suggestions
                .filter(({username}) => username.toLowerCase().includes(term.toLowerCase()))
                .map(({username}) => username)
                .slice(0, 10)),
		);

    constructor(private requestService: RequestService,
                @Inject(APP_CONFIG) public appConfig: AppConfig,
                private router: Router,) {
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

    lazyLoad() {
        if (this.suggestions?.length) return;
        this.requestService
            .get<UserMinimalDto[]>("user", {minimal: "true"}).toPromise()
            .then(users => this.suggestions = users);
    }

    searchSubmit(e: NgbTypeaheadSelectItemEvent) {
        this.router.navigateByUrl('/users/' + e.item);
    }
}

