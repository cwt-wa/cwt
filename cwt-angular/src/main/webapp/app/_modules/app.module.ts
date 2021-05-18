import {NgModule} from "@angular/core";
import {HttpClientModule} from "@angular/common/http";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {AppComponent} from "../app.component";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {WebAppViewService} from "../_services/web-app-view.service";
import {GmtClockComponent} from "../_util/gmt-clock.component";
import {RouterModule, Routes} from "@angular/router";
import {PageNotFoundComponent} from "../_util/page-not-found.component";
import {APP_CONFIG, appConfig} from "../app.config";
import {RequestService} from "../_services/request.service";
import {AuthService} from "../_services/auth.service";
import {ConfigurationService} from "../_services/configuration.service";
import {PreviousRouteService} from "../_services/previous-route.service";
import {Utils} from "../_util/utils";
import {CanReportService} from "../_services/can-report.service";
import {Toastr} from "../_services/toastr";
import {BetService} from "../_services/bet.service";
import {DatePipe} from "@angular/common";
import {BinaryService} from "../_services/binary.service";
import {LiveStreamComponent} from "../stream/live-stream.component";
import {CurrentTournamentService} from "../_services/current-tournament.service";
import {SharedModule} from "./shared.module";
import {TimeAgoService} from "../_services/time-ago.service";
import {PlayoffsService} from "../_services/playoffs.service";
import {StandingsOrderPipe} from "../_util/standings-order.pipe";
import {AdminCanActivateGuard} from "../_services/admin-can-activate-guard";

const appRoutes: Routes = [
    {
        path: '',
        loadChildren: () => import('./home.module').then(m => m.HomeModule)
    },
    {
        path: 'admin',
        loadChildren: () => import('./admin.module').then(m => m.AdminModule),
        canActivate: [AdminCanActivateGuard]
    },
    {
        path: 'register',
        loadChildren: () => import('./register.module').then(m => m.RegisterModule)
    },
    {
        path: 'login',
        loadChildren: () => import('./login.module').then(m => m.LoginModule)
    },
    {
        path: 'playoffs',
        loadChildren: () => import('./playoff-route.module').then(m => m.PlayoffRouteModule)
    },
    {
        path: 'apply',
        loadChildren: () => import('./apply.module').then(m => m.ApplyModule)
    },
    {
        path: 'applicants',
        loadChildren: () => import('./applicants.module').then(m => m.ApplicantsModule)
    },
    {
        path: 'groups',
        loadChildren: () => import('./group-route.module').then(m => m.GroupRouteModule),
    },
    {
        path: 'report',
        loadChildren: () => import('./report.module').then(m => m.ReportModule)
    },
    {
        path: 'games',
        loadChildren: () => import('./game-routing.module').then(m => m.GameRoutingModule)
    },
    {
        path: 'users',
        loadChildren: () => import('./user.module').then(m => m.UserModule)
    },
    {
        path: 'archive',
        loadChildren: () => import('./archive.module').then(m => m.ArchiveModule)
    },
    {
        path: 'help',
        loadChildren: () => import('./help.module').then(m => m.HelpModule)
    },
    {
        path: 'channels/create',
        loadChildren: () => import('./create-channel.module').then(m => m.CreateChannelModule)
    },
    {
        path: 'user-panel',
        loadChildren: () => import('./user-panel.module').then(m => m.UserPanelModule)
    },
    {
        path: 'password-forgotten',
        loadChildren: () => import('./password-forgotten.module').then(m => m.PasswordForgottenModule)
    },
    {
        path: 'password-reset',
        loadChildren: () => import('./password-reset.module').then(m => m.PasswordResetModule)
    },
    {
        path: "hell",
        redirectTo: "maps/hell",
    },
    {
        path: 'maps',
        loadChildren: () => import('./maps.module').then(m => m.MapsModule)
    },
    {
        path: 'streams',
        loadChildren: () => import('./game-routing.module').then(m => m.GameRoutingModule)
    },
    {
        path: '**',
        component: PageNotFoundComponent
    },
];

@NgModule({
    imports: [
        BrowserModule,
        FormsModule,
        HttpClientModule,
        NgbModule,
        RouterModule.forRoot(appRoutes),
        SharedModule,
    ],
    declarations: [
        AppComponent,
        PageNotFoundComponent,
        GmtClockComponent,
        LiveStreamComponent,
    ],
    providers: [
        AdminCanActivateGuard,
        WebAppViewService,
        AuthService,
        RequestService,
        ConfigurationService,
        DatePipe,
        StandingsOrderPipe,
        PreviousRouteService,
        Utils,
        BetService,
        CanReportService,
        BinaryService,
        CurrentTournamentService,
        Toastr,
        TimeAgoService,
        PlayoffsService,
        {provide: APP_CONFIG, useValue: appConfig}
    ],
    entryComponents: [],
    bootstrap: [AppComponent]
})

export class AppModule {

    // @ts-ignore
    constructor(private previousRouteService: PreviousRouteService) {
    }
}
