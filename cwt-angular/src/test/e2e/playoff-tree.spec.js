const fs = require('fs');
const httpMock = require('@zemke/http-mock')(9000);

const baseData = {
    "reporter": {
        "username": "Zemke"
    },
    "techWin": false,
    "ratings": [],
    "comments": [],
    "commentsSize": 0,
    "ratingsSize": 0,
    "voided": false
};

function instantiateGame(spot) {
    return {
        "scoreHome": 2,
        "scoreAway": 0,
        "playoff": {
            "round": 1,
            "spot": spot
        },
        "homeUser": {
            "username": `HomeR1S${spot}`
        },
        "awayUser": {
            "username": `AwayR1S${spot}`
        }
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

function writeScreenShot(data, filename) {
    var stream = fs.createWriteStream(filename);
    stream.write(new Buffer(data, 'base64'));
    stream.end();
}

describe('Playoff tree', function () {

    const fn = function (players) {
        httpMock.add('/api/tournament/current/game/playoff', JSON.stringify(createTree(players)));
        browser.get('http://localhost:4300/playoffs');
        browser.takeScreenshot().then(data => fs.writeFile(`${__dirname}/screenshots/${players}.png`, data, 'base64', console.error));
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
