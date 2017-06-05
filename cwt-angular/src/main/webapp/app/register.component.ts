import {Component} from "@angular/core";
import {User} from "./user";

@Component({
    selector: 'cwt-register',
    template: require('./register.component.html')
})
export class RegisterComponent {
    user = new User(18, 'Zemke', 'flzemke@gmail.com', 'testpw');

    submitted = false;

    onSubmit() { this.submitted = true; }

    // TODO: Remove this when we're done
    get diagnostic() { return this.user; }

}
