import {forkJoin as observableForkJoin} from 'rxjs';
import {distinctUntilChanged} from 'rxjs/operators';

import {Component, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {Configuration, GameCreationDto, Group} from "../custom";
import {ConfigurationService} from "../_services/configuration.service";
import {Observable} from "rxjs/Observable";
import {StandingsOrderPipe} from "../_util/standings-order.pipe";
import {PlayoffsService} from "../_services/playoffs.service";
import {Router} from "@angular/router";

const toastr = require('toastr/toastr.js');

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
                       private standingsOrderPipe: StandingsOrderPipe, private playoffsService: PlayoffsService,
                       private router: Router) {
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

    autoDraw() {
        const usersByPlaceAsc = this.groups.reduce<number[][]>((usersByPlaceAsc: number[][], group: Group, groupIndex: number) => {
            const sortedUsers = this.standingsOrderPipe.transform(group.standings).map(s => s.user.id);
            for (let place = 1; place <= this.numberOfGroupMembersAdvancing; place++) {
                usersByPlaceAsc[place - 1][groupIndex] = sortedUsers[place - 1]
            }
            return usersByPlaceAsc;
        }, new Array(this.numberOfGroupMembersAdvancing).fill(null).map(() => new Array(this.groups.length)));

        this.games = this.playoffsService.randomDraw(usersByPlaceAsc);
    }

    public isDrawn(userId: number): boolean {
        return this.games
            .find(
                g => (g.awayUser != null && g.awayUser === userId)
                    || (g.homeUser != null && g.homeUser === userId)) != null;
    }

    public submit(): void {
        this.requestService.post<GameCreationDto[]>('game/many', this.games)
            .subscribe(() => {
                toastr.success("Successfully started playoffs.");
                this.router.navigateByUrl('/playoffs');
            });
    }
}
