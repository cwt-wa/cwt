import {NgModule} from "@angular/core";
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {RouterModule, Routes} from "@angular/router";
import {StreamsComponent} from "../stream/streams.component";
import {StreamDetailComponent} from "../stream/stream-detail.component";
import {SharedModule} from "./shared.module";

const routes: Routes = [
    {
        path: '',
        component: StreamsComponent
    },
    {
        path: ':id',
        component: StreamDetailComponent
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
        StreamDetailComponent,
        StreamsComponent
    ],
    exports: [],
    providers: [],
    bootstrap: [],
    entryComponents: [],
})
export class StreamsModule {
}
