import {NgModule} from "@angular/core";
import {HttpClientModule} from "@angular/common/http";
import {RouterModule, Routes} from "@angular/router";
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {PlayoffsTreeComponent} from "../playoffs/playoffs-tree.component";
import {PlayoffModule} from "./playoff.module";

const routes: Routes = [
    {
        path: '',
        component: PlayoffsTreeComponent
    },
];

@NgModule({
    imports: [
        CommonModule,
        HttpClientModule,
        RouterModule.forChild(routes),
        FormsModule,
        PlayoffModule,
    ],
    declarations: [],
    exports: [],
    providers: [],
    bootstrap: [],
    entryComponents: [],
})
export class PlayoffRouteModule {
}
