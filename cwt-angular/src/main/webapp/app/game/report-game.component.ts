import {Component, OnInit} from '@angular/core';
import {AuthService} from "../_services/auth.service";
import {Configuration, JwtUser, ReportDto, User} from "../custom";
import {RequestService} from "../_services/request.service";

@Component({
    selector: 'cwt-report-game',
    template: require('./report-game.component.html')
})
export class ReportGameComponent implements OnInit {

    public remainingOpponents: User[];
    public report: ReportDto = <ReportDto> {};
    private authenticatedUser: JwtUser;
    private possibleScores: number[];

    public constructor(private authService: AuthService, private requestService: RequestService) {
    }

    public ngOnInit(): void {
        this.authenticatedUser = this.authService.getUserFromTokenPayload();
        this.report.user = this.authenticatedUser.id;

        this.requestService.get<User[]>(`user/${this.authenticatedUser.id}/group/remaining-opponents`)
            .subscribe(res => this.remainingOpponents = res);

        this.requestService.get<Configuration<number>>('configuration/score-best-of')
            .subscribe(res => {
                this.possibleScores = [];

                let i;
                for (i = 0; i < Math.ceil(res.value / 2); i++) {
                    this.possibleScores.push(i + 1);
                }
            });
    }

    public submit(): void {
        this.requestService.post('game', this.report)
            .subscribe();
    }
}
