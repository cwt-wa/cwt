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

                const numberOfRounds: number = Math.log2(res
                    .filter(g => g.playoff.round === 1)
                    .length) + 1;

                this.playoffGames = new Array<Game[]>(numberOfRounds)
                    .fill(null)
                    .map((value, index) => {
                        value;

                        const round: number = index + 1;
                        const expectedNumberOfGamesInRound = this.calcRequiredNumberOfGamesInRound(round);
                        const existingGamesInRound: Game[] = this.getExistingGamesInRound(round, res);

                        const existingAndUpcomingGamesInRound = new Array(expectedNumberOfGamesInRound - existingGamesInRound.length);
                        existingAndUpcomingGamesInRound.push(...existingGamesInRound);
                        existingAndUpcomingGamesInRound.fill({}, 0, (expectedNumberOfGamesInRound) - existingGamesInRound.length);

                        return existingAndUpcomingGamesInRound;
                    });

            });
    }

    /**
     * @todo This is hardcoded.
     */
    private calcRequiredNumberOfGamesInRound(round: number) {
        switch (round) {
            case 1:
                return 8;
            case 2:
                return 4;
            case 3:
                return 2;
            case 4:
                return 2;
        }

        throw Error(`The round ${round} has not been hardcoded.`);
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
