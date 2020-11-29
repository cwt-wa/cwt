import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {RequestService} from '../_services/request.service';
import {ChannelDto} from '../custom';
import {APP_CONFIG, AppConfig} from '../app.config';
import {Toastr} from "../_services/toastr";
import {Utils} from "../_util/utils";

interface LiveStream {
    event_id: string;
    user_id: string;
    user_name: string;
    title: string;
}

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

    liveStreams: LiveStream[] = [];
    userIdByChannelName: { [key: string]: string } = {} as any;

    private eventSource: EventSource;
    private initialRequestTookPlace: boolean = false;

    constructor(private requestService: RequestService,
                private toastr: Toastr,
                private utils: Utils,
                @Inject(APP_CONFIG) private appConfig: AppConfig) {
    }

    ngOnInit(): void {
        this.setupEventSource();
    }

    private setupEventSource() {
        this.eventSource = new EventSource(this.appConfig.liveStreamProducer);
        this.eventSource.onerror = (err: any) =>
            err?.originalTarget?.readyState === 2 && setTimeout(() => this.setupEventSource(), 1000);
        this.eventSource.addEventListener("STREAMS", e => {
            const eventData: LiveStream[] = JSON.parse((e as any).data);
            const newLiveStreams: LiveStream[] = eventData.filter(nS =>
                this.liveStreams.find(s => s.event_id === nS.event_id) == null);

            if (this.initialRequestTookPlace) {
                if (newLiveStreams.length > 0) {
                    this.queryChannelsIf(this.utils.isEmpty(this.userIdByChannelName), () => {
                        newLiveStreams.forEach(newLiveStream =>
                            this.toastr.info(`
                                <strong>${this.userIdByChannelName[newLiveStream.user_id] || newLiveStream.user_name}</strong>
                                is live with <strong>${newLiveStream.title}</strong>
                                <a target="_blank" href="https://twitch.tv/${newLiveStream.user_name}"
                                   class="btn btn-sm btn-primary float-right">
                                    Watch
                                </a>`));
                    });
                }
            }

            this.liveStreams = eventData;
            this.queryChannelsIf((this.utils.isEmpty(this.userIdByChannelName) && this.liveStreams.length > 0 && newLiveStreams.length < 1) || (!this.initialRequestTookPlace));
            this.initialRequestTookPlace = true;
        });
    }

    ngOnDestroy() {
        this.eventSource.close();
    }

    queryChannelsIf(condition: boolean, cb?: Function) {
        if (!condition) return cb && cb();

        this.requestService.get<ChannelDto[]>('channel').subscribe(res => {
            this.userIdByChannelName = res.reduce((prev, curr) => {
                prev[curr.id] = curr.displayName;
                return prev;
            }, {} as { [key: string]: string; });
            cb && cb();
        });
    }
}
