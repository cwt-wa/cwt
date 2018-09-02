import {Component, ElementRef, EventEmitter, ViewChild} from '@angular/core';
import {User} from "../custom";
import {Observable} from "rxjs/Observable";
import {debounceTime, distinctUntilChanged} from "rxjs/operators";

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
            .pipe(distinctUntilChanged())
            .map(term => this.suggestedUsers.filter(u => u.username.toLowerCase().indexOf(term.toLowerCase()) > -1));
    typeAheadInputFormatter = (value: User) => value.username || null;
    typeAheadResultFormatter = (value: User) => value.username || null;
    @ViewChild('nameInput')
    private nameInput: ElementRef;

    constructor(private elementRef: ElementRef) {
    }

    public ngAfterViewInit(): void {
        (this.nameInput.nativeElement as HTMLInputElement).focus();
    }

    public onSelectTypeAheadSuggestionItem(): void {
        this.putCursorAfterMention();
    }

    public keyDown($event: KeyboardEvent): void {
        if ($event.key !== 'Backspace') {
            return;
        }

        if (!this.mentionedUser) {
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
