import {Component, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {Configuration, GameDto, Group, User} from "../custom";
import {ConfigurationService} from "../_services/configuration.service";
import {Observable} from "rxjs/Observable";

@Component({
    selector: 'cwt-admin-playoffs-start',
    template: require('./admin-playoffs-start.component.html')
})
export class AdminPlayoffsStartComponent implements OnInit {

    public groups: Group[];
    public numberOfGroupMembersAdvancing: number;
    public games: GameDto[];
    private typeAheadForGroupMember: (text$: Observable<string>) => Observable<any>;
    private typeAheadInputFormatter: (value: User) => string;
    private typeAheadResultFormatter: (value: User) => string;

    public constructor(private requestService: RequestService, private configurationService: ConfigurationService) {
        this.typeAheadForGroupMember = (text$: Observable<string>) =>
            text$
                .distinctUntilChanged()
                .map(term => term);
        this.typeAheadInputFormatter = (value: User) => value.username;
        this.typeAheadResultFormatter = (value: User) => value.username;
    }

    public get firstBranchGames(): GameDto[] {
        return this.games.filter((value, index) => index < this.games.length / 2);
    }

    public get secondBranchGames(): GameDto[] {
        return this.games.filter((value, index) => index >= this.games.length / 2);
    }

    public ngOnInit(): void {
        Observable.forkJoin(
            this.requestService.get<Group[]>('tournament/current/group'),
            this.configurationService.requestByKeys<number>(["NUMBER_OF_GROUP_MEMBERS_ADVANCING"])
            )
            .subscribe((res: [Group[], Configuration<number>[]]) => {
                    this.groups = res[0];
                    this.numberOfGroupMembersAdvancing = res[1][0].value;

                    const numberOfPlayoffSpots: number = this.numberOfGroupMembersAdvancing * this.groups.length;
                    this.games = [];

                    let i;
                    for (i = 0; i < numberOfPlayoffSpots; i++) {
                        this.games.push(<GameDto> {
                            playoff: {
                                spot: i + 1,
                                round: 1
                            }
                        });
                    }
                }
            );
    }
}
