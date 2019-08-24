import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {RequestService} from "../_services/request.service";
import {Tournament} from "../custom";
import {finalize} from "rxjs/operators";

@Component({
    selector: 'cwt-archive-detail',
    template: require('./archive-detail.component.html')
})
export class ArchiveDetailComponent implements OnInit {

    tournament: Tournament;
    loading: boolean = true;

    constructor(private route: ActivatedRoute, private requestService: RequestService) {
    }

    ngOnInit(): void {
        this.route.paramMap.subscribe(res => {
                this.requestService.get<Tournament>(`tournament/${+res.get('idOrYear')}`)
                    .pipe(finalize(() => this.loading = false))
                    .subscribe(res => this.tournament = res);
            }
        );
    }
}
