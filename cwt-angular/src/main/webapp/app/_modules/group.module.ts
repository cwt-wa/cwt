import {NgModule} from "@angular/core";
import {HttpClientModule} from "@angular/common/http";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {SharedModule} from "./shared.module";
import {GroupGamesComponent} from "../group/group-games.component";
import {GroupsOverviewComponent} from "../group/groups-overview.component";
import {GroupTableComponent} from "../group/group-table.component";
import {RouterModule} from "@angular/router";

@NgModule({
    imports: [
        CommonModule,
        HttpClientModule,
        FormsModule,
        NgbModule,
        RouterModule,
        SharedModule,
    ],
    declarations: [
        GroupGamesComponent,
        GroupsOverviewComponent,
        GroupTableComponent,
    ],
    exports: [
        GroupGamesComponent,
        GroupsOverviewComponent,
        GroupTableComponent,
    ],
    providers: [],
    bootstrap: [],
    entryComponents: [],
})
export class GroupModule {
}
