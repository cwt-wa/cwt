import {Component} from "@angular/core";
import {RequestService} from "./_services/request.service";
import {UserRegistration} from "./user-registration";

@Component({
    selector: 'cwt-register',
    template: require('./register.component.html')
})
export class RegisterComponent {
    userRegistration: UserRegistration = new UserRegistration('', '', '');

    submitted = false;

    constructor(private requestService: RequestService) {
    }

    onSubmit() {
        // http://codeseven.github.io/toastr/demo.html
        this.requestService.post('auth/register', this.userRegistration)
            .subscribe();
    }

}
