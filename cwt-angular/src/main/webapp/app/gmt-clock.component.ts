import {Component} from "@angular/core";

@Component({
    selector: 'cwt-gmt-clock',
    styles: [
        '.display-date { display: inline-block; }',
        '.time-part { width: 20px; display: inline-block; text-align: center; }',
    ],
    template: `
        <div *ngIf="displayDate && timePart">
            {{displayDate}}
            <div class="time-part">{{timePart.hours}}</div>:<div class="time-part">{{timePart.minutes}}</div>:<div class="time-part">{{timePart.seconds}}</div>
            GMT
        </div>
    `
})
export class GmtClockComponent {
    private static readonly WEEKDAYS = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
    private static readonly MONTHS = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];

    public displayDate: string;
    public timePart: {hours: string, minutes: string, seconds: string};

    constructor() {
        window.setInterval(() => {
            const nowLocal = new Date(); // TODO Is it costly to instantiate Date every second?

            this.displayDate =
                GmtClockComponent.WEEKDAYS[nowLocal.getUTCDay()]
                + ', '
                + GmtClockComponent.MONTHS[nowLocal.getUTCMonth()]
                + ' '
                + nowLocal.getUTCDate()
                + ', '
                + nowLocal.getUTCFullYear()
                + ', ';

            this.timePart = {
                hours: this.prependLeadingZero(nowLocal.getUTCHours()),
                minutes: this.prependLeadingZero(nowLocal.getUTCMinutes()),
                seconds: this.prependLeadingZero(nowLocal.getUTCSeconds()),
            };
        }, 1000);

    }

    private prependLeadingZero(number: number): string {
        return number > 9 ? number.toString() : '0' + number;
    }
}
