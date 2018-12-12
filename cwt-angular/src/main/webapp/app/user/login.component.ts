import {Component} from "@angular/core";
import {UserLogin} from "./model/user-login";
import {RequestService} from "../_services/request.service";
import {AuthService} from "../_services/auth.service";
import {PreviousRouteService} from "../_services/previous-route.service";

@Component({
    selector: 'cwt-login',
    template: require('./login.component.html')
})
export class LoginComponent {
    userLogin = new UserLogin('', '');

    constructor(private requestService: RequestService, private authService: AuthService,
                private previousRouteService: PreviousRouteService) {

    }

    onSubmit() {
        this.requestService.post<{ token: string }>('auth/login', this.userLogin)
            .subscribe((wrappedToken) => {
                    this.authService.storeToken(wrappedToken.token);
                    window.location.href = this.previousRouteService.previousUrl || '/';
                }
            );
    }
}
