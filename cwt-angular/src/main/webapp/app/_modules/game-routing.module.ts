import {NgModule} from "@angular/core";
import {RouterModule, Routes} from "@angular/router";
import {CommonModule} from "@angular/common";
import {GameDetailComponent} from "../game/game-detail.component";
import {GameStatsComponent} from "../game/game-stats.component";
import {GameOverviewComponent} from "../game/game-overview.component";
import {SharedModule} from "./shared.module";
import {GameModule} from "./game.module";

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
        SharedModule,
        GameModule,
        RouterModule.forChild(routes),
    ],
    declarations: [],
    exports: [],
    providers: [],
    bootstrap: [],
    entryComponents: [],
})
export class GameRoutingModule {
}
