import {NgModule} from "@angular/core";
import {HttpModule} from "@angular/http";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {AppComponent} from "./app.component";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {WebAppViewService} from "./_services/web-app-view.service";
import {GmtClockComponent} from "./_util/gmt-clock.component";
import {RouterModule, Routes} from "@angular/router";
import {HomeComponent} from "./home/home.component";
import {PageNotFoundComponent} from "./_util/page-not-found.component";
import {RegisterComponent} from "./user/register.component";
import {LoginComponent} from "./user/login.component";
import {APP_CONFIG, appConfig} from "./app.config";
import {RequestService} from "./_services/request.service";
import {AuthService} from "./_services/auth.service";
import {AdminComponent} from "./admin.component";
import {AdminTournamentStartComponent} from "./admin-tournament-start.component";
import {ApplyComponent} from "./apply.component";

const appRoutes: Routes = [
    {
        path: '',
        component: HomeComponent
    },
    {
        path: 'register',
        component: RegisterComponent
    },
    {
        path: 'login',
        component: LoginComponent
    },
    {
        path: 'admin',
        component: AdminComponent
    },
    {
        path: 'admin/tournament/start',
        component: AdminTournamentStartComponent
    },
    {
        path: '**',
        component: PageNotFoundComponent
    }
];


@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        HttpModule,
        NgbModule.forRoot(),
        RouterModule.forRoot(appRoutes)
    ],
    declarations: [
        AppComponent,
        HomeComponent,
        PageNotFoundComponent,
        GmtClockComponent,
        RegisterComponent,
        LoginComponent,
        AdminComponent,
        AdminTournamentStartComponent,
        ApplyComponent
    ],
    providers: [
        WebAppViewService,
        AuthService,
        RequestService,
        {provide: APP_CONFIG, useValue: appConfig}
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
