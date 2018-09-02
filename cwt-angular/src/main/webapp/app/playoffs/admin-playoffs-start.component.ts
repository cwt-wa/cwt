
import {forkJoin as observableForkJoin} from 'rxjs';
import { distinctUntilChanged } from 'rxjs/operators';

import {Component, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {Configuration, Game, GameDto, Group, User} from "../custom";
import {ConfigurationService} from "../_services/configuration.service";
import {Observable} from "rxjs/Observable";
import {StandingsOrderPipe} from "../_util/standings-order.pipe";

@Component({
    selector: 'cwt-admin-playoffs-start',
    template: require('./admin-playoffs-start.component.html')
})
export class AdminPlayoffsStartComponent implements OnInit {

    public groups: Group[];
    public numberOfGroupMembersAdvancing: number;
    public games: Game[];
    typeAheadForPlayoffsUser: (text$: Observable<string>) => Observable<User[]>;
    typeAheadInputFormatter: (value: User) => string;
    typeAheadResultFormatter: (value: User) => string;

    public constructor(private requestService: RequestService, private configurationService: ConfigurationService,
                       private standingsOrderPipe: StandingsOrderPipe) {
        this.typeAheadForPlayoffsUser = (text$: Observable<string>) =>
            text$
                .pipe(distinctUntilChanged())
                .map(term => this.groups
                    .map(g => this.standingsOrderPipe.transform(g.standings).slice(0, this.numberOfGroupMembersAdvancing))
                    .reduce((previousStanding, currentStanding) => previousStanding.concat(currentStanding))
                    .map(s => s.user)
                    .filter(u => !this.games
                        .find(g => (g.homeUser && g.homeUser.id) === u.id || (g.awayUser && g.awayUser.id === u.id)))
                    .filter(u => u.username.toLowerCase().indexOf(term.toLowerCase()) !== -1));
        this.typeAheadInputFormatter = (value: User) => value.username;
        this.typeAheadResultFormatter = (value: User) => value.username;
    }

    public get firstBranchGames(): Game[] {
        return this.games.filter((value, index) => value && index < this.games.length / 2);
    }

    public get secondBranchGames(): Game[] {
        return this.games.filter((value, index) => value && index >= this.games.length / 2);
    }

    public get numberOfGroupMembersAdvancingIterable(): number[] {
        const numberOfGroupMembersAdvancingIterable: number[] = [];

        let i;
        for (i = 0; i < this.numberOfGroupMembersAdvancing; i++) {
            numberOfGroupMembersAdvancingIterable.push(i);
        }

        return numberOfGroupMembersAdvancingIterable;
    }

    public ngOnInit(): void {
        observableForkJoin(
            this.requestService.get<Group[]>('tournament/current/group'),
            this.configurationService.requestByKeys<number>("NUMBER_OF_GROUP_MEMBERS_ADVANCING")
            )
            .subscribe((res: [Group[], Configuration<number>[]]) => {
                    this.groups = res[0];
                    this.numberOfGroupMembersAdvancing = res[1][0].value;

                    const numberOfPlayoffSpots: number = this.numberOfGroupMembersAdvancing * this.groups.length / 2;
                    this.games = [];

                    let i;
                    for (i = 0; i < numberOfPlayoffSpots; i++) {
                        this.games.push(<Game> {
                            playoff: {
                                spot: i + 1,
                                round: 1
                            }
                        });
                    }
                }
            );
    }

    public submit(): void {
        const body: GameDto[] = this.games
            .map(g => <GameDto> {
                homeUser: g.homeUser.id,
                awayUser: g.awayUser.id,
                playoff: g.playoff
            });

        this.requestService.post('game/many', body)
            .subscribe();
    }
}
