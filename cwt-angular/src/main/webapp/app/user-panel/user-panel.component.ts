import {Component, ElementRef, OnInit, ViewChild} from "@angular/core";
import {ChannelDto, CountryDto, JwtUser, PasswordChangeDto, UserChangeDto, UserDetailDto} from "../custom";
import {RequestService} from "../_services/request.service";
import {AuthService} from "../_services/auth.service";
import {Toastr} from "../_services/toastr";
import {finalize} from "rxjs/operators";
import {BinaryService} from "../_services/binary.service";

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
    displayChannelCreationButton: boolean;

    private authUser: JwtUser;
    // @ts-ignore
    private user: UserDetailDto;

    constructor(private requestService: RequestService,
                private authService: AuthService,
                private toastr: Toastr,
                private binaryService: BinaryService) {
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

        if (this.authUser) {
            this.requestService.get<ChannelDto[]>('channel', {user: `${this.authUser.id}`})
                .subscribe(res => this.displayChannelCreationButton = !res.length);
        }
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
}
