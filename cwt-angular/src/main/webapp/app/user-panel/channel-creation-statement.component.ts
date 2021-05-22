import {Component, Input, Inject} from "@angular/core";
import {APP_CONFIG, AppConfig} from "../app.config";

@Component({
    selector: 'cwt-channel-creation-statement',
    template: `
        <p *ngIf="includeRegisterSentence">
            Register your channel to benefit from {{nameShort}} and Twitch interoperation:
        </p>
        <p>
            When you go live a very visible banner will appear on the top
            of the page advertising your live stream to the {{nameShort}} visitors.
        </p>
        <p>
            Your live stream broadcasts will be linked to the respective game on the {{nameShort}} site.<br>
            Allowing for easy finding of broadcasts and later watching in the context of {{nameShort}}.
        </p>
        <p>
            You can schedule live streams on games participants of the tournament have scheduled
            and thereby attract people early on.
        </p>
        <p>
            There is a dedicated {{nameShort}} Twitch chat bot which makes “beep boop” and has some nice commands
            to enrich the live streaming experience for your viewers.
        </p>
        <p>
            These features will only trigger when you include “{{nameShort}}” in the title of your live stream.<br>
            Therefore your Twitch channel will not publish information on {{nameShort}} if you also perform other
            duties with your Twitch channel other than streaming {{nameShort}} games.
        </p>
        <p>
            These features are all automated and work best when you include the players’ names as they
            are on the {{nameShort}} site in the title of your live stream.
        </p>
    `
})
export class ChannelCreationStatementComponent {

    @Input() includeRegisterSentence: boolean = true;

    nameShort: string;

    constructor(@Inject(APP_CONFIG) private appConfig: AppConfig) {
        this.nameShort = this.appConfig.nameShort;
        console.log(this.appConfig);
    }
}
