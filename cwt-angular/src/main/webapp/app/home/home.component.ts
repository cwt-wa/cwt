import {Component, OnInit} from "@angular/core";
import {AuthService} from "../_services/auth.service";
import {JwtUser} from "../custom";

@Component({
    selector: 'cwt-home',
    template: require('./home.component.html')
})
export class HomeComponent implements OnInit {
    private authenticatedUser: JwtUser;

    constructor(private authService: AuthService) {
    }

    public ngOnInit(): void {
        this.authenticatedUser = this.authService.getUserFromTokenPayload();
    }

}
