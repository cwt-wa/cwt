import {Component, Input, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {JwtUser, PlayoffGameDto, PlayoffTreeBetDto} from "../custom";
import {AuthService} from "../_services/auth.service";
import {Toastr} from "../_services/toastr";
import {BetResult, BetService} from "../_services/bet.service";
import {finalize} from "rxjs/operators";
import {CurrentTournamentService} from "../_services/current-tournament.service";

interface PlayoffTreeBetDtoWithBetResults extends PlayoffGameDto {
    betResult?: BetResult
}

@Component({
    selector: 'cwt-playoffs-tree',
    template: require('./playoffs-tree.component.html')
})
export class PlayoffsTreeComponent implements OnInit {

    @Input() tournamentId: number;
    @Input() hideTitle: boolean;
    @Input() hideLoadingIndicator: boolean;

    playoffGames: PlayoffTreeBetDtoWithBetResults[][];
    isThreeWayFinalTree: boolean;
    placingBet: number[] = [];
    loading: boolean = true;
    authUser: JwtUser;

    public constructor(private requestService: RequestService, private authService: AuthService,
                       private toastr: Toastr, private betService: BetService,
                       private currentTournamentService: CurrentTournamentService) {
    }

    public async ngOnInit() {
        this.authUser = await this.authService.authState;
        try {
            this.tournamentId = this.tournamentId || (await this.currentTournamentService.value)?.id;
            if (this.tournamentId == null) {
                this.toastr.info("There is currently no tournament.");
            } else {
              const playoffGames =
                  await this.requestService.get<PlayoffGameDto[]>(`tournament/${this.tournamentId}/game/playoff`)
                      .toPromise()
              this.createTree(playoffGames);
            }
        } catch (err) {
            this.toastr.error("The tournament could not be fetched.");
        } finally {
            this.loading = false;
        }
    }

    private createTree(res: PlayoffGameDto[]) {
        if (!res || !res.length) return;

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
                        (g as PlayoffTreeBetDtoWithBetResults).betResult = this.betService.createBetResult(g.bets, this.authUser);
                        return g as PlayoffTreeBetDtoWithBetResults;
                    });

                if (numberOfRounds === round) {
                    const finalGames = existingGamesInRound[0] != null
                        ? [this.getExistingGamesInRound(round + 1, res)[0], existingGamesInRound[0]]
                        : (this.isThreeWayFinalTree ? new Array(3).fill(<PlayoffGameDto>{}) : new Array(2).fill(<PlayoffGameDto>{}));

                    finalGames.map<PlayoffTreeBetDtoWithBetResults>(g => {
                        if (g.betResult == null) {
                            (g as PlayoffTreeBetDtoWithBetResults).betResult = this.betService.createBetResult(g.bets || [], this.authUser);
                        }
                        return g as PlayoffTreeBetDtoWithBetResults;
                    });

                    return finalGames;
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
    }

    public getExistingGamesInRound(round: number, games: PlayoffGameDto[]): PlayoffGameDto[] {
        return games.filter(g => g.playoff.round === round);
    }

    public highlightUser(id: number, enter: boolean) {
        document.querySelectorAll('.playoff-user').forEach(elem =>
            elem.classList.toggle('highlight', enter && parseInt(elem.attributes.getNamedItem("data-user").value) === id));
    }

    public async placeBet(betOnHome: boolean, game: PlayoffTreeBetDtoWithBetResults) {
        this.placingBet.push(game.id);
        this.requestService.post<PlayoffTreeBetDto>(
            `game/${game.id}/bet`,
            {user: this.authUser.id, game: game.id, betOnHome})
            .pipe(finalize(() => this.placingBet.splice(this.placingBet.findIndex(gId => gId === game.id), 1)))
            .subscribe(res => {
                const idxOfPreviousBet = game.bets.findIndex(b => b.user.id === this.authUser.id);
                if (idxOfPreviousBet === -1) {
                    game.bets.push(res);
                    this.toastr.success("Bet successfully placed.");
                } else {
                    game.bets[idxOfPreviousBet] = res;
                    this.toastr.success("Bet successfully updated.");
                }
                game.betResult = this.betService.createBetResult(game.bets, this.authUser);
            });
    }

    private calcRequiredNumberOfGamesInRound(round: number, gamesInFirstRound: number): number {
        return gamesInFirstRound * Math.pow(.5, (round - 1));
    }
}
