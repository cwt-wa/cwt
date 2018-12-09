import {Component, Input} from '@angular/core';

@Component({
    selector: 'cwt-country',
    template: `<img [src]="img()" [alt]="country" [ngbTooltip]="country">`
})
export class CountryComponent {
    @Input()
    country: string;

    img() {
        try {
            return require('../../img/flags/' + this.country.toLowerCase().replace(" ", "_") + '.png');
        } catch (e) {
            return require('../../img/flags/unknown.png');
        }
    }
}
