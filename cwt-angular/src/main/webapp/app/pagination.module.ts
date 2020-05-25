import {NgModule} from "@angular/core";
import {HttpClientModule} from "@angular/common/http";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {RouterModule} from "@angular/router";
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {SharedModule} from "./shared.module";
import {SorterComponent} from "./_util/sorter.component";
import {PaginatorComponent} from "./_util/paginator.component";

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
        SorterComponent,
        PaginatorComponent
    ],
    exports: [
        SorterComponent,
        PaginatorComponent
    ],
    providers: [],
    bootstrap: [],
    entryComponents: [],
})
export class PaginationModule {
}
