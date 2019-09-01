import {Component, OnInit} from '@angular/core';
import {RequestService} from "./_services/request.service";
import {Tournament} from "./custom";

@Component({
    selector: 'cwt-admin',
    template: require('./admin.component.html')
})

export class AdminComponent implements OnInit {

    tournament?: Tournament;

    constructor(private requestService: RequestService) {
    }

    ngOnInit(): void {
        this.requestService.get<Tournament>('tournament/current')
            .subscribe(res => this.tournament = res)
    }
}
