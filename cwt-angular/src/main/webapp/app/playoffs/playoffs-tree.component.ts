import {Component, Input, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {BetDto, PlayoffGameDto} from "../custom";
import {AuthService} from "../_services/auth.service";
import {Toastr} from "../_services/toastr";
import {BetResult, BetService} from "../_services/bet.service";

interface PlayoffTreeBetDtoWithBetResults extends PlayoffGameDto {
    betResult?: BetResult
}

@Component({
    selector: 'cwt-playoffs-tree',
    template: require('./playoffs-tree.component.html')
})
export class PlayoffsTreeComponent implements OnInit {

    @Input()
    tournamentId: number;

    playoffGames: PlayoffTreeBetDtoWithBetResults[][];
    isThreeWayFinalTree: boolean;

    public constructor(private requestService: RequestService, private authService: AuthService,
                       private toastr: Toastr, private betService: BetService) {
    }

    public ngOnInit(): void {
        this.requestService.get<PlayoffGameDto[]>(`tournament/${this.tournamentId || 'current'}/game/playoff`)
            .subscribe(res => {
                const gamesInFirstRound = res
                    .filter(g => g.playoff.round === 1)
                    .length;
                const log2GamesInFirstRound = Math.log2(gamesInFirstRound);
                const numberOfRounds = Math.floor(log2GamesInFirstRound) + 1;
                this.isThreeWayFinalTree = log2GamesInFirstRound % 1 !== 0;

                this.playoffGames = new Array<PlayoffGameDto[]>(numberOfRounds)
                    .fill(null)
                    .map((_value, index) => {
                        const round = index + 1;
                        const expectedNumberOfGamesInRound = this.calcRequiredNumberOfGamesInRound(round, gamesInFirstRound);
                        const existingGamesInRound: PlayoffTreeBetDtoWithBetResults[] = this.getExistingGamesInRound(round, res)
                            .map<PlayoffTreeBetDtoWithBetResults>(g => {
                                (g as PlayoffTreeBetDtoWithBetResults).betResult = this.betService.createBetResult(g.bets);
                                return g as PlayoffTreeBetDtoWithBetResults;
                            });

                        if (numberOfRounds === round) {
                            return existingGamesInRound[0] != null
                                ? [this.getExistingGamesInRound(round + 1, res)[0], existingGamesInRound[0]]
                                : (this.isThreeWayFinalTree ? new Array(3).fill(<PlayoffGameDto>{}) : new Array(2).fill(<PlayoffGameDto>{}));
                        }

                        existingGamesInRound.reverse();
                        const freeSpotsInRound = new Array(expectedNumberOfGamesInRound)
                            .fill(null)
                            .map((_value, idx) => idx + 1)
                            .filter(s => existingGamesInRound.findIndex(g => g.playoff.spot === s) === -1);

                        return [...existingGamesInRound].concat(new Array(expectedNumberOfGamesInRound - existingGamesInRound.length)
                            .fill(null)
                            .map(() => ({
                                playoff: {
                                    round: round,
                                    spot: freeSpotsInRound.pop()
                                }
                            } as PlayoffTreeBetDtoWithBetResults)))
                            .sort((a, b) => a.playoff && b.playoff
                                ? (a.playoff.spot > b.playoff.spot ? 1 : -1)
                                : 0);
                    });
            });
    }

    public getExistingGamesInRound(round: number, games: PlayoffGameDto[]): PlayoffGameDto[] {
        return games.filter(g => g.playoff.round === round);
    }

    public placeBet(betOnHome: boolean, game: PlayoffTreeBetDtoWithBetResults) {
        const authUserId = this.authService.getUserFromTokenPayload().id;
        this.requestService.post<BetDto>(
            `game/${game.id}/bet`,
            {user: authUserId, game: game.id, betOnHome})
            .subscribe(res => {
                const idxOfPreviousBet = game.bets.findIndex(b => b.user.id === authUserId);
                if (idxOfPreviousBet === -1) {
                    game.bets.push(res);
                    this.toastr.success("Bet successfully placed.");
                } else {
                    game.bets[idxOfPreviousBet] = res;
                    this.toastr.success("Bet successfully updated.");
                }
                game.betResult = this.betService.createBetResult(game.bets);
            });
    }

    private calcRequiredNumberOfGamesInRound(round: number, gamesInFirstRound: number): number {
        return gamesInFirstRound * Math.pow(.5, (round - 1));
    }
}
