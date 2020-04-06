import {Directive, ElementRef, OnInit} from '@angular/core';
import {NgModel} from "@angular/forms";

@Directive({
    selector: '[cwtDateTimeInput][type=text]',
})
export class DateTimeInputDirective implements OnInit {

    constructor(private ngModel: NgModel, private elem: ElementRef<HTMLInputElement>) {
    }

    ngOnInit(): void {
        this.ngModel.control.valueChanges.subscribe(() => {
            const val = this.elem.nativeElement.value;

            if (val == null || !val) return;

            const validationError = {'cwtDateTimeInput': true};
            const isoStr = val.replace(' ', 'T') + 'Z';
            const pattern = /^20[12][0-9]-[01][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]Z$/;

            if (!pattern.test(isoStr)) {
                this.ngModel.control.setErrors(validationError);
                return;
            }

            try {
                this.ngModel.control.setValue(
                    new Date(isoStr),
                    {emitModelToViewChange: false, emitViewToModelChange: true, emitEvent: false});
            } catch (_) {
                this.ngModel.control.setErrors(validationError);
            }
        });
    }
}
