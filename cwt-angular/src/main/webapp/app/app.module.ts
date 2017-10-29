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
import {ApplyBannerComponent} from "./application/apply-banner.component";
import {ApplyComponent} from "./application/apply.component";
import {ApplicantsComponent} from "./application/applicants.component";
import {AdminGroupsStartComponent} from "./group/admin-groups-start.component";
import {ConfigurationService} from "./_services/configuration.service";
import {GroupsOverviewComponent} from "./group/groups-overview.component";
import {GroupTableComponent} from "./group/group-table.component";
import {ReportGameComponent} from "./game/report-game.component";
import {AdminPlayoffsStartComponent} from "./playoffs/admin-playoffs-start.component";
import {StandingsOrderPipe} from "./_util/standings-order.pipe";
import {PlayoffsTreeComponent} from "./playoffs/playoffs-tree.component";
import {GameDetailComponent} from "./game/game-detail.component";
import {RatingComponent} from "./game/rating.component";
import {DamnArchiveComponent} from "./archive/damn-archive.component";
import {ReadMoreComponent} from "./_util/read-more.component";
import {ArchiveDetailComponent} from "./archive/archive-detail.component";
import {GroupGamesComponent} from "./group/group-games.component";
import {TimeAgoComponent} from "./_util/time-ago.component";
import {TimeAgoService} from "./_services/time-ago.service";
import {WriteAdminNewsComponent} from "./news/write-admin-news.component";
import {ChatComponent} from "./message/chat.component";
import {ChatInputComponent} from "./message/chat-input.component";
import {MentionComponent} from "./message/mention.component";

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
        path: 'admin/tournaments/start',
        component: AdminTournamentStartComponent
    },
    {
        path: 'admin/groups/start',
        component: AdminGroupsStartComponent
    },
    {
        path: 'admin/playoffs/start',
        component: AdminPlayoffsStartComponent
    },
    {
        path: 'playoffs',
        component: PlayoffsTreeComponent
    },
    {
        path: 'apply',
        component: ApplyComponent
    },
    {
        path: 'applicants',
        component: ApplicantsComponent
    },
    {
        path: 'groups',
        component: GroupsOverviewComponent,
    },
    {
        path: 'report',
        component: ReportGameComponent
    },
    {
        path: 'game/',
        component: ReportGameComponent
    },
    {
        path: 'game/:id',
        component: GameDetailComponent
    },
    {
        path: 'archive',
        component: DamnArchiveComponent
    },
    {
        path: 'archive/:idOrYear',
        component: ArchiveDetailComponent
    },
    {
        path: 'admin/news',
        component: WriteAdminNewsComponent
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
        ApplyBannerComponent,
        ApplyComponent,
        ApplicantsComponent,
        AdminGroupsStartComponent,
        GroupsOverviewComponent,
        GroupTableComponent,
        ReportGameComponent,
        AdminPlayoffsStartComponent,
        StandingsOrderPipe,
        PlayoffsTreeComponent,
        GameDetailComponent,
        RatingComponent,
        DamnArchiveComponent,
        ReadMoreComponent,
        ArchiveDetailComponent,
        GroupGamesComponent,
        TimeAgoComponent,
        WriteAdminNewsComponent,
        ChatComponent,
        ChatInputComponent,
        MentionComponent,
    ],
    providers: [
        WebAppViewService,
        AuthService,
        RequestService,
        ConfigurationService,
        StandingsOrderPipe,
        TimeAgoService,
        {provide: APP_CONFIG, useValue: appConfig}
    ],
    entryComponents: [MentionComponent],
    bootstrap: [AppComponent]
})

export class AppModule {
}
