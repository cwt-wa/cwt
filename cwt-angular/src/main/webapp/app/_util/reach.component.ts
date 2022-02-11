import {Component, Input} from '@angular/core';

@Component({
    selector: 'cwt-reach',
    template: `<img class="reach" [src]="img()">`,
    styles: [`
        img.reach {
            height: 3.2rem;
        }
    `]
})
export class ReachComponent {
    @Input()
    maxRound: number;

    @Input()
    round: number;

    img() {
        const assoc: { [key: number]: string } = {};
        assoc[this.maxRound + 2] = 'gold.png';
        assoc[this.maxRound + 1] = 'silver.png';
        assoc[this.maxRound] = 'bronze.png';
        assoc[this.maxRound - 1] = 'semi.png';
        assoc[this.maxRound - 2] = 'quarter.png';
        assoc[this.maxRound - 3] = 'last16.png';
        assoc[1] = 'group.png';
        assoc[0] = 'void.png';

        return require(`../../img/reach/${assoc[this.round] || 'last16.png'}`);
    }
}
