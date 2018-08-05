import {Component, Inject, Input, OnInit} from "@angular/core";
import {Application, Group, User} from "../custom";
import {APP_CONFIG, AppConfig} from "../app.config";
import {Observable} from "rxjs/Observable";

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

    constructor(@Inject(APP_CONFIG) private appConfig: AppConfig) {
        this.numberOfGroups = this.appConfig.tournament.numberOfGroups;
        this.usersPerGroup = this.appConfig.tournament.usersPerGroup;

        this.typeAheadForGroupMember = (text$: Observable<string>) =>
            text$
                .distinctUntilChanged()
                .map(term =>
                    this.applications
                        .map(a => a.applicant)
                        .filter(u => !this.userIsDrawn(u))
                        .filter(a => a.username.toLowerCase().indexOf(term.toLowerCase()) !== -1));
        this.typeAheadInputFormatter = (value: User) => value.username || null;
        this.typeAheadResultFormatter = (value: User) => value.username;
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
