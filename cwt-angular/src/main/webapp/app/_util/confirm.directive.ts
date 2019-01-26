import {Directive, ElementRef, OnDestroy, OnInit} from '@angular/core';

@Directive({
    selector: '[cwtConfirm]',
})
export class ConfirmDirective implements OnInit, OnDestroy {

    private readonly eventType = "change";

    private readonly changeListenerFn = (e: Event) => {
        if ((e.srcElement as HTMLFormElement).name !== this.el.nativeElement.name) {
            this.el.nativeElement.checked = false;
        }
    };

    constructor(private el: ElementRef<HTMLInputElement>) {
    }

    ngOnInit(): void {
        this.el.nativeElement.form.addEventListener(this.eventType, this.changeListenerFn)
    }

    ngOnDestroy(): void {
        this.el.nativeElement.form.removeEventListener(this.eventType, this.changeListenerFn);
    }
}
