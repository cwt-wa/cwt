import {Component, Input} from "@angular/core";
import {Application, Group} from "../custom";

@Component({
    selector: 'cwt-admin-groups-start-automatic-draw',
    template: require('./admin-groups-start-automatic-draw.component.html')
})
export class AdminGroupsStartAutomaticDrawComponent {

    @Input()
    groups: Group[];

    @Input()
    applications: Application[];
}
