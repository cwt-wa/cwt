import {Pipe, PipeTransform} from '@angular/core';
import {DatePipe} from "@angular/common";

/**
 * Wrapper around Angular's DatePipe but with CWT defaults and in GMT.
 */
@Pipe({
    name: 'cwtDate',
})
export class CwtDatePipe implements PipeTransform {

    constructor(private datePipe: DatePipe) {
    }

    transform(value: any, format: string = 'MMM d, y, HH:ss'): string | null {
        if (format === 'time') format = 'HH:ss';
        return this.datePipe.transform(value, format, 'GMT');
    }
}
