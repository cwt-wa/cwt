import {Component, OnInit} from "@angular/core";
import {User} from "./user";
import {UserService} from "./_services/user.service";

@Component({
    selector: 'cwt-register',
    template: require('./register.component.html')
})
export class RegisterComponent implements OnInit {
    user: User = new User(1, '', '', '');

    submitted = false;

    constructor(private userService: UserService) {
    }

    ngOnInit(): void {
        this.userService.getUsers()
            .subscribe(
                user => {
                    console.log(user);
                    return this.user = user;
                },
                err => console.log(err)
            );
    }

    onSubmit() { this.submitted = true; }

}
