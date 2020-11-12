import {Component, Input} from '@angular/core';
import {StreamDto} from "../custom";

@Component({
    selector: 'cwt-watch-on-twitch',
    template: `
        <a class="btn btn-success text-real-white" target="_blank" *ngIf="streams.length === 1"
           [href]="'https://www.twitch.tv/videos/' + streams[0].id">
            <i class="fa fa-lg fa-twitch"></i>&nbsp;<span class="d-none d-lg-inline">Watch on Twitch</span>
        </a>
        <div ngbDropdown *ngIf="streams.length > 1">
            <button type="button" class="btn btn-success text-real-white" ngbDropdownToggle>
                <i class="fa fa-lg fa-twitch"></i>&nbsp;<span class="d-none d-lg-inline">Watch on Twitch</span>
            </button>
            <div ngbDropdownMenu>
                <a ngbDropdownItem *ngFor="let stream of streams" target="_blank"
                   [href]="'https://www.twitch.tv/videos/' + stream.id">
                    {{stream.title}}
                </a>
            </div>
        </div>
    `,
})
export class WatchOnTwitchComponent {

    @Input() streams: StreamDto[];
}
