import {Component, Input, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {Game} from "../custom";

@Component({
    selector: 'cwt-playoffs-tree',
    template: require('./playoffs-tree.component.html')
})
export class PlayoffsTreeComponent implements OnInit {

    @Input()
    tournamentId: number;

    public playoffGames: Game[][];

    public constructor(private requestService: RequestService) {
    }

    public ngOnInit(): void {
        this.requestService.get<Game[]>(`tournament/${this.tournamentId || 'current'}/game/playoff`)
            .subscribe(res => {
                const gamesInFirstRound = res
                    .filter(g => g.playoff.round === 1)
                    .length;
                const numberOfRounds = Math.log2(gamesInFirstRound) + 1;

                this.playoffGames = new Array<Game[]>(numberOfRounds)
                    .fill(null)
                    .map((_value, index) => {
                        const round = index + 1;
                        const expectedNumberOfGamesInRound = this.calcRequiredNumberOfGamesInRound(round, gamesInFirstRound);
                        const existingGamesInRound: Game[] = this.getExistingGamesInRound(round, res);

                        if (numberOfRounds === round) {
                            return existingGamesInRound[0] != null
                                ? [this.getExistingGamesInRound(round + 1, res)[0], existingGamesInRound[0]]
                                : [<Game> {}, <Game> {}];
                        }

                        existingGamesInRound.reverse();

                        const existingAndUpcomingGamesInRound: Game[] = new Array(...existingGamesInRound)
                            .fill(<Game> {}, 0, (expectedNumberOfGamesInRound) - existingGamesInRound.length);

                        existingAndUpcomingGamesInRound.sort((a, b) => a.playoff && b.playoff
                            ? (a.playoff.spot > b.playoff.spot ? 1 : -1)
                            : 0);

                        return existingAndUpcomingGamesInRound;
                    });

            });
    }

    private calcRequiredNumberOfGamesInRound(round: number, gamesInFirstRound: number): number {
        return gamesInFirstRound * Math.pow(.5, (round - 1));
    }

    public getExistingGamesInRound(round: number, games: Game[]): Game[] {
        return games
            .filter(g => g.playoff.round === round);
    }

    public get debug() {
        return this.playoffGames
            .map((a, i) => a
                .map(g => <any> {home: g.homeUser && g.homeUser.username, away: g.awayUser && g.awayUser.username, round: i}))
    }
}
