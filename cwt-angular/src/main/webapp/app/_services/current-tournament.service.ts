import {Injectable} from "@angular/core";
import {RequestService} from "./request.service";
import {TournamentDetailDto} from "../custom";

@Injectable()
export class CurrentTournamentService {

    public readonly value: Promise<TournamentDetailDto | null>;

    constructor(private requestService: RequestService) {
        let currentTournamentResolver: (val?: TournamentDetailDto) => void;
        this.value = new Promise(resolve => currentTournamentResolver = resolve);
        this.requestService.get<TournamentDetailDto>('tournament/current')
            .subscribe(res => currentTournamentResolver(res));
    }
}
