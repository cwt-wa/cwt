import {distinctUntilChanged, map} from 'rxjs/operators';
import {forkJoin as observableForkJoin, Observable} from 'rxjs';

import {Component, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {Configuration, GameCreationDto, GroupWithGamesDto} from "../custom";
import {ConfigurationService} from "../_services/configuration.service";
import {StandingsOrderPipe} from "../_util/standings-order.pipe";
import {PlayoffsService} from "../_services/playoffs.service";
import {Router} from "@angular/router";
import {Toastr} from "../_services/toastr";

@Component({
    selector: 'cwt-admin-playoffs-start',
    template: require('./admin-playoffs-start.component.html')
})
export class AdminPlayoffsStartComponent implements OnInit {

    public groups: GroupWithGamesDto[];
    public numberOfGroupMembersAdvancing: number;
    public games: GameCreationDto[];
    typeAheadForPlayoffsUser: (text$: Observable<string>) => Observable<number[]>;
    typeAheadInputFormatter: (userId: number) => string;
    typeAheadResultFormatter: (userId: number) => string;

    public constructor(private requestService: RequestService, private configurationService: ConfigurationService,
                       private standingsOrderPipe: StandingsOrderPipe, private playoffsService: PlayoffsService,
                       private router: Router, private toastr: Toastr) {
        this.typeAheadForPlayoffsUser = (text$: Observable<string>) =>
            text$
                .pipe(distinctUntilChanged()).pipe(
                map(term => this.getPlayoffUsers()
                    .filter(u => !this.games.find(g => g.homeUser === u.id || g.awayUser === u.id))
                    .filter(u => u.username.toLowerCase().indexOf(term.toLowerCase()) !== -1)
                    .map(u => u.id)));
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
            this.requestService.get<GroupWithGamesDto[]>('tournament/current/group'),
            this.configurationService.requestByKeys("NUMBER_OF_GROUP_MEMBERS_ADVANCING")
            )
            .subscribe((res: [GroupWithGamesDto[], Configuration[]]) => {
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
        const usersByPlaceAsc = this.groups.reduce<number[][]>((usersByPlaceAsc: number[][], group: GroupWithGamesDto, groupIndex: number) => {
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
        this.requestService.post<GameCreationDto[]>('current/playoffs/start', this.games)
            .subscribe(() => {
                this.toastr.success("Successfully started playoffs.");
                this.router.navigateByUrl('/playoffs');
            });
    }
}
