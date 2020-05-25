import {NgModule} from "@angular/core";
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {RouterModule, Routes} from "@angular/router";
import {ChannelCreateComponent} from "./stream/channel-create.component";

const routes: Routes = [
    {
        path: '',
        component: ChannelCreateComponent
    }
];


@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        RouterModule.forChild(routes),
    ],
    declarations: [
        ChannelCreateComponent
    ],
    exports: [],
    providers: [],
    bootstrap: [],
    entryComponents: [],
})
export class CreateChannelModule {
}
