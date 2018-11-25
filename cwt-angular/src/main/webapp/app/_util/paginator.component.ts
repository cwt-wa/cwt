import {Component, EventEmitter, HostListener, Input, OnInit, Output} from '@angular/core';
import {PageDto} from "../custom";

@Component({
    selector: 'cwt-paginator',
    template: require('./paginator.component.html')
})
export class PaginatorComponent implements OnInit {
    @Input()
    page: PageDto<any>;

    @Output("goTo")
    goToEventEmitter: EventEmitter<number> = new EventEmitter<number>();

    suggestedStarts: number[];

    ngOnInit(): void {
        this.suggestedStarts = this.getSuggestedStarts(
            this.page.start, this.page.totalPages, this.getMaxSuggestions(window.innerWidth));
    }

    @HostListener('window:resize', ['$event'])
    onResize(event: Event) {
        this.suggestedStarts = this.getSuggestedStarts(
            this.page.start, this.page.totalPages, this.getMaxSuggestions((event.target as Window).innerWidth));
    }

    goTo(start: number) {
        this.goToEventEmitter.emit(start)
    }

    getSuggestedStarts(currentStart: number, totalPages: number, oddMaxSuggestions: number): number[] {
        if (currentStart >= totalPages) throw new Error(`currentStart may not be greater or equal to totalPages.`);
        if (oddMaxSuggestions % 2 === 0) throw new Error(`oddMaxSuggestions may only be an odd number.`);

        if (totalPages < oddMaxSuggestions) {
            return new Array(totalPages).fill(null).map((_value, index) => index);
        }

        const maxSuggestionsOnEitherSide = (oddMaxSuggestions - 1) / 2;
        const suggestedStarts: number[] = [];

        const numOfStartsAfterCurrentStart = totalPages - (currentStart + 1);

        let numOfUnusedStartSuggestions: number = 0;
        if (numOfStartsAfterCurrentStart < maxSuggestionsOnEitherSide) {
            numOfUnusedStartSuggestions = maxSuggestionsOnEitherSide - numOfStartsAfterCurrentStart;
        }

        for (let i = currentStart - 1; i >= 0 && suggestedStarts.length < maxSuggestionsOnEitherSide + numOfUnusedStartSuggestions; i--) {
            suggestedStarts.push(i);
        }

        suggestedStarts.reverse();
        suggestedStarts.push(currentStart);

        for (let i = currentStart + 1; suggestedStarts.length < oddMaxSuggestions && i < totalPages; i++) {
            suggestedStarts.push(i);
        }

        return suggestedStarts;
    }

    private getMaxSuggestions(windowInnerWidth: number): number {
        let maxSuggestions;
        if (windowInnerWidth < 576) {
            maxSuggestions = 7;
        } else if (windowInnerWidth >= 1200) {
            maxSuggestions = 17;
        } else if (windowInnerWidth >= 992) {
            maxSuggestions = 13;
        } else if (windowInnerWidth >= 768) {
            maxSuggestions = 17;
        } else if (windowInnerWidth >= 576) {
            maxSuggestions = 13;
        }
        return maxSuggestions;
    }
}
