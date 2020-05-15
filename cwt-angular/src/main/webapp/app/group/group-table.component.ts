import {Component, EventEmitter, Input, Output} from '@angular/core';
import {GroupWithGamesDto} from "../custom";

@Component({
    selector: 'cwt-group-table',
    template: require('./group-table.component.html')
})
export class GroupTableComponent {
    @Input()
    public group: GroupWithGamesDto;

    @Input()
    public numberOfGroupMembersAdvancing: number;

    @Output()
    public mouseOverUser: EventEmitter<{ user: number, enter: boolean }> = new EventEmitter();
}
