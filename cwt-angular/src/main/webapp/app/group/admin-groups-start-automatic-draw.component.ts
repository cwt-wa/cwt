import {Component, Inject, Input, OnInit} from "@angular/core";
import {Application, Group, GroupDto, User} from "../custom";
import {APP_CONFIG, AppConfig} from "../app.config";
import {Observable} from "rxjs/Observable";
import {distinctUntilChanged} from "rxjs/operators";
import {RequestService} from "../_services/request.service";
import {Router} from "@angular/router";
import {Utils} from "../_util/utils";

const toastr = require('toastr/toastr.js');

@Component({
    selector: 'cwt-admin-groups-start-automatic-draw',
    template: require('./admin-groups-start-automatic-draw.component.html')
})
export class AdminGroupsStartAutomaticDrawComponent implements OnInit {

    @Input()
    groups: Group[];

    @Input()
    applications: Application[];

    pots: User[][];
    usersPerGroup: 4;
    numberOfGroups: 8;

    typeAheadForGroupMember: (text$: Observable<string>) => Observable<User[]>;
    typeAheadInputFormatter: (value: User) => string;
    typeAheadResultFormatter: (value: User) => string;

    constructor(@Inject(APP_CONFIG) private appConfig: AppConfig, private requestService: RequestService,
                private router: Router) {
        this.numberOfGroups = this.appConfig.tournament.numberOfGroups;
        this.usersPerGroup = this.appConfig.tournament.usersPerGroup;

        this.typeAheadForGroupMember = (text$: Observable<string>) =>
            text$
                .pipe(distinctUntilChanged())
                .map(term =>
                    this.applications
                        .map(a => a.applicant)
                        .filter(u => !this.userIsDrawn(u))
                        .filter(a => a.username.toLowerCase().indexOf(term.toLowerCase()) !== -1));
        this.typeAheadInputFormatter = (value: User) => value.username || null;
        this.typeAheadResultFormatter = (value: User) => value.username;
    }

    public submit(): void {
        const randomlyDrawnGroups: GroupDto[] = this.groups.map(g => ({label: g.label, users: []}));

        let i;
        for (i = 0; i < this.appConfig.tournament.usersPerGroup; i++) {
            const indicesOfGroupsAlreadyDrawnAUserOfCurrentPot = [];

            let j;
            for (j = 0; j < this.appConfig.tournament.numberOfGroups; j++) {
                const user = this.pots[i][j];

                let randomIndexOfGroup: number;
                do {
                    randomIndexOfGroup = Math.floor(Math.random() * this.appConfig.tournament.numberOfGroups);
                } while (indicesOfGroupsAlreadyDrawnAUserOfCurrentPot.indexOf(randomIndexOfGroup) !== -1);

                randomlyDrawnGroups[randomIndexOfGroup].users.push(user.id);
                indicesOfGroupsAlreadyDrawnAUserOfCurrentPot.push(randomIndexOfGroup)
            }
        }

        randomlyDrawnGroups.forEach(x => Utils.shuffleArray(x.users));

        this.requestService.post('tournament/current/group/many', randomlyDrawnGroups)
            .subscribe(
                () => {
                    this.router.navigateByUrl('/applicants');
                    toastr.success("Successfully saved.");
                },
                () => toastr.error("An unknown error occurred."));
    }

    public get drawnApplicants(): User[] {
        return this.applications
            .map(a => a.applicant)
            .filter(u => this.userIsDrawn(u));
    }

    public get undrawnApplicants(): User[] {
        return this.applications
            .map(a => a.applicant)
            .filter(u => !this.userIsDrawn(u));
    }

    public ngOnInit(): void {
        this.pots = [];

        let i;
        for (i = 0; i < this.appConfig.tournament.usersPerGroup; i++) {
            const newPot = [];

            let j;
            for (j = 0; j < this.appConfig.tournament.numberOfGroups; j++) {
                newPot.push(<User>{});
            }

            this.pots.push(newPot);
        }
    }

    private userIsDrawn(user: User): boolean {
        if (user == null) {
            return false;
        }

        for (const pot of this.pots) {
            for (const drawnApplicant of pot) {
                if (drawnApplicant && drawnApplicant.id === user.id) {
                    return true;
                }
            }
        }

        return false;
    }
}
