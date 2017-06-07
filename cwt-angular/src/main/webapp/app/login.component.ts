import {Component} from "@angular/core";
import {User} from "./user";

@Component({
    selector: 'cwt-login',
    template: require('./login.component.html')
})
export class LoginComponent {
    user = new User(1, '', '', '');

    onSubmit() {

    }
}
