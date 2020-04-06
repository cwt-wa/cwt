import {StandingsOrderPipe} from "../../../main/webapp/app/_util/standings-order.pipe";
import {GameMinimalDto, StandingDto} from "../../../main/webapp/app/custom";

const createUser = (username: string, id: number) => ({username, id});

describe('StandingsOrderPipe', () => {
    it('Sorts descendingly by points > game ratio > round ratio > head-to-head record.', () => {
        const pipe = new StandingsOrderPipe();

        const firstUserStandings = {
            user: createUser('Zemke', 1),
            points: 3,
            games: 2,
            gameRatio: 2,
            roundRatio: 2,
        } as StandingDto;
        const secondUserStandings = {...firstUserStandings, user: createUser('Khamski', 2)};
        const thirdUserStandings = {...firstUserStandings, user: createUser('Darío', 3), gameRatio: firstUserStandings.gameRatio + 1};
        const fourthUserStandings = {...firstUserStandings, user: createUser('Sascha', 4), points: 0};

        const games: GameMinimalDto[] = [{
            scoreHome: 2,
            scoreAway: 3,
            homeUser: firstUserStandings.user,
            awayUser: secondUserStandings.user,
        } as GameMinimalDto];

        expect(pipe.transform([firstUserStandings, secondUserStandings, thirdUserStandings, fourthUserStandings], games).map(s => s.user.id))
            .toEqual([thirdUserStandings.user.id, secondUserStandings.user.id, firstUserStandings.user.id, fourthUserStandings.user.id]);
    });
});
