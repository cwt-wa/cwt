import {Component, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";

@Component({
    selector: 'cwt-streams',
    template: require('./channel-create.component.html')
})
export class ChannelCreateComponent implements OnInit {

    constructor(private requestService: RequestService) {
    }

    ngOnInit(): void {
    }
}
