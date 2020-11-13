import {NgModule} from "@angular/core";
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {RouterModule} from "@angular/router";
import {SharedModule} from "./shared.module";

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        RouterModule,
        SharedModule,
    ],
    declarations: [],
    exports: [],
    providers: [],
    bootstrap: [],
    entryComponents: [],
})
export class StreamsModule {
}
