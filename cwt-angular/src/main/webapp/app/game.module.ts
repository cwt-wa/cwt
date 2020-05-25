import {NgModule} from "@angular/core";
import {HttpClientModule} from "@angular/common/http";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {RouterModule, Routes} from "@angular/router";
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {SharedModule} from "./shared.module";
import {GameDetailComponent} from "./game/game-detail.component";
import {GameStatsComponent} from "./game/game-stats.component";
import {GameOverviewComponent} from "./game/game-overview.component";
import {MapComponent} from "./game/map.component";
import {WeaponComponent} from "./_util/weapon.component";
import {PaginationModule} from "./pagination.module";
import {PlayoffsService} from "./_services/playoffs.service";

const routes: Routes = [
    {
        path: ':id',
        component: GameDetailComponent
    },
    {
        path: ':id/stats',
        component: GameStatsComponent
    },
    {
        path: '',
        component: GameOverviewComponent
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
        GameDetailComponent,
        GameStatsComponent,
        GameOverviewComponent,
        GameDetailComponent,
        MapComponent,
        WeaponComponent,
    ],
    exports: [],
    providers: [
        PlayoffsService
    ],
    bootstrap: [],
    entryComponents: [],
})
export class GameModule {
}
