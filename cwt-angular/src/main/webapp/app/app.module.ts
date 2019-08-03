import {NgModule} from "@angular/core";
import {HttpClientModule} from "@angular/common/http";
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
import {StreamsComponent} from "./stream/streams.component";
import {AdminGroupsStartManualDrawComponent} from "./group/admin-groups-start-manual-draw.component";
import {AdminGroupsStartAutomaticDrawComponent} from "./group/admin-groups-start-automatic-draw.component";
import {GameOverviewComponent} from "./game/game-overview.component";
import {UserComponent} from "./_util/user.component";
import {PaginatorComponent} from "./_util/paginator.component";
import {UserOverviewComponent} from "./user/user-overview.component";
import {SorterComponent} from "./_util/sorter.component";
import {CountryComponent} from "./_util/country.component";
import {ReachComponent} from "./_util/reach.component";
import {UserDetailComponent} from "./user/user-detail.component";
import {PreviousRouteService} from "./_services/previous-route.service";
import {Utils} from "./_util/utils";
import {PlayoffsService} from "./_services/playoffs.service";
import {ConfirmDirective} from "./_util/confirm.directive";
import {ValidateResultDirective} from "./_util/validate-result.directive";
import {AddTechWinComponent} from "./tech-win/add-tech-win.component";
import {TypeaheadOpenOnFocusDirective} from "./_util/typeahead-open-on-focus.directive";
import {ReplacePlayerComponent} from "./replace-player/replace-player.component";
import {MarkdownComponent} from "./_util/markdown.component";
import {AdminSettingsComponent} from "./admin/admin-settings.component";
import {AdminTournamentReviewComponent} from "./admin/admin-tournament-review.component";
import {CanDeactivateGuard} from "./_services/can-deactivate-guard";
import {CanReportService} from "./_services/can-report.service";
import {Toastr} from "./_services/toastr";
import {BetService} from "./_services/bet.service";
import {UserPanelComponent} from "./user-panel/user-panel.component";
import {ConfirmValidator} from "./_util/confirm.validator";
import {StreamDetailComponent} from "./stream/stream-detail.component";
import {SchedulerComponent} from "./scheduler/scheduler.component";
import {ChannelCreateComponent} from "./stream/channel-create.component";
import {DateTimeInputDirective} from "./_util/date-time-input.directive";

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
        path: 'games',
        component: GameOverviewComponent
    },
    {
        path: 'users',
        component: UserOverviewComponent
    },
    {
        path: 'users/:username',
        component: UserDetailComponent
    },
    {
        path: 'games/:id',
        component: GameDetailComponent
    },
    {
        path: 'archive',
        component: DamnArchiveComponent
    },
    {
        path: 'streams',
        component: StreamsComponent
    },
    {
        path: 'streams/:id',
        component: StreamDetailComponent
    },
    {
        path: 'channels/create',
        component: ChannelCreateComponent
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
        path: 'admin/tech-win',
        component: AddTechWinComponent
    },
    {
        path: 'admin/replace-player',
        component: ReplacePlayerComponent
    },
    {
        path: 'admin/settings',
        component: AdminSettingsComponent
    },
    {
        path: 'admin/tournaments/review',
        component: AdminTournamentReviewComponent,
        canDeactivate: [CanDeactivateGuard]
    },
    {
        path: 'user-panel',
        component: UserPanelComponent
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
        HttpClientModule,
        NgbModule,
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
        AdminGroupsStartManualDrawComponent,
        AdminGroupsStartAutomaticDrawComponent,
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
        StreamsComponent,
        UserComponent,
        UserOverviewComponent,
        GameOverviewComponent,
        CountryComponent,
        PaginatorComponent,
        SorterComponent,
        UserDetailComponent,
        ReachComponent,
        ConfirmDirective,
        ConfirmValidator,
        ValidateResultDirective,
        TypeaheadOpenOnFocusDirective,
        AddTechWinComponent,
        ReplacePlayerComponent,
        MarkdownComponent,
        AdminSettingsComponent,
        AdminTournamentReviewComponent,
        UserPanelComponent,
        StreamDetailComponent,
        ChannelCreateComponent,
        DateTimeInputDirective,
        SchedulerComponent,
    ],
    providers: [
        WebAppViewService,
        AuthService,
        RequestService,
        ConfigurationService,
        StandingsOrderPipe,
        TimeAgoService,
        PreviousRouteService,
        Utils,
        PlayoffsService,
        BetService,
        CanReportService,
        CanDeactivateGuard,
        Toastr,
        {provide: APP_CONFIG, useValue: appConfig}
    ],
    entryComponents: [MentionComponent],
    bootstrap: [AppComponent]
})

export class AppModule {

    // @ts-ignore
    constructor(private previousRouteService: PreviousRouteService) {
    }
}
