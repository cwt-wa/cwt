import {Component, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {StreamDto} from "../custom";
import {ActivatedRoute} from "@angular/router";
import {DomSanitizer, SafeResourceUrl} from "@angular/platform-browser";

@Component({
    selector: 'cwt-stream-detail',
    template: require('./stream-detail.component.html')
})
export class StreamDetailComponent implements OnInit {

    stream: StreamDto;
    streamUrl: SafeResourceUrl;

    constructor(private requestService: RequestService,
                private route: ActivatedRoute,
                private sanitizer: DomSanitizer) {
    }

    ngOnInit(): void {
        this.route.paramMap.subscribe(res => {
            this.requestService.get<StreamDto>(`stream/${res.get('id')}`)
                .subscribe(res => {
                    this.stream = res;
                    this.streamUrl = this.sanitizer.bypassSecurityTrustResourceUrl(
                        'https://player.twitch.tv/?video=' + this.stream.id);
                });
        });
    }
}
