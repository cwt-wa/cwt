import {NgModule} from "@angular/core";
import {HttpClientModule} from "@angular/common/http";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {RouterModule, Routes} from "@angular/router";
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {SharedModule} from "./shared.module";
import {PaginationModule} from "./pagination.module";
import {UserOverviewComponent} from "../user/user-overview.component";
import {UserDetailComponent} from "../user/user-detail.component";
import {CountryComponent} from "../_util/country.component";
import {ReachComponent} from "../_util/reach.component";

const routes: Routes = [
    {
        path: '',
        component: UserOverviewComponent
    },
    {
        path: ':username',
        component: UserDetailComponent
    },

];

@NgModule({
    imports: [
        CommonModule,
        HttpClientModule,
        RouterModule.forChild(routes),
        FormsModule,
        NgbModule,
        SharedModule,
        PaginationModule
    ],
    declarations: [
        UserDetailComponent,
        UserOverviewComponent,
        CountryComponent,
        ReachComponent,
    ],
    exports: [],
    providers: [],
    bootstrap: [],
    entryComponents: [],
})
export class UserModule {
}
