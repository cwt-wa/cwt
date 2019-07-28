import {Component, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {StreamDto} from "../custom";
import {Utils} from "../_util/utils";
import {merge} from "rxjs";

@Component({
    selector: 'cwt-streams',
    template: require('./streams.component.html')
})
export class StreamsComponent implements OnInit {

    streams: StreamDto[] = [];

    constructor(private requestService: RequestService, private utils: Utils) {
    }

    ngOnInit(): void {
        merge(
            this.requestService.get<StreamDto[]>('stream', {'new': 'false'}),
            this.requestService.get<StreamDto[]>('stream', {'new': 'true'}),
        ).subscribe(res => this.streams = this.utils.mergeDistinctBy<StreamDto>(this.streams, res, 'id'));
    }
}
