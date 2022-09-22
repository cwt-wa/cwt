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

        try {
          const regexWithProtocol = new RegExp("(?<!@)(\\b(?:http[s]?:\\/\\/)[a-z0-9-.]+?\\.(?:[a-z]{2,})(?:[.a-z0-9-\\/]+)?(?:[^\\s]*)?\\b)", "gi");
          value = value.replace(regexWithProtocol, `<a href="http://$1"${htmlClasses}target="_blank">$1</a>`);
          value = value.replace(/http?:\/\/(http[s]?:\/\/)/gi, '$1');
        } catch {
          const regexWithProtocol = /(\b(?:http[s]?:\/\/)[a-z0-9-.]+?\.(?:[a-z]{2,})(?:[.a-z0-9-\/]+)?(?:[^\s]*)?\b)/gi;
          value = value.replace(regexWithProtocol, `<a href="http://$1"${htmlClasses}target="_blank">$1</a>`);
          value = value.replace(/http?:\/\/(http[s]?:\/\/)/gi, '$1');
        }

        const regexForMail = /(\b\S+@\S+\b)/g;
        value = value.replace(regexForMail, '<a href="mailto:$1">$1</a>');

        return value;
    }
}
