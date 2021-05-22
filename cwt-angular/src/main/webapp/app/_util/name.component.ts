import {Component, Input, Inject} from '@angular/core';
import {APP_CONFIG, AppConfig} from "../app.config";

@Component({
    selector: 'cwt-name',
    template: '<span class="text-nowrap">{{result}}</span>'
})
export class NameComponent {

    @Input()
    private short: boolean = false;

    /**
     * The name or short name.
     */
    public result: string;

    constructor(@Inject(APP_CONFIG) private appConfig: AppConfig) {
        this.result = this.short === true ?  this.appConfig.nameShort : this.appConfig.nameLong;
    }
}

