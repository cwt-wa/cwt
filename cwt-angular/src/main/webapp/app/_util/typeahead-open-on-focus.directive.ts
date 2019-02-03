import {Directive, HostListener, Input} from '@angular/core'
import {NgbTypeahead} from '@ng-bootstrap/ng-bootstrap';

@Directive({
    selector: 'input[ngbTypeahead][typeaheadOpenOnFocus]'
})
export class TypeaheadOpenOnFocusDirective {

    @Input("typeaheadOpenOnFocus")
    enabled: boolean = true;

    constructor(private typeahead: NgbTypeahead) {
    }

    @HostListener('focus', ['$event.target'])
    @HostListener('click', ['$event.target'])
    onClick(eventTarget: EventTarget) {
        if (this.enabled === false) return;
        if (!this.typeahead.isPopupOpen()) eventTarget.dispatchEvent(new Event('input'))
    }
}
