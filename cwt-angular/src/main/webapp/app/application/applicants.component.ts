import {Component, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {Application} from "../custom";

@Component({
    selector: 'cwt-applicants',
    template: require('./applicants.component.html')
})
export class ApplicantsComponent implements OnInit {

    public applications: Application[];

    constructor(private requestService: RequestService) {
    }

    public ngOnInit(): void {
        this.requestService.get<Application[]>('tournament/current/applications')
            .subscribe(res => this.applications = res)
    }
}
