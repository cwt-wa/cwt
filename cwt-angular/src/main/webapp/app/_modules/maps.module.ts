import {NgModule} from "@angular/core";
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {RouterModule, Routes} from "@angular/router";
import {MapsIndexComponent} from "../maps/maps-index.component";
import {SharedModule} from "./shared.module";
import {PaginationModule} from "./pagination.module";
import {HellMapsComponent} from "../maps/hell-maps.component";

const routes: Routes = [
    {
        path: '',
        component: MapsIndexComponent
    },
    {
        path: 'hell',
        component: HellMapsComponent
    }
];

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        RouterModule.forChild(routes),
        SharedModule,
        PaginationModule,
    ],
    declarations: [
        MapsIndexComponent,
        HellMapsComponent
    ],
    exports: [],
    providers: [],
    bootstrap: [],
    entryComponents: [],
})
export class MapsModule {
}
