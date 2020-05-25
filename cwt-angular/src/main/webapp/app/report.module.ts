import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {RouterModule, Routes} from "@angular/router";
import {ReportGameComponent} from "./game/report-game.component";
import {FormsModule} from "@angular/forms";
import {ValidateResultDirective} from "./_util/validate-result.directive";

const routes: Routes = [
    {
        path: '',
        component: ReportGameComponent
    }
];


@NgModule({
    imports: [
        CommonModule,
        RouterModule.forChild(routes),
        FormsModule,
    ],
    declarations: [
        ReportGameComponent,
        ValidateResultDirective,
    ],
    exports: [],
    providers: [],
    bootstrap: [],
    entryComponents: [],
})
export class ReportModule {
}

