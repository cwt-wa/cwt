import {NgModule} from "@angular/core";
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {ConfirmDirective} from "../_util/confirm.directive";
import {ConfirmValidator} from "../_util/confirm.validator";

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
    ],
    declarations: [
        ConfirmDirective,
        ConfirmValidator,
    ],
    exports: [],
    providers: [],
    bootstrap: [],
    entryComponents: [],
})
export class FormConfirmModule {
}
