import {debounceTime, distinctUntilChanged, map} from 'rxjs/operators';
import {Component, ElementRef, EventEmitter, ViewChild} from '@angular/core';
import {User} from "../custom";
import {Observable} from "rxjs";
import {NgbTypeahead} from "@ng-bootstrap/ng-bootstrap";

@Component({
    selector: 'mention',
    template: require('./mention.component.html'),
    styles: [
            `
            .mention {
                display: inline-flex;
                flex-direction: row;
                flex-wrap: nowrap;
                justify-content: space-between;
                align-items: stretch;
                margin-top: -21px;
            }`,

            `
            input.name {
                margin-right: 1px;
            }`,

            `
            .name {
                flex-grow: 5;
            }`

    ]
})
export class MentionComponent {

    suggestedUsers: User[] = [];
    mentionedUser: User;
    removeMention: EventEmitter<any> = new EventEmitter();
    filterSuggestedUsersForTypehead = (text$: Observable<string>) =>
        text$
            .pipe(debounceTime(200))
            .pipe(distinctUntilChanged()).pipe(
            map(this.filterByTerm.bind(this)));
    typeAheadInputFormatter = (value: User) => value.username || null;
    typeAheadResultFormatter = (value: User) => value.username || null;
    disabled: boolean;
    mentionHasJustBeenSelected: boolean;

    @ViewChild('nameInput') private nameInput: ElementRef;
    @ViewChild('ngbTypeaheadInstance') private ngbTypeaheadInstance: NgbTypeahead;

    constructor(private elementRef: ElementRef) {
    }

    public ngAfterViewInit(): void {
        (this.nameInput.nativeElement as HTMLInputElement).focus();
    }

    public filterByTerm(term: string = this.nameInput.nativeElement.value) {
        return this.suggestedUsers.filter(
            u => u.username.toLowerCase().indexOf(term.toLowerCase()) > -1);
    }

    public onSelectTypeAheadSuggestionItem(): void {
        this.mentionHasJustBeenSelected = true;
        this.putCursorAfterMention();
    }

    public keyDown($event: KeyboardEvent): void {
        if ($event.key === 'Backspace') {
            const term = this.nameInput.nativeElement.value;
            if (!this.mentionedUser && term.length === 0) {
                this.removeMention.emit();
            }
        } else if ($event.key === 'Escape') {
            this.removeMention.emit();
        } else if ($event.key === ' ') {
            ($event.target as HTMLInputElement).blur();
        }
    }

    public onBlur() {
        this.ngbTypeaheadInstance.dismissPopup();
        const possibleUsers = this.filterByTerm();
        if (possibleUsers.length === 1) {
            this.mentionedUser = possibleUsers[0];
            this.onSelectTypeAheadSuggestionItem();
        } else {
            this.removeMention.emit();
        }
    }

    private putCursorAfterMention(): void {
        const range: Range = document.createRange();
        range.setStartAfter(this.elementRef.nativeElement);
        range.collapse(true);

        const selection: Selection = window.getSelection();
        selection.removeAllRanges();
        selection.addRange(range);
    }
}
