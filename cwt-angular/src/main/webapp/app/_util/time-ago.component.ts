import {Component, Input, OnInit} from '@angular/core';
import {TimeAgoService} from "../_services/time-ago.service";

@Component({
    selector: 'cwt-time-ago',
    // template: require('time-ago.component.html')
    template: `
        <span [title]="(date | date:'medium')">{{timeAgo}} ago</span>
        
    `
})
export class TimeAgoComponent implements OnInit {
    @Input()
    date: Date | string;
    timeAgo: string;

    constructor(private timeAgoService: TimeAgoService) {
    }

    public ngOnInit(): void {
        this.timeAgo = this.timeAgoService.timeAgo(new Date(<Date> this.date));
    }


}
