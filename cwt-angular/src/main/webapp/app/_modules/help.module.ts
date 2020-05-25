import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {RouterModule, Routes} from "@angular/router";
import {HelpComponent} from "../rules/help.component";
import {SharedModule} from "./shared.module";

const routes: Routes = [
    {
        path: '',
        component: HelpComponent
    }
];


@NgModule({
    imports: [
        CommonModule,
        RouterModule.forChild(routes),
        SharedModule,
    ],
    declarations: [
        HelpComponent,
    ],
    exports: [],
    providers: [],
    bootstrap: [],
    entryComponents: [],
})
export class HelpModule {
}
