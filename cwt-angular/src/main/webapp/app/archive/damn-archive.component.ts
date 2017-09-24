import {Component, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {Tournament} from "../custom";

@Component({
    selector: 'cwt-damn-archive',
    template: require('./damn-archive.component.html'),
})
export class DamnArchiveComponent implements OnInit {

    public tournaments: Tournament[];

    constructor(private requestService: RequestService) {
    }

    ngOnInit(): void {
        this.requestService.get<Tournament[]>('tournament')
            .subscribe(res => this.tournaments = res);
    }
}
