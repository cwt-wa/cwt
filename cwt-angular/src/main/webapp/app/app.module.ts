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

const appRoutes: Routes = [
    {
        path: '',
        component: HomeComponent
    },
    {
        path: 'dulli',
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
        LoginComponent
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
