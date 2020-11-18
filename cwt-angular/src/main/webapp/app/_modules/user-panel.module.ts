import {NgModule} from "@angular/core";
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {RouterModule, Routes} from "@angular/router";
import {UserPanelComponent} from "../user-panel/user-panel.component";
import {SharedModule} from "./shared.module";
import {FormConfirmModule} from "./form-confirm.module";
import {ChannelCreationStatementComponent} from "../user-panel/channel-creation-statement.component";

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
        FormConfirmModule,
    ],
    declarations: [
        UserPanelComponent,
        ChannelCreationStatementComponent,
    ],
    exports: [],
    providers: [],
    bootstrap: [],
    entryComponents: [],
})
export class UserPanelModule {
}
