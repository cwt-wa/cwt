import {Injectable} from "@angular/core";
import {GameCreationDto} from "../custom";
import {Utils} from "../_util/utils";

@Injectable()
export class PlayoffsService {

    constructor(private utils: Utils) {
    }

    public randomDraw(usersByPlaceAsc: number[][]): GameCreationDto[] {
        return (JSON.parse(JSON.stringify(usersByPlaceAsc)) as number[][])
            .reduce<GameCreationDto[]>((games, users, idx, allUsers) => {
                let indexOfOpponents = usersByPlaceAsc.length - 1 - idx;

                let homeUsers: number[];
                let awayUsers: number[];
                if (indexOfOpponents === idx) {
                    homeUsers = users.slice(0, users.length / 2);
                    awayUsers = users.slice(users.length / 2);
                } else {
                    homeUsers = users;
                    awayUsers = allUsers[indexOfOpponents];
                }


                games.push(...this.utils.shuffleArray(homeUsers).map(u => {
                    let rndOpponent = awayUsers[Math.ceil(Math.random() * awayUsers.length) - 1];
                    awayUsers.splice(awayUsers.indexOf(rndOpponent), 1);
                    return ({
                        homeUser: u,
                        awayUser: rndOpponent,
                    } as GameCreationDto);
                }));
                allUsers.splice(-1);
                return games;
            }, [] as GameCreationDto[])
            .map((g, idx) => {
                g.playoff = {
                    round: 0,
                    spot: idx + 1
                };
                return g;
            });
    }
}
