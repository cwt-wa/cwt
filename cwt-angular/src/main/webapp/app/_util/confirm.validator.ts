import {Directive, Input} from '@angular/core';
import {AbstractControl, FormGroup, NG_VALIDATORS, ValidationErrors, Validator, ValidatorFn} from "@angular/forms";

export function confirmValidator(...fields: string[]): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } | null => {
        const distinctFieldValues = fields
            .map((f => control.get(f) != null ? control.get(f).value : null))
            .filter(v => v != null)
            .reduce((previousValue, currentValue) => {
                if (previousValue.indexOf(currentValue) === -1) previousValue.push(currentValue);
                return previousValue
            }, []);

        return distinctFieldValues.length === fields.length ? {'confirmValidator': true} : null;
    };
}

const cwtConfirmValidator = "cwtConfirmValidator";

@Directive({
    selector: `[${cwtConfirmValidator}]`,
    providers: [{provide: NG_VALIDATORS, useExisting: ConfirmValidator, multi: true}]
})
export class ConfirmValidator implements Validator {

    @Input(cwtConfirmValidator) fields: string;

    validate(control: FormGroup): ValidationErrors | null {
        if (this.fields == null) return null;
        return confirmValidator(...this.fields.split(','))(control);
    }
}
