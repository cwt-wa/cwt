
import {timer as observableTimer} from 'rxjs';
import {Component, Input, OnInit} from '@angular/core';
import {TimeAgoService} from "../_services/time-ago.service";
import {TimeAgo} from "../custom";

@Component({
    selector: 'cwt-time-ago',
    template: `
        <span [title]="(date | date:'medium')" i18n>
            {timeAgo.unit, select,
                DAY {{{timeAgo.value}} {timeAgo.value, plural, =1 {day} other {days}} ago}
                HOUR {{{timeAgo.value}} {timeAgo.value, plural, =1 {hour} other {hours}} ago}
                MINUTE {{{timeAgo.value}} {timeAgo.value, plural, =1 {minute} other {minutes}} ago}
                SECOND {just now}
                other {{{timeAgo.original | date:'medium'}}}
            }
        </span>
    `
})
export class TimeAgoComponent implements OnInit {
    @Input()
    date: Date | string;
    timeAgo: TimeAgo;

    constructor(private timeAgoService: TimeAgoService) {
    }

    public ngOnInit(): void {
        const dateAsDate: Date = new Date(<Date> this.date);
        this.timeAgo = this.timeAgoService.timeAgo(dateAsDate);

        observableTimer(0, 1000 * 60)
            .subscribe(() => this.timeAgo = this.timeAgoService.timeAgo(dateAsDate));
    }
}
