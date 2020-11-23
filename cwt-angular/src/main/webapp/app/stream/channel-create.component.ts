import {Component, Inject, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {ChannelCreationDto, ChannelDto, JwtUser} from "../custom";
import {AuthService} from "../_services/auth.service";
import {finalize} from "rxjs/operators";
import {APP_CONFIG, AppConfig} from "../app.config";

@Component({
    selector: 'cwt-streams',
    template: require('./channel-create.component.html')
})
export class ChannelCreateComponent implements OnInit {

    channel: ChannelCreationDto = {twitchLoginName: null, user: null};
    userAlreadyHasChannel: boolean = false;
    channelCreated: boolean = false;
    authUser: JwtUser;
    loading: boolean = true;

    constructor(private requestService: RequestService, private authService: AuthService,
                @Inject(APP_CONFIG) private appConfig: AppConfig) {
    }

    async ngOnInit() {
        this.authUser = await this.authService.authState;
        if (!this.authUser) return;
        this.channel.user = this.authUser.id;
        this.requestService.get<ChannelDto[]>('channel', {user: `${this.authUser.id}`})
            .pipe(finalize(() => this.loading = false))
            .subscribe(res => this.userAlreadyHasChannel = !!res.length);
    }

    submit() {
        this.requestService.post<ChannelDto>('channel', this.channel)
            .subscribe(({id}) => {
                this.channelCreated = true;
                fetch(`${this.appConfig.liveStreamSubscriber}/${id}`)
                    .then(res => res.json())
                    .then(console.log)
                    .catch(console.error);
            });
    }
}
