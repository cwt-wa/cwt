import {Component, OnInit} from "@angular/core";
import {AuthService} from "../_services/auth.service";
import {Configuration, JwtUser} from "../custom";
import {ConfigurationService} from "../_services/configuration.service";

@Component({
    selector: 'cwt-home',
    template: require('./home.component.html')
})
export class HomeComponent implements OnInit {
    news: Configuration<string>;
    authenticatedUser: JwtUser;

    constructor(private authService: AuthService, private configurationService: ConfigurationService) {
    }

    public ngOnInit(): void {
        this.authenticatedUser = this.authService.getUserFromTokenPayload();

        this.configurationService.requestByKeys<string>("NEWS")
            .subscribe(res => this.news = res[0]);
    }
}
