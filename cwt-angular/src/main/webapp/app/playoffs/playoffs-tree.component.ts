import {Component, Input, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {GameDetailDto} from "../custom";

@Component({
    selector: 'cwt-playoffs-tree',
    template: require('./playoffs-tree.component.html')
})
export class PlayoffsTreeComponent implements OnInit {

    @Input()
    tournamentId: number;

    public playoffGames: GameDetailDto[][];

    public constructor(private requestService: RequestService) {
    }

    public ngOnInit(): void {
        this.requestService.get<GameDetailDto[]>(`tournament/${this.tournamentId || 'current'}/game/playoff`)
            .subscribe(res => {
                const gamesInFirstRound = res
                    .filter(g => g.playoff.round === 1)
                    .length;
                const numberOfRounds = Math.log2(gamesInFirstRound) + 1;

                this.playoffGames = new Array<GameDetailDto[]>(numberOfRounds)
                    .fill(null)
                    .map((_value, index) => {
                        const round = index + 1;
                        const expectedNumberOfGamesInRound = this.calcRequiredNumberOfGamesInRound(round, gamesInFirstRound);
                        const existingGamesInRound: GameDetailDto[] = this.getExistingGamesInRound(round, res);

                        if (numberOfRounds === round) {
                            return existingGamesInRound[0] != null
                                ? [this.getExistingGamesInRound(round + 1, res)[0], existingGamesInRound[0]]
                                : [<GameDetailDto> {}, <GameDetailDto> {}];
                        }

                        existingGamesInRound.reverse();

                        const existingAndUpcomingGamesInRound: GameDetailDto[] = new Array(...existingGamesInRound)
                            .fill(<GameDetailDto> {}, 0, (expectedNumberOfGamesInRound) - existingGamesInRound.length);

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

    public getExistingGamesInRound(round: number, games: GameDetailDto[]): GameDetailDto[] {
        return games
            .filter(g => g.playoff.round === round);
    }

    public get debug() {
        return this.playoffGames
            .map((a, i) => a
                .map(g => <any> {home: g.homeUser && g.homeUser.username, away: g.awayUser && g.awayUser.username, round: i}))
    }
}
