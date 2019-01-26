import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {AuthService} from "../_services/auth.service";
import {Configuration, GameCreationDto, JwtUser, ReportDto, ServerError, User} from "../custom";
import {RequestService} from "../_services/request.service";
import {Router} from "@angular/router";

const toastr = require('toastr/toastr.js');

@Component({
    selector: 'cwt-report-game',
    template: require('./report-game.component.html')
})
export class ReportGameComponent implements OnInit {

    @ViewChild('replayFile') replayFile: ElementRef<HTMLInputElement>;

    public remainingOpponents: User[];
    public report: ReportDto = <ReportDto> {};
    private authenticatedUser: JwtUser;
    private possibleScores: number[];

    public constructor(private authService: AuthService, private requestService: RequestService, private router: Router) {
    }

    public ngOnInit(): void {
        this.authenticatedUser = this.authService.getUserFromTokenPayload();
        this.report.user = this.authenticatedUser.id;

        this.requestService.get<User[]>(`user/${this.authenticatedUser.id}/group/remaining-opponents`)
            .subscribe(res => this.remainingOpponents = res);

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
        const formData = new FormData();
        formData.append('replay', this.replayFile.nativeElement.files[0]);
        formData.append('score-home', this.report.scoreOfUser.toString());
        formData.append('score-away', this.report.scoreOfOpponent.toString());
        formData.append('away-user', this.report.opponent.toString());
        formData.append('home-user', this.report.user.toString());

        this.requestService.formDataPost('game', formData)
            .subscribe(
                (res: GameCreationDto) => {
                    this.router.navigateByUrl(`/game/${res.id}`);
                    toastr.success("Successfully saved.");
                },
                (err: ServerError) => {
                    toastr.error(err.error.message);
                });
    }
}
