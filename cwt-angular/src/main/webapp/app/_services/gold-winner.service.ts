import {Injectable} from "@angular/core";
import {RequestService} from "./request.service";
import {TournamentDetailDto, JwtUser} from "../custom";
import {AuthService} from "../_services/auth.service";

@Injectable()
export class GoldWinnerService {

    constructor(private authService: AuthService,
                private requestService: RequestService) {
    }

    async highlight(): Promise<boolean> {
        const currentTournamentPromise = this.requestService.get<TournamentDetailDto>('tournament/current').toPromise();
        const [authUser, currentTournament] = await Promise.all([this.authService.authState, currentTournamentPromise])
        return this.doHighlight(currentTournament, authUser);
    }

    doHighlight(currentTournament: TournamentDetailDto, authUser: JwtUser): boolean {
        const userIsGoldWinner =
            currentTournament
            && currentTournament.status === 'FINISHED'
            && currentTournament.goldWinner
            && currentTournament.goldWinner.id === authUser.id;
        if (userIsGoldWinner) {
            document.body.classList.add('goldWinner');
        }
        return userIsGoldWinner;
    }
}

