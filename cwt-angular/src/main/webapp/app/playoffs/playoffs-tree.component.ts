import {Component, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {Game} from "../custom";

@Component({
    selector: 'cwt-playoffs-tree',
    template: require('./playoffs-tree.component.html')
})
export class PlayoffsTreeComponent implements OnInit {

    public games: Game[];
    private numberOfRounds: number;

    public constructor(private requestService: RequestService) {
    }

    /**
     * @todo Calling this from the template causes lots of DOM updates.
     */
    public get numberOfRoundsIterable(): number[] {
        const numberOfRoundsIterable: number[] = [];

        let i;
        for (i = 0; i < this.numberOfRounds; i++) {
            numberOfRoundsIterable.push(i);
        }

        return numberOfRoundsIterable;
    }

    public ngOnInit(): void {
        this.requestService.get<Game[]>('tournament/current/game/playoff')
            .subscribe(res => {
                this.games = res;

                this.numberOfRounds = Math.log2(this.games
                    .filter(g => g.playoff.round === 1)
                    .length) + 1;
            });

        // TODO Order games so that they make sense in a tree.
    }

    public gamesInRound(round: number): Game[] {
        const gamesOfRound: Game[] = this.games
            .filter(g => g.playoff.round === round);

        if (round === this.numberOfRounds) {
            gamesOfRound.push(...this.gamesInRound(round + 1));
            gamesOfRound.reverse();
        }

        return gamesOfRound;
    }
}
