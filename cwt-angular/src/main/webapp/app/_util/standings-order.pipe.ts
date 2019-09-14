import {Pipe, PipeTransform} from '@angular/core';
import {GroupStanding, StandingDto} from "../custom";

/**
 * Order standings by points > game ratio > round ratio > head-to-head record.
 *
 * @todo Use <a href="https://angular.io/api/common/AsyncPipe">AsyncPipe</a> to do head-to-head record.
 */
@Pipe({
    name: 'cwtStandingsOrder'
})
export class StandingsOrderPipe implements PipeTransform {

    transform(standings: StandingDto[]): StandingDto[] {
        return standings.sort()
            .sort((a, b) => a.roundRatio > b.roundRatio ? -1 : +1)
            .sort((a, b) => a.gameRatio > b.gameRatio ? -1 : +1)
            .sort((a, b) => a.points > b.points ? -1 : +1);
    }
}
