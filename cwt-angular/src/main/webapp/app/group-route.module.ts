import {NgModule} from "@angular/core";
import {HttpClientModule} from "@angular/common/http";
import {RouterModule, Routes} from "@angular/router";
import {CommonModule} from "@angular/common";
import {GroupsOverviewComponent} from "./group/groups-overview.component";
import {GroupModule} from "./group.module";

const routes: Routes = [
    {
        path: '',
        component: GroupsOverviewComponent
    }
];

@NgModule({
    imports: [
        CommonModule,
        HttpClientModule,
        RouterModule.forChild(routes),
        GroupModule,
    ],
    declarations: [],
    exports: [],
    providers: [],
    bootstrap: [],
    entryComponents: [],
})
export class GroupRouteModule {
}
