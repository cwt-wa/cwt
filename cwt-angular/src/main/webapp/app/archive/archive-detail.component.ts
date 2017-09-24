import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {RequestService} from "../_services/request.service";
import {Tournament} from "../custom";

@Component({
    selector: 'cwt-archive-detail',
    template: require('./archive-detail.component.html')
})
export class ArchiveDetailComponent implements OnInit {

    tournament: Tournament;

    constructor(private route: ActivatedRoute, private requestService: RequestService) {
    }

    ngOnInit(): void {
        this.route.paramMap.subscribe(res => {
                this.requestService.get<Tournament>(`tournament/${+res.get('idOrYear')}`)
                    .subscribe(res => this.tournament = res);
            }
        );
    }
}
