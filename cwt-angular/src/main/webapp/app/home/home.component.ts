import {Component, OnInit} from "@angular/core";
import {AuthService} from "../_services/auth.service";
import {Configuration, JwtUser, TournamentDetailDto} from "../custom";
import {ConfigurationService} from "../_services/configuration.service";
import {CurrentTournamentService} from "../_services/current-tournament.service";

@Component({
    selector: 'cwt-home',
    template: require('./home.component.html')
})
export class HomeComponent implements OnInit {
    news: Configuration;
    authenticatedUser?: JwtUser;
    tournament?: TournamentDetailDto;

    constructor(private authService: AuthService,
                private configurationService: ConfigurationService,
                private currentTournamentService: CurrentTournamentService) {
    }

    public ngOnInit(): void {
        this.authService.authState
            .then(user => this.authenticatedUser = user);

        this.configurationService.requestByKeys("NEWS")
            .subscribe(res => this.news = res[0]);

        this.currentTournamentService.value.then(res => this.tournament = res);
    }
}
