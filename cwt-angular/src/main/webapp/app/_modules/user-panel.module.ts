import {NgModule} from "@angular/core";
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {RouterModule, Routes} from "@angular/router";
import {UserPanelComponent} from "../user-panel/user-panel.component";
import {SharedModule} from "./shared.module";

const routes: Routes = [
    {
        path: '',
        component: UserPanelComponent
    }
];


@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        RouterModule.forChild(routes),
        SharedModule,
    ],
    declarations: [
        UserPanelComponent
    ],
    exports: [],
    providers: [],
    bootstrap: [],
    entryComponents: [],
})
export class UserPanelModule {
}
