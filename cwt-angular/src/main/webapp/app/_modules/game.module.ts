import {NgModule} from "@angular/core";
import {HttpClientModule} from "@angular/common/http";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {SharedModule} from "./shared.module";
import {GameDetailComponent} from "../game/game-detail.component";
import {GameStatsComponent} from "../game/game-stats.component";
import {GameOverviewComponent} from "../game/game-overview.component";
import {MapComponent} from "../game/map.component";
import {WeaponComponent} from "../_util/weapon.component";
import {PaginationModule} from "./pagination.module";
import {RouterModule} from "@angular/router";

@NgModule({
    imports: [
        RouterModule,
        CommonModule,
        HttpClientModule,
        FormsModule,
        NgbModule,
        SharedModule,
        PaginationModule,
    ],
    declarations: [
        GameDetailComponent,
        GameStatsComponent,
        GameOverviewComponent,
        GameDetailComponent,
        MapComponent,
        WeaponComponent,
    ],
    exports: [
        GameOverviewComponent
    ],
    providers: [],
    bootstrap: [],
    entryComponents: [],
})
export class GameModule {
}
