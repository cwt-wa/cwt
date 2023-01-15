import {NgModule} from "@angular/core";
import {HttpClientModule} from "@angular/common/http";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {RouterModule, Routes} from "@angular/router";
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {SharedModule} from "./shared.module";
import {GroupModule} from "./group.module";
import {ArchiveDetailComponent} from "../archive/archive-detail.component";
import {DamnArchiveComponent} from "../archive/damn-archive.component";
import {RankingComponent} from "../archive/ranking.component";
import {PlayoffModule} from "./playoff.module";


const routes: Routes = [
    {
        path: '',
        component: DamnArchiveComponent
    },
    {
        path: 'ranking',
        component: RankingComponent
    },
    {
        path: ':idOrYear',
        component: ArchiveDetailComponent
    },
];

@NgModule({
    imports: [
        CommonModule,
        HttpClientModule,
        FormsModule,
        NgbModule,
        SharedModule,
        PlayoffModule,
        GroupModule,
        RouterModule.forChild(routes),
    ],
    declarations: [
        DamnArchiveComponent,
        ArchiveDetailComponent,
        RankingComponent,
    ],
    exports: [],
    providers: [],
    bootstrap: [],
    entryComponents: [],
})
export class ArchiveModule {
}
