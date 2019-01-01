import {PlayoffsService} from "../../../../main/webapp/app/_services/playoffs.service";
import {GameCreationDto} from "../../../../main/webapp/app/custom";
import {Utils} from "../../../../main/webapp/app/_util/utils";

describe('PlayoffsService', () => {
    it('can draw randomly under constraints.', () => {
        const service = new PlayoffsService(new Utils());

        function allUsersWereDrawn(draw: GameCreationDto[], usersByPlace: number[][]) {
            const actuallyDrawnUsers: number[] = draw
                .reduce<number[]>((previousValue: number[], currentValue: GameCreationDto) => {
                    previousValue.push(currentValue.homeUser);
                    previousValue.push(currentValue.awayUser);
                    return previousValue;
                }, [] as number[])
                .sort();
            const usersExpectedToBeDrawn = (JSON.parse(JSON.stringify(usersByPlace)) as number[][])
                .reduce((previousValue: number[], currentValue: number[]) => {
                    previousValue.push(...currentValue);
                    return previousValue;
                })
                .sort();
            expect(actuallyDrawnUsers).toEqual(usersExpectedToBeDrawn);
        }

        function drawnByPlace(draw: GameCreationDto[], usersByPlace: number[][]) {
            function findPlaceOfUser(userId: number): number {
                let result;
                usersByPlace.forEach((userArr, index) => {
                    if (userArr.indexOf(userId) !== -1) {
                        result = index + 1;
                        return;
                    }
                });

                return result;
            }

            expect(draw.length)
                .toEqual(usersByPlace.reduce((prev, curr) => prev + curr.length, 0) / 2);

            draw.forEach(game => {
                expect(findPlaceOfUser(game.homeUser) + findPlaceOfUser(game.awayUser) === usersByPlace.length + 1)
                    .toBeTruthy();
            })
        }

        function perform(usersByPlaceAsc: any) {
            const randomDraw = service.randomDraw(usersByPlaceAsc);
            console.log(randomDraw.map(g => `${g.homeUser}vs${g.awayUser}`).join(", "));

            allUsersWereDrawn(randomDraw, usersByPlaceAsc);
            drawnByPlace(randomDraw, usersByPlaceAsc);
            randomDraw
                .sort((a, b) => a.playoff.spot - b.playoff.spot)
                .forEach((g, idx) => {
                    expect(g.playoff.round).toEqual(0);
                    expect(g.playoff.spot).toEqual(idx + 1);
                })
        }

        perform([[1, 2, 3, 4, 5, 6, 7, 8], [9, 10, 11, 12, 13, 14, 15, 16]]);
        perform([[1, 2, 3, 4], [9, 10, 11, 12]]);
        perform([[1, 2, 3, 4], [5, 6, 7, 8], [9, 10, 11, 12], [13, 14, 15, 16]]);
        perform([[1, 2, 3, 4], [5, 6, 7, 8], [9, 10, 11, 12]]);
    });
});
