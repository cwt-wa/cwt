import {NgModule} from "@angular/core";
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {RouterModule, Routes} from "@angular/router";
import {RegisterComponent} from "../user/register.component";
import {SharedModule} from "./shared.module";
import {NgxCaptchaModule} from 'ngx-captcha';
import {FormConfirmModule} from "./form-confirm.module";

const routes: Routes = [
    {
        path: '',
        component: RegisterComponent
    }
];

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        RouterModule.forChild(routes),
        NgxCaptchaModule,
        SharedModule,
        FormConfirmModule,
    ],
    declarations: [
        RegisterComponent
    ],
    exports: [],
    providers: [],
    bootstrap: [],
    entryComponents: [],
})
export class RegisterModule {
}
