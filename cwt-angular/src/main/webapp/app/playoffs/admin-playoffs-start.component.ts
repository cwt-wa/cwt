import {distinctUntilChanged, map} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {Component, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {GameCreationDto, GroupWithGamesDto, PlayoffGameDto} from "../custom";
import {StandingsOrderPipe} from "../_util/standings-order.pipe";
import {PlayoffsService} from "../_services/playoffs.service";
import {Router} from "@angular/router";
import {Toastr} from "../_services/toastr";
import {CurrentTournamentService} from "../_services/current-tournament.service";

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

    public constructor(private requestService: RequestService,
                       private standingsOrderPipe: StandingsOrderPipe,
                       private playoffsService: PlayoffsService,
                       private router: Router,
                       private toastr: Toastr,
                       private currentTournamentService: CurrentTournamentService) {
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
            .map(g => this.standingsOrderPipe.transform(g.standings, g.games).slice(0, this.numberOfGroupMembersAdvancing))
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

    public async ngOnInit(): Promise<void> {
        const currentTournament = await this.currentTournamentService.value;
        this.numberOfGroupMembersAdvancing = currentTournament.numOfGroupAdvancing;
        this.requestService.get<GroupWithGamesDto[]>(`tournament/${currentTournament.id}/group`)
            .subscribe(res => {
                this.groups = res;

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
            });
    }

    autoDraw() {
        const usersByPlaceAsc = this.groups.reduce<number[][]>((usersByPlaceAsc: number[][], group: GroupWithGamesDto, groupIndex: number) => {
            const sortedUsers = this.standingsOrderPipe.transform(group.standings, group.games).map(s => s.user.id);
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
        this.requestService.post<PlayoffGameDto[]>('tournament/current/playoffs/start', this.games)
            .subscribe(() => {
                this.toastr.success("Started playoffs");
                this.router.navigateByUrl('/playoffs');
            });
    }
}
