import {forkJoin as observableForkJoin} from 'rxjs';
import {distinctUntilChanged} from 'rxjs/operators';

import {Component, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {Configuration, GameCreationDto, Group} from "../custom";
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
    public games: GameCreationDto[];
    typeAheadForPlayoffsUser: (text$: Observable<string>) => Observable<number[]>;
    typeAheadInputFormatter: (userId: number) => string;
    typeAheadResultFormatter: (userId: number) => string;

    public constructor(private requestService: RequestService, private configurationService: ConfigurationService,
                       private standingsOrderPipe: StandingsOrderPipe) {
        this.typeAheadForPlayoffsUser = (text$: Observable<string>) =>
            text$
                .pipe(distinctUntilChanged())
                .map(term => this.getPlayoffUsers()
                    .filter(u => !this.games.find(g => g.homeUser === u.id || g.awayUser === u.id))
                    .filter(u => u.username.toLowerCase().indexOf(term.toLowerCase()) !== -1)
                    .map(u => u.id));
        this.typeAheadInputFormatter = (userId: number) => this.getPlayoffUsers().find(u => u.id === userId).username;
        this.typeAheadResultFormatter = (userId: number) => this.getPlayoffUsers().find(u => u.id === userId).username;
    }

    private getPlayoffUsers() {
        return this.groups
            .map(g => this.standingsOrderPipe.transform(g.standings).slice(0, this.numberOfGroupMembersAdvancing))
            .reduce((previousStanding, currentStanding) => previousStanding.concat(currentStanding))
            .map(s => s.user);
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
            this.configurationService.requestByKeys("NUMBER_OF_GROUP_MEMBERS_ADVANCING")
            )
            .subscribe((res: [Group[], Configuration[]]) => {
                    this.groups = res[0];
                    this.numberOfGroupMembersAdvancing = parseInt(res[1][0].value);

                    const numberOfPlayoffSpots: number = this.numberOfGroupMembersAdvancing * this.groups.length / 2;
                    this.games = [];

                    let i;
                    for (i = 0; i < numberOfPlayoffSpots; i++) {
                        this.games.push({
                            playoff: {
                                spot: i + 1,
                                round: 1
                            }
                        } as GameCreationDto);
                    }
                }
            );
    }

    public isDrawn(userId: number): boolean {
        return this.games
            .find(
                g => (g.awayUser != null && g.awayUser === userId)
                    || (g.homeUser != null && g.homeUser === userId)) != null;
    }

    public submit(): void {
        this.requestService.post<GameCreationDto[]>('game/many', this.games)
            .subscribe();
    }
}
