import {Directive, Input} from '@angular/core';
import {FormGroup, NG_VALIDATORS, ValidationErrors, Validator} from "@angular/forms";

@Directive({
    selector: '[cwtValidateResult]',
    providers: [{provide: NG_VALIDATORS, useExisting: ValidateResultDirective, multi: true}]
})
export class ValidateResultDirective implements Validator {

    @Input("cwtValidateResult") scoreInputs: HTMLSelectElement[];

    validate(control: FormGroup): ValidationErrors | null {
        if (this.scoreInputs == null || this.scoreInputs[0] == null || this.scoreInputs[1] == null) return null;

        const score = parseInt(control.get(this.scoreInputs[0].name).value);
        if (score == null || !Number.isInteger(score)) return null;

        const otherScore = parseInt(control.get(this.scoreInputs[1].name).value);
        if (otherScore == null || !Number.isInteger(otherScore)) return null;

        const maxScore = (Array.apply(null, this.scoreInputs[0].options).concat(Array.apply(null, this.scoreInputs[1].options)) as HTMLOptionElement[])
            .map(o => parseInt(o.value))
            .filter(n => Number.isInteger(n))
            .sort()
            .pop();

        if (score === maxScore) {
            return otherScore < score ? null : {cwtValidateResult: true}
        } else if (otherScore === maxScore) {
            return score < otherScore ? null : {cwtValidateResult: true}
        } else {
            return {cwtValidateResult: true}
        }
    }
}
