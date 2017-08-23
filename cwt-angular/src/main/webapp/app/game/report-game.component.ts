import {Component, OnInit} from '@angular/core';
import {AuthService} from "../_services/auth.service";
import {JwtUser} from "../custom";

@Component({
    selector: 'cwt-report-game',
    template: require('./report-game.component.html')
})
export class ReportGameComponent implements OnInit {

    private authenticatedUser: JwtUser;

    public constructor(private authService: AuthService) {
    }

    public ngOnInit(): void {
        this.authenticatedUser = this.authService.getUserFromTokenPayload();
    }
}
