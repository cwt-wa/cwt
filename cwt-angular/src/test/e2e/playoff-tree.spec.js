const httpMock = require('@zemke/http-mock')(9000);
const screenshot = require('./screenshot');

const tokenPayload = {
    "id": 1,
    "username": "Zemke",
    "email": "florian@zemke.io",
    "roles": "USER",
    "enabled": true
};
const TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJjb250ZXh0Ijp7InVzZXIiOnsiaWQiOjEsInVzZXJuYW1lIjoiWmVta2UiLCJlbWFpbCI6ImZsb3JpYW5AemVta2UuaW8iLCJyb2xlcyI6IlVTRVIiLCJlbmFibGVkIjp0cnVlfX19.yF22wLYh8NUWL7HQE347uSg8-VO8rNY9FuOa36HqQhw";

const baseData = {
    "techWin": false,
    "ratings": [],
    "comments": [],
    "commentsSize": 0,
    "ratingsSize": 0,
    "voided": false
};

function rnd100() {
    return Math.floor(Math.random() * 100) + 1;
}

function instantiateGame(spot) {
    return {
        "id": rnd100(),
        "playoff": {
            "round": 1,
            "spot": spot
        },
        "homeUser": {
            "username": `HomeR1S${spot}`
        },
        "awayUser": {
            "username": `AwayR1S${spot}`
        },
        "bets": [{
            "id": rnd100(),
            "user": {id: rnd100(), username: "Zemke"},
            "betOnHome": false
        }, {
            "id": rnd100(),
            "user": {id: rnd100(), username: "Alfred"},
            "betOnHome": true
        }, {
            "id": rnd100(),
            "user": {id: rnd100(), username: "Bert"},
            "betOnHome": false
        }]
    };
}

function createTree(numberOfPlayersInFirstRound) {
    const games = [];

    let i;
    for (i = 0; i < numberOfPlayersInFirstRound / 2; i++) {
        games.push(Object.assign({}, baseData, instantiateGame(i + 1)));
    }

    return games;
}

describe('Playoff tree in different sizes', function () {

    httpMock.add('/api/tournament/current', {id: 1234});

    const fn = function (players) {
        httpMock.add('/api/tournament/current/game/playoff', createTree(players));
        browser.get('http://localhost:4300/playoffs');
        screenshot(players);
    };

    // One-way finals
    it('4', () => fn(4));
    it('8', () => fn(8));
    it('16', () => fn(16));
    it('32', () => fn(32));
    it('64', () => fn(64));
    it('128', () => fn(128));

    // Three-way finals
    it('6', () => fn(6));
    it('12', () => fn(12));
    it('24', () => fn(24));
    it('48', () => fn(48));
    it('96', () => fn(96));
});

describe('Playoff tree bets', function () {

    it('16', function () {
        const tree = createTree(16);
        httpMock.add('/api/tournament/current/game/playoff', tree);
        httpMock.add('/api/auth/refresh', {token: TOKEN});
        httpMock.add(/^\/api\/game\/\d+\/bet$/, (_, data) =>
            ({
                id: rnd100(),
                user: {
                    id: data.user,
                    username: tokenPayload.username
                },
                betOnHome: data.betOnHome,
            }));
        browser.get('http://localhost:4300/playoffs');
        browser.sleep(2147483647);
    });
});
