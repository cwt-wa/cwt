import {Component, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {ChannelCreationDto, ChannelDto, JwtUser} from "../custom";
import {AuthService} from "../_services/auth.service";
import {finalize} from "rxjs/operators";

@Component({
    selector: 'cwt-streams',
    template: require('./channel-create.component.html')
})
export class ChannelCreateComponent implements OnInit {

    channel: ChannelCreationDto = {
        twitchLoginName: null,
        title: null,
        user: null,
    };
    userAlreadyHasChannel: boolean = false;
    channelCreated: boolean = false;
    authUser: JwtUser;
    loading: boolean = true;

    constructor(private requestService: RequestService, private authService: AuthService) {
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
            .subscribe(() => this.channelCreated = true);
    }
}
