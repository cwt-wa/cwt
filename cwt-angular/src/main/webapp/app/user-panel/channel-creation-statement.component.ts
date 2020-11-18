import {Component} from "@angular/core";

@Component({
    selector: 'cwt-channel-creation-statement',
    template: `
        <p>
            Register your channel to benefit from CWT and Twitch interoperation:
        </p>
        <p>
            When you go live a very visible banner will appear on the top
            of the page advertising your live stream to the CWT visitors.
        </p>
        <p>
            Your live stream broadcasts will be linked to the respective game on the CWT site.<br>
            Allowing for easy finding of broadcasts and later watching in the context of CWT.
        </p>
        <p>
            You can schedule live streams on games participants of the tournament have scheduled
            and thereby attract people early on.
        </p>
        <p>
            These features will only trigger when you include “CWT” in the title of your live stream.<br>
            Therefore your Twitch channel will not publish information on CWT if you also perform other
            duties with your Twitch channel other than streaming CWT games.
        </p>
        <p>
            These features are all automated and work best when you include the players’ names as they
            are on the CWT site in the title of your live stream.
        </p>
    `
})
export class ChannelCreationStatementComponent {
}
