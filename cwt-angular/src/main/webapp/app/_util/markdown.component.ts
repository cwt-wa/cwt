import {Component, Input, OnInit} from '@angular/core';
import 'marked/lib/marked.js';

@Component({
    selector: 'cwt-markdown',
    template: `<span class="markdown" [innerHTML]="compiled"></span>`
})
export class MarkdownComponent implements OnInit {
    @Input()
    raw: string;

    compiled: string;

    ngOnInit(): void {
        this.compiled = require('marked/lib/marked.js')(this.raw)
    }
}
