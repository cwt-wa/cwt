import {NgModule} from "@angular/core";
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {RouterModule, Routes} from "@angular/router";
import {PasswordResetComponent} from "../user/password-reset.component";
import {FormConfirmModule} from "./form-confirm.module";

const routes: Routes = [
    {
        path: '',
        component: PasswordResetComponent
    }
];

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        RouterModule.forChild(routes),
        FormConfirmModule,
    ],
    declarations: [
        PasswordResetComponent
    ],
    exports: [],
    providers: [],
    bootstrap: [],
    entryComponents: [],
})
export class PasswordResetModule {
}
