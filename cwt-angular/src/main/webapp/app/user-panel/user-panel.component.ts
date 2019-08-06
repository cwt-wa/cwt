import {Component, ElementRef, OnInit, ViewChild} from "@angular/core";
import {CountryDto, JwtUser, PasswordChangeDto, UserChangeDto, UserDetailDto} from "../custom";
import {RequestService} from "../_services/request.service";
import {AuthService} from "../_services/auth.service";
import {Toastr} from "../_services/toastr";
import {finalize} from "rxjs/operators";

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

    private authUser: JwtUser;
    // @ts-ignore
    private user: UserDetailDto;

    constructor(private requestService: RequestService,
                private authService: AuthService,
                private toastr: Toastr) {
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
        this.loadingPhoto = true;
        this.showPhoto = true;
        this.requestService.getBlob(`user/${this.authUser.id}/photo`)
            .pipe(finalize(() => this.loadingPhoto = false))
            .subscribe(res => {
                // @ts-ignore
                this.photoPreview.nativeElement.src = (window.URL || window.webkitURL).createObjectURL(res);
            });
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
                this.showPhoto = false;
            });
    }
}
