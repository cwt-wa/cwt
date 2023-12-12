import {Component} from "@angular/core";
import {RequestService} from "../_services/request.service";
import {Toastr} from "../_services/toastr";
import {Router} from "@angular/router";
import {finalize} from "rxjs/operators";

@Component({
    selector: 'cwt-password-forgotten',
    template: require('./password-forgotten.component.html')
})
export class PasswordForgottenComponent {

    email: string;
    loading: boolean = false;

    constructor(private requestService: RequestService,
                private toastr: Toastr,
                private router: Router) {
    }

    onSubmit() {
        this.loading = true;

        this.requestService
            .post('user/password-forgotten', {email: this.email})
            .pipe(finalize(() => this.loading = false))
            .subscribe(() => {
                this.toastr.success('You will receive an email shortly');
                this.router.navigateByUrl('/');
            });
    }
}
