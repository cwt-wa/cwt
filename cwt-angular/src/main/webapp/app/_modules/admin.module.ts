import {NgModule} from "@angular/core";
import {HttpClientModule} from "@angular/common/http";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {RouterModule} from "@angular/router";
import {FormsModule} from "@angular/forms";
import {AdminVoidGameComponent} from "../admin-void-game.component";
import {AdminExtractStatsComponent} from "../admin/admin-extract-stats.component";
import {AdminChatComponent} from "../message/admin-chat.component";
import {CanDeactivateGuard} from "../_services/can-deactivate-guard";
import {WriteAdminNewsComponent} from "../news/write-admin-news.component";
import {AdminComponent} from "../admin.component";
import {AdminTournamentStartComponent} from "../admin-tournament-start.component";
import {AdminGroupsStartComponent} from "../group/admin-groups-start.component";
import {AdminPlayoffsStartComponent} from "../playoffs/admin-playoffs-start.component";
import {AdminSettingsComponent} from "../admin/admin-settings.component";
import {AdminTournamentReviewComponent} from "../admin/admin-tournament-review.component";
import {AdminGroupsStartManualDrawComponent} from "../group/admin-groups-start-manual-draw.component";
import {AdminGroupsStartAutomaticDrawComponent} from "../group/admin-groups-start-automatic-draw.component";
import {CommonModule} from "@angular/common";
import {ChatModule} from "./chat.module";
import {SharedModule} from "./shared.module";
import {WriteRulesComponent} from "../rules/write-rules.component";
import {AddTechWinComponent} from "../tech-win/add-tech-win.component";
import {ReplacePlayerComponent} from "../replace-player/replace-player.component";

const adminRoutes = [
    {
        path: '',
        component: AdminComponent
    },
    {
        path: 'settings',
        component: AdminSettingsComponent
    },
    {
        path: 'chat',
        component: AdminChatComponent
    },
    {
        path: 'void-game',
        component: AdminVoidGameComponent
    },
    {
        path: 'extract-stats',
        component: AdminExtractStatsComponent
    },
    {
        path: 'news',
        component: WriteAdminNewsComponent
    },
    {
        path: 'tournaments/start',
        component: AdminTournamentStartComponent
    },
    {
        path: 'tournaments/review',
        component: AdminTournamentReviewComponent,
        canDeactivate: [CanDeactivateGuard]
    },
    {
        path: 'groups/start',
        component: AdminGroupsStartComponent
    },
    {
        path: 'playoffs/start',
        component: AdminPlayoffsStartComponent
    },
    {
        path: 'rules',
        component: WriteRulesComponent
    },
    {
        path: 'tech-win',
        component: AddTechWinComponent
    },
    {
        path: 'replace-player',
        component: ReplacePlayerComponent
    },
];

@NgModule({
    imports: [
        CommonModule,
        HttpClientModule,
        RouterModule.forChild(adminRoutes),
        FormsModule,
        NgbModule,
        SharedModule,
        ChatModule,
    ],
    declarations: [
        AdminExtractStatsComponent,
        AdminVoidGameComponent,
        AdminChatComponent,
        AdminSettingsComponent,
        AdminTournamentReviewComponent,
        WriteAdminNewsComponent,
        AdminPlayoffsStartComponent,
        AdminGroupsStartComponent,
        AdminGroupsStartManualDrawComponent,
        AdminGroupsStartAutomaticDrawComponent,
        AdminComponent,
        AdminTournamentStartComponent,
        ReplacePlayerComponent,
        AddTechWinComponent,
        WriteRulesComponent,
    ],
    exports: [],
    providers: [],
    bootstrap: [],
    entryComponents: [],
})
export class AdminModule {
}
