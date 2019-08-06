const httpMock = require('@zemke/http-mock')(9000);

describe('Game detail page', () => {

    it('shows betting standings', () => {
        const gameId = 1;
        httpMock.add(`/api/game/${gameId}`, {
            id: gameId,
            scoreHome: 3,
            scoreAway: 1,
            techWin: false,
            created: new Date(),
            modified: new Date(),
            playoff: {round: 2, spot: 1},
            group: null,
            tournament: {
                id: 1,
                created: new Date()
            },
            homeUser: {id: 1, username: 'Zemke'},
            awayUser: {id: 2, username: 'Rafka'},
            reporter: {id: 1, username: 'Zemke'},
            ratings: [],
            comments: [],
            isReplayExists: false,
            playoffRoundLocalized: 'Quarterfinal',
        });
        httpMock.add(`/api/game/${gameId}/bets`, [{
            id: 1,
            user: {id: 3, username: 'Alfred'},
            betOnHome: true
        }, {
            id: 2,
            user: {id: 4, username: 'Joe'},
            betOnHome: false
        }, {
            id: 3,
            user: {id: 5, username: 'M3dal'},
            betOnHome: true
        }]);

        browser.get(`http://localhost:4300/games/${gameId}`);
        browser.sleep(999999);
    });
});
