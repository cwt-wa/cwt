import {NgModule} from "@angular/core";
import {HttpModule} from "@angular/http";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {AppComponent} from "./app.component";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {WebAppViewService} from "./_services/web-app-view.service";
import {GmtClockComponent} from "./gmt-clock.component";
import {RouterModule, Routes} from "@angular/router";
import {HomeComponent} from "./home.component";
import {PageNotFoundComponent} from "./page-not-found.component";
import {RegisterComponent} from "./register.component";
import {LoginComponent} from "./login.component";
import {APP_CONFIG, appConfig} from "./app.config";
import {RequestService} from "./_services/request.service";

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
        RequestService,
        {provide: APP_CONFIG, useValue: appConfig}
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
