import {Component, OnInit} from "@angular/core";
import {AuthService} from "../_services/auth.service";
import {Configuration, JwtUser, TournamentDetailDto} from "../custom";
import {ConfigurationService} from "../_services/configuration.service";
import {CurrentTournamentService} from "../_services/current-tournament.service";

@Component({
    styles: [`
        .hell-objects {
            position: absolute;
            bottom: -42px;
            z-index: -1;
            right: 40px;
        }

        .hell-objects .sickle {
            transform: scaleY(-1);
            height: 90px;
        }

        .hell-objects .monument {
            height: 60px;
        }

        .hell-objects .monument:first-child {
            transform: rotate(12deg);
        }

        .hell-objects img:last-child {
            transform: rotate(-6deg);
        }

        .hellground {
            position: absolute;
            bottom: -14px;
            white-space: nowrap;
            overflow: hidden;
            width: 100%;
            border-top-left-radius: 50px;
            border-top-right-radius: 50px;
            z-index: -1;
        }

        .ew {
            position: absolute;
            bottom: 10px;
            z-index: 10;
            right: 274px;
            transform: scaleX(-1) rotate(10deg);
        }

        .hellground img:nth-child(odd) {
            transform: scaleX(-1);
        }

        .hellceiling {
            position: absolute;
            bottom: -3px;
            white-space: nowrap;
            overflow: hidden;
            width: 100%;
            border-bottom-left-radius: 50px;
            border-bottom-right-radius: 50px;
            z-index: -1;
        }

        .news img {
          width: 100%;
        }
    `],
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
