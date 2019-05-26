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
                const numberOfRounds = Math.log2(gamesInFirstRound) + 1; // 4

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
                        const freeSpotsInRound = new Array(expectedNumberOfGamesInRound)
                            .fill(null)
                            .map((_value, idx) => idx + 1)
                            .filter(s => existingGamesInRound.findIndex(g => g.playoff.spot === s) === -1);

                        const existingAndUpcomingGamesInRound: GameDetailDto[] = [...existingGamesInRound]
                            .concat(new Array(expectedNumberOfGamesInRound - existingGamesInRound.length)
                                .fill(null)
                                .map(() => ({
                                    playoff: {
                                        round: round,
                                        spot: freeSpotsInRound.pop()
                                    }
                                } as GameDetailDto)));

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
}
