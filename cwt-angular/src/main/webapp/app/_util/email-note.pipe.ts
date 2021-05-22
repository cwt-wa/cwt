import {Pipe, PipeTransform, Inject} from '@angular/core';
import {APP_CONFIG, AppConfig} from "../app.config";

@Pipe({
    name: 'cwtEmailNote'
})
export class EmailNote implements PipeTransform {

    constructor(@Inject(APP_CONFIG) private appConfig: AppConfig) {
    }

    transform(_: null): string {
        return `
We use this as a source of identification. You can reset your password with it if you happen to forget it, for
instance.<br>
In spite of that and if at all, we might send you an email once a year to inform you about the upcoming
edition of ${this.appConfig.nameShort}.<br>
In case you forget your password you can get a new one sent to your email address. So you wouldn’t need to contact the support.
`
    }
}
