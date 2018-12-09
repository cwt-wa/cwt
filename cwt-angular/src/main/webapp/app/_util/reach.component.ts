import {Component, Input} from '@angular/core';

@Component({
    selector: 'cwt-reach',
    template: `<img [src]="img()">`
})
export class ReachComponent {
    @Input()
    maxRound: number;

    @Input()
    round: number;

    img() {
        const assoc: { [key: number]: string } = {};
        assoc[this.maxRound + 2] = 'gold.gif';
        assoc[this.maxRound + 1] = 'silver.gif';
        assoc[this.maxRound] = 'bronze.gif';
        assoc[this.maxRound - 1] = 'semi.gif';
        assoc[this.maxRound - 2] = 'quarter.gif';
        assoc[this.maxRound - 3] = 'last16.gif';
        assoc[1] = 'group.png';
        assoc[0] = 'void.gif';

        return require(`../../img/reach/${assoc[this.round]}`);
    }
}
