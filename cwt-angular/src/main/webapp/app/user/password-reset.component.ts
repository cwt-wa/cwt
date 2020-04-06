import {Component} from "@angular/core";
import {RequestService} from "../_services/request.service";
import {ActivatedRoute} from "@angular/router";
import {AuthService} from "../_services/auth.service";

@Component({
    selector: 'cwt-password-forgotten',
    template: require('./password-reset.component.html')
})
export class PasswordResetComponent {

    confirmPassword: string;
    newPassword: string;

    constructor(private requestService: RequestService,
                private route: ActivatedRoute,
                private authService: AuthService) {
    }

    onSubmit() {
        const body = {
            password: this.newPassword,
            resetKey: this.route.snapshot.queryParams.key
        };
        this.requestService
            .post<{ token: string }>(`user/reset-password`, body)
            .subscribe(({token}) => {
                this.authService.storeToken(token);
                window.location.href = '/';
            });
    }
}
