import {Component, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";

@Component({
    selector: 'cwt-admin-groups-start',
    template: require('./admin-groups-start.component.html')
})
export class AdminGroupsStartComponent implements OnInit {

    constructor(private requestService: RequestService) {
    }

    public ngOnInit(): void {
        this.requestService.get('configuration', {keys: ['USERS_PER_GROUP', 'NUMBER_OF_GROUPS']})
            .subscribe();
    }
}
