import {Component, ElementRef, Inject, OnInit, ViewChild} from "@angular/core";
import {CountryDto, JwtUser, PasswordChangeDto, UserChangeDto, UserDetailDto} from "../custom";
import {RequestService} from "../_services/request.service";
import {AuthService} from "../_services/auth.service";
import {Toastr} from "../_services/toastr";
import {APP_CONFIG, AppConfig} from "../app.config";

@Component({
    selector: 'cwt-user-panel',
    template: require('./user-panel.component.html')
})
export class UserPanelComponent implements OnInit {

    @ViewChild('photoFile') photoFile: ElementRef<HTMLInputElement>;

    possibleCountries: CountryDto[] = [];
    profile: UserChangeDto;
    passwordChange: PasswordChangeDto = {} as PasswordChangeDto;
    confirmPassword: string;
    private authUser: JwtUser;
    // @ts-ignore
    private user: UserDetailDto;

    constructor(private requestService: RequestService,
                private authService: AuthService,
                private toastr: Toastr,
                @Inject(APP_CONFIG) private appConfig: AppConfig) {
    }

    ngOnInit(): void {
        this.authUser = this.authService.getUserFromTokenPayload();

        this.requestService.get<UserDetailDto>(`user/${this.authUser.id}`)
            .subscribe(res => {
                this.profile = {
                    username: res.username,
                    country: res.country.id,
                    about: res.about,
                };
                this.user = res;
            });

        this.requestService.get<CountryDto[]>("country")
            .subscribe(res => this.possibleCountries = res);
    }

    showCurrentPhoto() {
        this.photoUrl = `${this.appConfig.apiEndpoint}/user/${this.authUser.id}/photo`;
        // TOOD
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
        const formData = new FormData();
        formData.append('photo', this.photoFile.nativeElement.files[0]);

        this.requestService.formDataPost(`user/${this.authUser.id}/change-photo`, formData)
            .subscribe(() => {
                this.toastr.success("Successfully saved photo.");
            });
    }
}
