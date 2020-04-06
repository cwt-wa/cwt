import {Directive, Input} from '@angular/core';
import {FormControl, NG_VALIDATORS, ValidationErrors, Validator} from "@angular/forms";

@Directive({
    selector: '[cwtDateTimeInputAfter][cwtDateTimeInput]',
    providers: [{provide: NG_VALIDATORS, useExisting: ValidateDatetimeInputAfterDirective, multi: true}]
})
export class ValidateDatetimeInputAfterDirective implements Validator {

    @Input("cwtDateTimeInputAfter") afterSubject: Date;

    validate(control: FormControl): ValidationErrors | null {
        if (!control.value || !this.afterSubject) return null;
        return this.afterSubject.getTime() < Date.parse(control.value)
            ? null
            : {'cwtDateTimeInputAfter': true};
    }
}
