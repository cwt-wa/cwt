import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
    name: 'cwtConvertLinks',
})
export class ConvertLinksPipe implements PipeTransform {

    constructor() {
    }

    transform(value: string, classes: string[] = []): string | null {
        if (typeof value !== "string") return value;

        const htmlClasses = classes.length ? ` class="${classes.join(" ")}" ` : " ";

        const regexWithProtocol = /((?:http[s]?:\/\/)?(?:www)?[.a-z0-9-]+?\.(?:[a-z]+)(?:[.a-z0-9-\/]+)?(?:\?\S*)?)/gi;
        value = value.replace(regexWithProtocol, `<a href="http://$1"${htmlClasses}target="_blank">$1</a>`);
        value = value.replace(/http?:\/\/(http[s]?:\/\/)/gi, '$1');
        return value;
    }
}
