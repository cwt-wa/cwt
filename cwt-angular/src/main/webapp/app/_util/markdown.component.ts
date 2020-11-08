import {Component, Input, OnInit, ViewEncapsulation} from '@angular/core';
import 'marked/lib/marked.js';

@Component({
    selector: 'cwt-markdown',
    template: `<span class="markdown" [innerHTML]="compiled"></span>`,
    encapsulation: ViewEncapsulation.None,
    styles: [`
      .markdown img {
        width: 100%;
      }
    `]
})
export class MarkdownComponent implements OnInit {
    @Input()
    raw: string;

    compiled: string;

    ngOnInit(): void {
        this.compiled = require('marked/lib/marked.js')(this.raw)
    }
}
