import {Component, ElementRef, Inject, OnInit, ViewChild} from "@angular/core";
import {ChannelDto, CountryDto, JwtUser, PasswordChangeDto, UserChangeDto, UserDetailDto} from "../custom";
import {RequestService} from "../_services/request.service";
import {AuthService} from "../_services/auth.service";
import {Toastr} from "../_services/toastr";
import {finalize} from "rxjs/operators";
import {BinaryService} from "../_services/binary.service";
import {APP_CONFIG, AppConfig} from "../app.config";

@Component({
    selector: 'cwt-user-panel',
    template: require('./user-panel.component.html')
})
export class UserPanelComponent implements OnInit {

    @ViewChild('photoFile') photoFile: ElementRef<HTMLInputElement>;
    @ViewChild('photoPreview') photoPreview: ElementRef<HTMLImageElement>;

    possibleCountries: CountryDto[] = [];
    profile: UserChangeDto;
    passwordChange: PasswordChangeDto = {} as PasswordChangeDto;
    confirmPassword: string;
    showPhoto = false;
    loadingPhoto: boolean = false;
    thereIsNoPhoto: boolean;
    userChannel: ChannelDto;
    togglingBotInvite: boolean = false;
    botInvited: boolean = null;
    botRequestFailed: boolean = false;
    togglingBotAutoJoin: boolean = false;
    notification;

    private authUser: JwtUser;
    // @ts-ignore
    private user: UserDetailDto;

    constructor(private requestService: RequestService,
                private authService: AuthService,
                private toastr: Toastr,
                private binaryService: BinaryService,
                @Inject(APP_CONFIG) private appConfig: AppConfig) {
    }

    async ngOnInit() {
        this.authUser = await this.authService.authState;

        this.requestService.get<UserDetailDto>(`user/${this.authUser.id}`, {'include-email': "true"})
            .subscribe(res => {
                this.profile = {
                    username: res.username,
                    country: res.country.id,
                    about: res.about,
                    email: res.email,
                };
                this.user = res;
            });

        this.requestService.get<CountryDto[]>("country")
            .subscribe(res => this.possibleCountries = res);

        const sub = await (await navigator.serviceWorker.ready).pushManager.getSubscription()
            .then(res => res.endpoint)
            .catch(() => null);
        this.requestService.get(`user/${this.authUser.id}/notification`, sub != null ? {sub} : null)
            .subscribe(res => this.notification = res.human.sort((a,b) => b.pos - a.pos));

        if (this.authUser) {
            this.requestService.get<ChannelDto[]>('channel', {user: `${this.authUser.id}`})
                .subscribe(async res => {
                    this.userChannel = res[0]
                    if (this.userChannel) {
                        try {
                            this.botInvited = (await this.twitchBotEndpoint('status')).joined;
                        } catch (err) {
                            this.botRequestFailed = true;
                            console.error('bot status failed', err);
                        }
                    }
                });
        }
    }

    async subscribe() {
        const perm = await Notification.requestPermission().catch(e => e);
        console.log('perm:', perm);
        if (perm !== "granted") {
            this.toastr.error("Cannot send notifications without permission.");
            return;
        }

        // get sub
        const reg = await navigator.serviceWorker.ready
        let sub = await reg.pushManager.getSubscription();
        if (!sub) {
          console.log('creating sub');
          const key = await (await fetch('https://push.zemke.io/key')).text();
          console.log('public key:', key);
          sub = await reg.pushManager.subscribe({
            userVisibleOnly: true,
            applicationServerKey: key
          });
        }
        console.log('sub:', sub);
        const payload = {
            subscription: sub,
            setting: this.notification,
        };
        this.requestService.post(`user/${this.authUser.id}/notification`, payload)
            .subscribe(() => this.toastr.success("Subscribed to notifications on this device"))
    }

    async twitchBotEndpoint(action: string) {
        const method = action === 'status' ? 'GET' : "POST";
        return fetch(
            `${this.appConfig.twitchBotEndpoint}/${this.userChannel.login}/${action}`, {
                method,
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': this.authService.getToken(),
                }
            }).then(res => {
                const statusStr = res.status.toString();
                if (!statusStr.startsWith("2")) throw Error(statusStr);
                return res.json();
            });
    }

    showCurrentPhoto() {
        this.loadingPhoto = true;
        this.showPhoto = true;

        this.binaryService.getUserPhoto(this.authUser.id)
            .pipe(finalize(() => this.loadingPhoto = false))
            .subscribe(
                res => {
                    this.photoPreview.nativeElement.src = res;
                    this.thereIsNoPhoto = false;
                },
                () => this.thereIsNoPhoto = true);
    }

    submitProfile() {
        this.requestService.post<{token: string}>(`user/${this.authUser.id}`, this.profile)
            .subscribe(res => {
                if (res && res.token != null) {
                    this.authService.storeToken(res.token);
                    this.toastr.success("Successfully saved profile. Page will refresh shortly…");
                    setTimeout(() => location.reload(), 2000);
                } else {
                    this.toastr.success("Successfully saved profile.")
                }
            });
    }

    submitPasswordChange() {
        this.requestService.post(`user/${this.authUser.id}/change-password`, this.passwordChange)
            .subscribe(() => this.toastr.success("Successfully changed password."));
    }

    submitPhoto() {
        this.binaryService.saveUserPhoto(this.authUser.id, this.photoFile.nativeElement.files[0])
            .subscribe(() => {
                this.toastr.success("Successfully saved photo.");
                this.showPhoto = false;
            });
    }

    deletePhoto() {
        this.binaryService.deleteUserPhoto(this.authUser.id).subscribe(() => {
            this.toastr.success("Successfully deleted photo.");
            this.showPhoto = false;
        }, () => this.toastr.error("An unknown error occurred."));
    }

    toggleBotAutoJoin() {
        this.togglingBotAutoJoin = true;
        this.requestService.put<ChannelDto>(
                `channel/${this.userChannel.id}/botAutoJoin`,
                {botAutoJoin: this.userChannel.botAutoJoin})
            .pipe(finalize(() => this.togglingBotAutoJoin = false))
            .subscribe(res => this.userChannel = res);
    }

    async inviteBot() {
        return this.toggleBot('join');
    }

    async revokeBot() {
        return this.toggleBot('part');
    }

    private async toggleBot(joinOrPart: string) {
        try {
            await this.twitchBotEndpoint(joinOrPart);
            this.botInvited = !this.botInvited;
            this.toastr.success(`Successfully ${joinOrPart}ed.`);
        } catch (err) {
            console.error(`${joinOrPart} not working:`, err);
            this.toastr.error("Excuse me, Beep Boop no working.");
            this.togglingBotInvite = false;
        }
    }
}
