import {Component} from "@angular/core";
import {UserLogin} from "./model/user-login";
import {RequestService} from "../_services/request.service";
import {AuthService} from "../_services/auth.service";
const toastr = require('toastr/toastr.js');

@Component({
    selector: 'cwt-login',
    template: require('./login.component.html')
})
export class LoginComponent {
    userLogin = new UserLogin('', '');

    constructor(private requestService: RequestService, private authService: AuthService) {
    }

    onSubmit() {
        this.requestService.post<{token: string}>('auth', this.userLogin)
            .subscribe(
                (wrappedToken) => {
                    toastr.success('You have been logged in successfully.');
                    this.authService.storeToken(wrappedToken.token);
                },
                () => toastr.error('Meh.')
            );
    }
}
