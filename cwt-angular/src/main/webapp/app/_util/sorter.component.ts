import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {PageDto} from "../custom";

@Component({
    selector: 'cwt-sorter',
    template: require('./sorter.component.html')
})
export class SorterComponent implements OnInit {
    @Input()
    pageOfModels: PageDto<any>;

    @Input()
    disabled: boolean;

    @Output()
    load = new EventEmitter();

    ngOnInit(): void {
    }

    sort(sortable: string, sortAscending: boolean) {
        this.pageOfModels.sortBy = sortable;
        this.pageOfModels.sortAscending = sortAscending;
        this.pageOfModels.start = 0;
        this.load.emit();
    }
}
