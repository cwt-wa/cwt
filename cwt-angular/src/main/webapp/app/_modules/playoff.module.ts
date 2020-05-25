import {NgModule} from "@angular/core";
import {HttpClientModule} from "@angular/common/http";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {RouterModule} from "@angular/router";
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {SharedModule} from "./shared.module";
import {PlayoffsTreeComponent} from "../playoffs/playoffs-tree.component";

@NgModule({
    imports: [
        CommonModule,
        HttpClientModule,
        RouterModule,
        FormsModule,
        NgbModule,
        SharedModule,
    ],
    declarations: [
        PlayoffsTreeComponent
    ],
    exports: [
        PlayoffsTreeComponent
    ],
    providers: [],
    bootstrap: [],
    entryComponents: [],
})
export class PlayoffModule {
}
