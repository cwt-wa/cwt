import {NgModule} from "@angular/core";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {ChatInputComponent} from "../message/chat-input.component";
import {ChatComponent} from "../message/chat.component";
import {SharedModule} from "./shared.module";
import {RouterModule} from "@angular/router";
import {MentionComponent} from "../message/mention.component";

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        NgbModule,
        RouterModule,
        SharedModule,
    ],
    declarations: [
        ChatInputComponent,
        ChatComponent,
        MentionComponent
    ],
    exports: [
        ChatInputComponent,
        ChatComponent,
    ],
    providers: [],
    bootstrap: [],
    entryComponents: [MentionComponent],
})
export class ChatModule {
}
