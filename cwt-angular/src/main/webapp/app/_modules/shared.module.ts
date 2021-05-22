import {NgModule} from "@angular/core";
import {HttpClientModule} from "@angular/common/http";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {MarkdownComponent} from "../_util/markdown.component";
import {CwtDatePipe} from "../_util/cwt-date.pipe";
import {TimeAgoComponent} from "../_util/time-ago.component";
import {UserComponent} from "../_util/user.component";
import {RouterModule} from "@angular/router";
import {ConvertLinksPipe} from "../_util/convert-links.pipe";
import {ValidateDatetimeInputAfterDirective} from "../_util/date-time-input-after.validator";
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {StandingsOrderPipe} from "../_util/standings-order.pipe";
import {ReplayLinkPipe} from "../_util/replay-link.pipe";
import {EmailNote} from "../_util/email-note.pipe";
import {NameComponent} from "../_util/name.component";

@NgModule({
    imports: [
        CommonModule,
        HttpClientModule,
        RouterModule,
        FormsModule,
        NgbModule,
    ],
    declarations: [
        MarkdownComponent,
        TimeAgoComponent,
        CwtDatePipe,
        UserComponent,
        ConvertLinksPipe,
        ValidateDatetimeInputAfterDirective,
        StandingsOrderPipe,
        ReplayLinkPipe,
        EmailNote,
        NameComponent,
    ],
    exports: [
        MarkdownComponent,
        TimeAgoComponent,
        CwtDatePipe,
        UserComponent,
        ConvertLinksPipe,
        ValidateDatetimeInputAfterDirective,
        StandingsOrderPipe,
        ReplayLinkPipe,
        EmailNote,
        NameComponent,
    ],
    providers: [],
    bootstrap: [],
    entryComponents: [],
})
export class SharedModule {
}
