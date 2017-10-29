import {Component, OnInit} from "@angular/core";
import {AuthService} from "../_services/auth.service";
import {Configuration, JwtUser, Message} from "../custom";
import {ConfigurationService} from "../_services/configuration.service";
import {RequestService} from "../_services/request.service";

@Component({
    selector: 'cwt-home',
    template: require('./home.component.html')
})
export class HomeComponent implements OnInit {
    news: Configuration<string>;
    messages: Message[];
    private authenticatedUser: JwtUser;

    constructor(private authService: AuthService, private configurationService: ConfigurationService,
                private requestService: RequestService) {
    }

    public ngOnInit(): void {
        this.authenticatedUser = this.authService.getUserFromTokenPayload();

        this.configurationService.requestByKeys<string>("NEWS")
            .subscribe(res => this.news = res[0]);

        this.requestService.get<Message[]>('message')
            .subscribe(res => this.messages = res);
    }

}
