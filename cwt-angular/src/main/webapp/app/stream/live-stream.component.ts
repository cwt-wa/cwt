import {Component, OnInit, OnDestroy, Inject} from '@angular/core';
import {RequestService} from '../_services/request.service';
import {ChannelDto} from '../custom';
import {AppConfig, APP_CONFIG} from '../app.config';

interface LiveStream {
    event_id: string;
    user_id: string;
    user_name: string;
    title: string;
};

@Component({
    selector: 'cwt-live-stream',
    template: `
<div *ngFor="let stream of liveStreams" class="alert alert-info">
    <div class="d-flex justify-content-between align-items-center">
        <div class="d-flex align-items-center">
            <i class="fa fa-twitch fa-lg mr-2"></i>
            <span>
                <strong>{{userIdByChannelName[stream.user_id] || stream.user_name}}</strong>
                is live with
                <strong>{{stream.title}}</strong>
            </span>
        </div>
        <a target="_blank" [href]="'https://twitch.tv/' + stream.user_name"
           class="btn btn-sm btn-primary float-right">
            Watch
        </a>
    </div>
</div>
`
})
export class LiveStreamComponent implements OnInit, OnDestroy {

    private eventSource: EventSource;
    liveStreams: LiveStream[];
    userIdByChannelName: {[key: string]: string} = {} as any;

    constructor(private requestService: RequestService,
                @Inject(APP_CONFIG) private appConfig: AppConfig) {}

    ngOnInit(): void {
        this.eventSource = new EventSource(this.appConfig.liveStreamProducer);
        this.eventSource.onerror = console.error;
        this.eventSource.onmessage = e => {
            this.liveStreams = JSON.parse(e.data);

            if (!!this.liveStreams.length
                    && !Object.keys(this.userIdByChannelName).length) {
                this.queryChannels();
            }
        };

    }

    ngOnDestroy() {
        this.eventSource.close();
    }

    queryChannels() {
        this.requestService.get<ChannelDto[]>('channel')
            .subscribe(res => this.userIdByChannelName = res.reduce((prev, curr) => {
                prev[curr.id] = curr.displayName;
                return prev;
            }, {} as {[key: string]: string}));
    }
}
