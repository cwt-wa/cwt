import {Injectable} from "@angular/core";
import {JwtUser, PlayoffTreeBetDto} from "../custom";

export type BetResult = {
    total: number;
    awayUser: PlayoffTreeBetDto[];
    homeUser: PlayoffTreeBetDto[];
    awayUserPercent: number;
    homeUserPercent: number;
    userBet: PlayoffTreeBetDto | null;
    userBetOnHome: boolean;
    userBetOnAway: boolean;
}

@Injectable()
export class BetService {

    createBetResult(bets: PlayoffTreeBetDto[], authUser: JwtUser): BetResult {
        const preliminaryBetResult = {
            total: bets.length,
            homeUser: bets.filter(b => b.betOnHome),
            awayUser: bets.filter(b => !b.betOnHome),
            userBet: authUser ? (bets.find(b => b.user.id === authUser.id) || null) : null,
            userBetOnHome: authUser ? (bets.find(b => b.user.id === authUser.id && b.betOnHome) || false) : false,
            userBetOnAway: authUser ? (bets.find(b => b.user.id === authUser.id && !b.betOnHome) || false) : false,
        } as BetResult;

        if (preliminaryBetResult.total === 0) {
            preliminaryBetResult.homeUserPercent = 50;
            preliminaryBetResult.awayUserPercent = 50;
        } else {
            preliminaryBetResult.homeUserPercent = preliminaryBetResult.homeUser.length / preliminaryBetResult.total * 100;
            preliminaryBetResult.awayUserPercent = preliminaryBetResult.awayUser.length / preliminaryBetResult.total * 100;
        }

        return preliminaryBetResult;
    }
}
