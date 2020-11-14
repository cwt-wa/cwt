import {Component, ElementRef, EventEmitter, Input, OnInit, Output, QueryList, ViewChildren} from '@angular/core';
import {GroupWithGamesDto} from "../custom";
import {Subject} from "rxjs";

@Component({
    selector: 'cwt-group-table',
    template: require('./group-table.component.html'),
    styles: [
        `
        tr.highlight {
          background-color: rgba(0, 0, 0, 0.4);
        }
        `
    ]
})
export class GroupTableComponent implements OnInit {

    @ViewChildren('standingTr') public standingTableRows: QueryList<ElementRef<HTMLTableRowElement>>;

    @Input()
    public group: GroupWithGamesDto;

    @Input()
    public highlightUser?: Subject<{ user: number, enter: boolean }>;

    @Output()
    public mouseOverUser: EventEmitter<{ user: number, enter: boolean }> = new EventEmitter();

    public numberOfGroupMembersAdvancing: number;

    ngOnInit(): void {
        this.numberOfGroupMembersAdvancing = this.group.tournament.numOfGroupAdvancing;
        this.highlightUser && this.highlightUser.subscribe(event =>
            this.standingTableRows.toArray()
                .filter(tr => parseInt(tr.nativeElement.getAttribute('data-user')) === event.user)
                .forEach(tr => tr.nativeElement.classList.toggle('highlight', event.enter)))
    }
}
