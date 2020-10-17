import {NgModule} from "@angular/core";
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {RouterModule, Routes} from "@angular/router";
import {MapsIndexComponent} from "../maps/maps-index.component";
import {SharedModule} from "./shared.module";

const routes: Routes = [
    {
        path: '',
        component: MapsIndexComponent
    },
];

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        RouterModule.forChild(routes),
        SharedModule,
    ],
    declarations: [
        MapsIndexComponent
    ],
    exports: [],
    providers: [],
    bootstrap: [],
    entryComponents: [],
})
export class MapsModule {
}
