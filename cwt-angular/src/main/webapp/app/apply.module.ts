import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {RouterModule, Routes} from "@angular/router";
import {ApplyComponent} from "./application/apply.component";
import {SharedModule} from "./shared.module";
import {FormsModule} from "@angular/forms";

const routes: Routes = [
    {
        path: '',
        component: ApplyComponent
    }
];


@NgModule({
    imports: [
        CommonModule,
        RouterModule.forChild(routes),
        FormsModule,
        SharedModule,
    ],
    declarations: [
        ApplyComponent,
    ],
    exports: [],
    providers: [],
    bootstrap: [],
    entryComponents: [],
})
export class ApplyModule {
}
