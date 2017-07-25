import {Component} from "@angular/core";
import {RequestService} from "../_services/request.service";
import {UserRegistration} from "./model/user-registration";
const toastr = require('toastr/toastr.js');

@Component({
    selector: 'cwt-register',
    template: require('./register.component.html')
})
export class RegisterComponent {
    userRegistration: UserRegistration = new UserRegistration('', '', '');

    submitted = false;

    constructor(private requestService: RequestService) {
    }

    submit() {
        this.requestService.post('auth/register', this.userRegistration)
            .subscribe(
                () => toastr.success('You have been registered successfully.'),
                () => toastr.error('Meh.')
            );
    }

}
