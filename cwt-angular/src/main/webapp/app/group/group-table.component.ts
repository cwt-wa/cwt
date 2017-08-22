import {Component, Input} from '@angular/core';
import {Group} from "../custom";

@Component({
    selector: 'cwt-group-table',
    template: require('./group-table.component.html')
})
export class GroupTableComponent {
    @Input()
    public group: Group;

    @Input()
    public numberOfGroupMembersAdvancing: number;
}
