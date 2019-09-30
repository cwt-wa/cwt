import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
    name: 'cwtEmailNote'
})
export class EmailNote implements PipeTransform {

    transform(_: null): string {
        return `
We use this as a source of identification. You can reset your password with it if you happen to forget it, for
instance.<br>
In spite of that and if at all, we might send you an email once a year to inform you about the upcoming
edition of CWT.<br>
In case you forget your password you can get a new one sent to your email address. So you wouldn’t need to contact the support.
`
    }
}
