import {Component, OnInit, Inject} from "@angular/core";
import {ConfigurationService} from "../_services/configuration.service";
import {RequestService} from "../_services/request.service";
import {AuthService} from "../_services/auth.service";
import {PreviousRouteService} from "../_services/previous-route.service";
import {APP_CONFIG, AppConfig} from "../app.config";
import {UserRegistrationDto} from "../custom";

@Component({
    selector: 'cwt-register',
    template: require('./register.component.html')
})
export class RegisterComponent implements OnInit {
    userRegistration: UserRegistrationDto = {} as UserRegistrationDto;
    passwordConfirm: string;
    captchaKey: string;
    disabled: boolean;

    constructor(private requestService: RequestService,
                private authService: AuthService,
                private configurationService: ConfigurationService,
                private previousRouteService: PreviousRouteService,
                @Inject(APP_CONFIG) private appConfig: AppConfig) {
        this.captchaKey = this.appConfig.captchaKey;
    }

    ngOnInit(): void {
        this.configurationService.requestByKeys("DISABLE_REGISTRATION")
            .subscribe(res => this.disabled = res.length > 0 && res[0]?.value === 'true');
    }

    submit() {
        this.requestService.post<{ token: string }>('auth/register', this.userRegistration)
            .subscribe((wrappedToken) => {
                    this.authService.storeToken(wrappedToken.token);
                    window.location.href = this.previousRouteService.previousUrl || '/';
                }
            );
    }
}
