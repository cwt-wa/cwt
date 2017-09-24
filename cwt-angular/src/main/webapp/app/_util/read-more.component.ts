import {Component, Input, OnInit} from '@angular/core';

@Component({
    selector: 'cwt-read-more',
    template: `
        {{cutText}}<span *ngIf="remainingText && !showRemainingText">…</span>
        <span *ngIf="remainingText && !showRemainingText">
            <a href="" (click)="showRemainingText = true; $event.preventDefault();" class="text-nowrap">
                read more
            </a>
        </span>
        <span *ngIf="showRemainingText">
            {{remainingText}}
        </span>
        <a href="" *ngIf="showRemainingText" (click)="showRemainingText = false; $event.preventDefault();">
            less
        </a>
    `
})
export class ReadMoreComponent implements OnInit {
    @Input()
    length: number;
    @Input()
    text: string;
    showRemainingText: boolean = false;
    remainingText: string;
    cutText: string;

    constructor() {
    }

    ngOnInit(): void {
        this.text = this.text.trim();
        const wordsToDisplay: string[] = this.text.split(' ', this.length);


        this.cutText = wordsToDisplay.join(' ');
        this.remainingText = this.text.slice(this.cutText.length);
    }
}
