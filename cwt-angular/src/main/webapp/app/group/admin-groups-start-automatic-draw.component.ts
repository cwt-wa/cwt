import {distinctUntilChanged, map} from 'rxjs/operators';
import {Component, Input, OnInit} from "@angular/core";
import {Application, Group, GroupDto, User} from "../custom";
import {Observable} from "rxjs";
import {RequestService} from "../_services/request.service";
import {Router} from "@angular/router";
import {Utils} from "../_util/utils";
import {Toastr} from "../_services/toastr";
import {ConfigurationService} from "../_services/configuration.service";

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
    usersPerGroup: number;
    numberOfGroups: number;

    typeAheadForGroupMember: (text$: Observable<string>) => Observable<User[]>;
    typeAheadInputFormatter: (value: User) => string;
    typeAheadResultFormatter: (value: User) => string;

    constructor(private requestService: RequestService,
                private router: Router, private utils: Utils, private toastr: Toastr,
                private configurationService: ConfigurationService) {
        this.typeAheadForGroupMember = (text$: Observable<string>) =>
            text$
                .pipe(distinctUntilChanged()).pipe(
                map(term =>
                    this.applications
                        .map(a => a.applicant)
                        .filter(u => !this.userIsDrawn(u))
                        .filter(a => a.username.toLowerCase().indexOf(term.toLowerCase()) !== -1)));
        this.typeAheadInputFormatter = (value: User) => value.username || null;
        this.typeAheadResultFormatter = (value: User) => value.username;
    }

    public submit(): void {
        const randomlyDrawnGroups: GroupDto[] = this.groups.map(g => ({label: g.label, users: []}));

        let i;
        for (i = 0; i < this.usersPerGroup; i++) {
            const indicesOfGroupsAlreadyDrawnAUserOfCurrentPot = [];

            let j;
            for (j = 0; j < this.numberOfGroups; j++) {
                const user = this.pots[i][j];

                let randomIndexOfGroup: number;
                do {
                    randomIndexOfGroup = Math.floor(Math.random() * this.numberOfGroups);
                } while (indicesOfGroupsAlreadyDrawnAUserOfCurrentPot.indexOf(randomIndexOfGroup) !== -1);

                randomlyDrawnGroups[randomIndexOfGroup].users.push(user.id);
                indicesOfGroupsAlreadyDrawnAUserOfCurrentPot.push(randomIndexOfGroup)
            }
        }

        randomlyDrawnGroups.forEach(x => this.utils.shuffleArray(x.users));

        this.requestService.post('tournament/current/group/many', randomlyDrawnGroups)
            .subscribe(
                () => {
                    this.router.navigateByUrl('/groups');
                    this.toastr.success("Successfully saved.");
                });
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
        this.configurationService.requestByKeys("NUMBER_OF_GROUPS", "USERS_PER_GROUP")
            .subscribe(res => {
                this.numberOfGroups = parseInt(res.find(c => c.key === "NUMBER_OF_GROUPS").value);
                this.usersPerGroup = parseInt(res.find(c => c.key === "USERS_PER_GROUP").value);;

                this.pots = [];

                let i;
                for (i = 0; i < this.usersPerGroup; i++) {
                    const newPot = [];

                    let j;
                    for (j = 0; j < this.numberOfGroups; j++) {
                        newPot.push(<User>{});
                    }

                    this.pots.push(newPot);
                }
            })
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
