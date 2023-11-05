import {RouterModule, Routes} from "@angular/router";
import {NgModule} from "@angular/core";
import {HttpClientModule} from "@angular/common/http";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {HomeComponent} from "../home/home.component";
import {DonateComponent} from "../home/donate.component";
import {ApplyBannerComponent} from "../application/apply-banner.component";
import {SchedulerComponent} from "../scheduler/scheduler.component";
import {SharedModule} from "./shared.module";
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {ChatModule} from "./chat.module";
import {DateTimeInputDirective} from "../_util/date-time-input.directive";

const homeRoutes: Routes = [
    {
        path: '',
        component: HomeComponent
    },
];

@NgModule({
    imports: [
        CommonModule,
        HttpClientModule,
        NgbModule,
        FormsModule,
        RouterModule.forChild(homeRoutes),
        SharedModule,
        ChatModule,
    ],
    declarations: [
        HomeComponent,
        ApplyBannerComponent,
        SchedulerComponent,
        ApplyBannerComponent,
        DateTimeInputDirective,
        DonateComponent,
    ],
    providers: [],
    bootstrap: [],
    entryComponents: [],

})
export class HomeModule {
}
