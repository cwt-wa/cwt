import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {RouterModule, Routes} from "@angular/router";
import {ApplicantsComponent} from "./application/applicants.component";
import {SharedModule} from "./shared.module";

const routes: Routes = [
    {
        path: '',
        component: ApplicantsComponent
    }
];


@NgModule({
    imports: [
        CommonModule,
        RouterModule.forChild(routes),
        SharedModule,
    ],
    declarations: [
        ApplicantsComponent
    ],
    exports: [],
    providers: [],
    bootstrap: [],
    entryComponents: [],
})
export class ApplicantsModule {
}

