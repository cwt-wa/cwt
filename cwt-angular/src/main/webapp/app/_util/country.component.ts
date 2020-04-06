import {Component, Input} from '@angular/core';
import {CountryDto} from "../custom";

@Component({
    selector: 'cwt-country',
    template: `<img [src]="img()" [alt]="country.name" [ngbTooltip]="country.name">`
})
export class CountryComponent {
    @Input()
    country: CountryDto;

    img() {
        try {
            return require('../../img/flags/' + this.country.flag);
        } catch (e) {
            return require('../../img/flags/unknown.png');
        }
    }
}
