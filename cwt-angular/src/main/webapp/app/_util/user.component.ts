import {Component, Input} from '@angular/core';

@Component({
    selector: 'cwt-user',
    template: `
        <a [routerLink]="['/users', username]">{{username}}</a>
    `
})
export class UserComponent {
    @Input()
    username: string;
}
