import {Injectable} from "@angular/core";
import {PlayoffTreeBetDto} from "../custom";
import {AuthService} from "./auth.service";

export type BetResult = {
    total: number;
    awayUser: PlayoffTreeBetDto[];
    homeUser: PlayoffTreeBetDto[];
    awayUserPercent: number;
    homeUserPercent: number;
    userBet: PlayoffTreeBetDto | null
}

@Injectable()
export class BetService {

    constructor(private authService: AuthService) {
    }

    createBetResult(bets: PlayoffTreeBetDto[]): BetResult {
        const authUser = this.authService.getUserFromTokenPayload();
        const preliminaryBetResult = {
            total: bets.length,
            homeUser: bets.filter(b => b.betOnHome),
            awayUser: bets.filter(b => !b.betOnHome),
            userBet: authUser ? (bets.find(b => b.user.id === authUser.id) || null) : null
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
