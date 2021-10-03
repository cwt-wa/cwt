import {Component, Input} from '@angular/core';

@Component({
    selector: 'cwt-weapon',
    template: `<img *ngIf="weapon" [src]="img()" [alt]="title" [ngbTooltip]="title" height="32">`
})
export class WeaponComponent {

    @Input()
    weapon: string;

    get title() {
        return this.weapon.replace(/([A-Z])/g, " $1").trim();
    }

    img() {
        try {
            return require('../../img/weapons/' + this.weapon.toLowerCase().split(' ').join('') + '.png');
        } catch (e) {
            return require('../../img/weapons/unknown.gif');
        }
    }
}
