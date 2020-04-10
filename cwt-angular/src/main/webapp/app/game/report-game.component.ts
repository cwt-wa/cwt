import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {AuthService} from "../_services/auth.service";
import {Configuration, GameCreationDto, JwtUser, ReportDto, User} from "../custom";
import {RequestService} from "../_services/request.service";
import {Router} from "@angular/router";
import {CanReportService} from "../_services/can-report.service";
import {Toastr} from "../_services/toastr";

@Component({
    selector: 'cwt-report-game',
    template: require('./report-game.component.html')
})
export class ReportGameComponent implements OnInit {

    @ViewChild('replayFile') replayFile: ElementRef<HTMLInputElement>;

    public remainingOpponents: User[];
    public report: ReportDto = <ReportDto>{};
    private authenticatedUser: JwtUser;
    private possibleScores: number[];

    public constructor(private authService: AuthService, private requestService: RequestService, private router: Router,
                       private canReportService: CanReportService, private toastr: Toastr) {
    }

    public ngOnInit(): void {
        this.authenticatedUser = this.authService.getUserFromTokenPayload();
        this.report.user = this.authenticatedUser.id;

        this.requestService.get<User[]>(`user/${this.authenticatedUser.id}/remaining-opponents`)
            .subscribe(res => {
                this.remainingOpponents = res;

                if (this.remainingOpponents.length === 1) {
                    this.report.opponent = this.remainingOpponents[0].id;
                }
            });

        this.requestService.get<Configuration>('configuration/score-best-of', {'user-id': this.authenticatedUser.id.toString()})
            .subscribe(res => {
                this.possibleScores = [];

                let i;
                for (i = 0; i <= Math.ceil(parseInt(res.value) / 2); i++) {
                    this.possibleScores.push(i);
                }
            });
    }

    public submit(): void {
        const payload: ReportDto = {
            scoreOfUser: this.report.scoreOfUser,
            scoreOfOpponent: this.report.scoreOfOpponent,
            opponent: this.report.opponent,
            user: this.report.user,
        };

        this.requestService.post('game', payload).subscribe((res: GameCreationDto) => {
            this.router.navigateByUrl(`/games/${res.id}`);
            this.toastr.success("Successfully saved.");
            this.canReportService.canReport.next(this.remainingOpponents.length - 1 > 0);

            const formData = new FormData();
            formData.append('replay', this.replayFile.nativeElement.files[0]);
            formData.append('away-user', payload.opponent.toString());
            formData.append('home-user', payload.user.toString());

            this.requestService.formDataPost(`binary/game/${res.id}/replay`, formData)
                .subscribe({
                    error: () => {
                        this.toastr.error("The replay file could not be uploaded.");
                    }
                });
        });
    }
}
