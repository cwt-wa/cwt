import {Component, ElementRef, EventEmitter, Input, OnInit, Output, QueryList, ViewChildren} from '@angular/core';
import {GameMinimalDto} from "../custom";
import {Subject} from "rxjs";

@Component({
    selector: 'cwt-group-games',
    template: require('./group-games.component.html'),
    styles: [
            `
        td.highlight {
          background-color: rgba(0, 0, 0, 0.075);
        }
        `
    ]
})
export class GroupGamesComponent implements OnInit {

    @ViewChildren('homeUserTd') public homeUserTd: QueryList<ElementRef<HTMLTableDataCellElement>>;
    @ViewChildren('awayUserTd') public awayUserTd: QueryList<ElementRef<HTMLTableDataCellElement>>;

    @Input()
    games: GameMinimalDto[];

    @Input()
    highlightUser?: Subject<{ user: number, enter: boolean }>;

    @Output()
    public mouseOverUser: EventEmitter<{ user: number, enter: boolean }> = new EventEmitter();

    ngOnInit(): void {
        this.highlightUser && this.highlightUser.subscribe(({user, enter}) =>
            [...this.homeUserTd.toArray(), ...this.awayUserTd.toArray()]
                .filter(ref => parseInt(ref.nativeElement.getAttribute('data-user-id')) === user)
                .forEach(ref => ref.nativeElement.classList.toggle('highlight', enter)));
    }
}
