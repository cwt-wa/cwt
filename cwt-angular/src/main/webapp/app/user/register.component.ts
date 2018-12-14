import {Component} from "@angular/core";
import {RequestService} from "../_services/request.service";
import {UserRegistration} from "./model/user-registration";
import {AuthService} from "../_services/auth.service";
import {PreviousRouteService} from "../_services/previous-route.service";

@Component({
    selector: 'cwt-register',
    template: require('./register.component.html')
})
export class RegisterComponent {
    userRegistration: UserRegistration = new UserRegistration('', '', '');

    constructor(private requestService: RequestService, private authService: AuthService,
                private previousRouteService: PreviousRouteService) {
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
