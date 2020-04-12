import {Component, Input, OnInit} from '@angular/core';

const colors: { [key: string]: string } = {
    blue: '#9D9FFF',
    red: '#FF7F7F',
    green: '#80FF80',
    yellow: '#FFFF80',
    cyan: '#80FFFF',
    magenta: '#FF82FF',
};

// noinspection CssUnusedSymbol
@Component({
    selector: 'cwt-game-stats',
    styles: [`
      .stats {
        border-radius: .25rem;
        overflow: hidden;
        padding: 0;
        margin: 0;
      }

      .stats .head {
        display: flex;
        justify-content: center;
        color: #fbfbfb;
        background-color: #1B2021;
        text-shadow: 0 0 .15rem #000;
        padding: .8rem 0;
        line-height: 1.1rem;
        margin: 0 0 1px 0;
        text-align: center;
      }

      .stats .head > div.user {
        flex-grow: 1;
      }

      .stats .head div.user > span.user {
        display: block;
        font-size: 1rem;
      }

      .stats .head div.user > span.team {
        display: block;
        font-size: .8rem;
      }

      .suddenDeath {
        display: flex;
        background-image: linear-gradient(to top, #323b7e 0, #323b7e 50%, #1B2021 50%, #1B2021 100%);
        padding: .8rem 0;
        margin-bottom: 1px;
        justify-content: stretch;
      }

      .suddenDeath img {
        flex-grow: 1;
        width: 100%;
      }

      .turn {
        display: flex;
        justify-content: space-between;
        flex-wrap: nowrap;
        padding: .5rem 0;
        background: transparent;
        margin: 0 0 1px 0;
        align-items: center;
        min-height: 4rem;
      }

      .turn.lossOfControl {
        border: .2rem dotted red;
      }

      .small.lossOfControl {
        border: .1rem dotted red;
      }

      .kills {
        width: 44px;
        padding: 0 3px;
        white-space: nowrap;
        flex-wrap: nowrap;
      }

      .turn .kills:first-child {
        text-align: left;
      }

      .turn .kills:last-child {
        text-align: right;
      }

      .turn .kills:first-child img:not(:first-child) {
        margin-left: -8px;
      }

      .turn .kills:last-child img:not(:last-child) {
        margin-right: -8px;
      }

      .turn .kills:last-child img {
        transform: scale(-1, 1);
      }

      .weapon {
        display: inline-block;
        background-clip: padding-box;
        margin: .3rem;
        padding: .2rem;
        box-shadow: 0 0 .25rem #000;
      }

      .blue { background-color: ${colors.blue};}
      .red { background-color: ${colors.red};}
      .green { background-color: ${colors.green};}
      .yellow { background-color: ${colors.yellow};}
      .cyan { background-color: ${colors.cyan};}
      .magenta { background-color: ${colors.magenta};}
`
    ],
    template: `
        <div class="stats" *ngIf="stats">
            <div class="head" [ngStyle]="{'background-image': linearGradientHealthPoints(0)}">
                <div class="user">
                    <span class="user">{{stats.teams[0].user}}</span>
                    <span class="team">{{stats.teams[0].team}}</span>
                </div>
                <div class="user">
                    <span class="user">{{stats.teams[1].user}}</span>
                    <span class="team">{{stats.teams[1].team}}</span>
                </div>
            </div>
            <div *ngFor="let turn of stats.turns; let index = index">
                <div class="suddenDeath" *ngIf="suddenDeathBeforeTurn === index + 1">
                    <img [src]="waterImage" alt="water"/>
                    <img [src]="waterImage" alt="water"/>
                </div>
                <div class="turn"
                     [ngStyle]="{'background-image': linearGradientHealthPoints(index + 1)}"
                     [ngClass]="{'lossOfControl': turn.lossOfControl}">
                    <div class="kills">
                        <img *ngFor="let kill of retrieveKills(index, stats.teams[0].user)" [src]="kill"/>
                    </div>
                    <div class="weapons">
                        <div *ngFor="let weapon of turn.weapons"
                             class="weapon {{getColorOfUser(turn.user).toLowerCase()}}">
                            <cwt-weapon [weapon]="weapon"></cwt-weapon>
                        </div>
                    </div>
                    <div class="kills">
                        <img *ngFor="let kill of retrieveKills(index, stats.teams[1].user)" [src]="kill"/>
                    </div>
                </div>
            </div>
            <div class="head" [ngStyle]="{'background-image': linearGradientHealthPoints(0)}">
                <div class="user" *ngFor="let averageTurnTime of averageTurnTimes;">
                    &empty; {{averageTurnTime}}s
                </div>
            </div>
            <div class="head">
                {{stats.roundTime.split(':')[1]}}m
                {{stats.roundTime.split(':')[2]}}s
            </div>
        </div>
        <ng-template #statsDescription>
            <p>
                The bars in the background are approximations of health points. They do not include health reductions to due
                self-killing weapons like Kamikaze or continuous health reductions due to Sudden Death or Skunks or the like.<br>
                They’re actually more like accumulations of damage conceded.
            </p>
            <p>
                Other than that the bars in the background depict the state after that turn whose used weapons are shown on top
                with a border of the color of the team that used the weapon.
            </p>
            <p>
                Dashed red border means that the turn has ended due to the Worm losing control. Like he or she
                slipped or fell off the cliff or the like. Poor worm.
            </p>
            <p>
                Weapons like Ninja Rope are shown to be used even if the usage didn’t reduce ammo.
                Like when shooting a Ninja Rope into empty space.
            </p>
            <p *ngIf="this.suddenDeathBeforeTurn !== -1">
                Yes, that wave is Sudden Death.
            </p>
            <p>
                Powered by <a href="https://waaas.zemke.io/" target="_blank" class="font-weight-bolder">WAaaS</a>
            </p>
        </ng-template>
        <div class="text-right mt-1">
            <i class="fa cursor-pointer fa-question d-sm-none"
               [ngbPopover]="statsDescription" placement="left-bottom"></i>
            <i class="fa cursor-pointer fa-question d-none d-sm-block"
               [ngbPopover]="statsDescription" placement="right-bottom"></i>
        </div>
    `
})
export class GameStatsComponent implements OnInit {

    @Input() stats: GameStats.GameStats;
    totalHealthPointsPerTeam: number;
    numberOfTeams: number;
    losingUser: string;
    winningUser: string;
    killImage: string = require('../../img/grave.png');
    waterImage: string = require('../../img/water.gif');
    suddenDeathBeforeTurn: number;
    averageTurnTimes: number[];

    ngOnInit(): void {
        this.losingUser = this.stats.teams.find(t => t.team !== this.stats.winsTheRound).user;
        this.winningUser = this.stats.teams.find(t => t.team === this.stats.winsTheRound).user;
        this.totalHealthPointsPerTeam = this.calcLostHealthPoints(this.stats.turns, this.losingUser);
        this.numberOfTeams = this.stats.teams.length;
        this.averageTurnTimes = (() =>
            this.stats.teams
                .map(team => team.user)
                .map(user => {
                    const turnTimes = this.stats.turns
                        .filter(turn => turn.user === user)
                        .map(turn => turn.timeUsedSeconds);
                    return Math.round(turnTimes.reduce((acc, curr) => acc + curr, 0) / turnTimes.length);
                }))();
        this.suddenDeathBeforeTurn = (() => {
            if (!this.stats.suddenDeath) return -1;

            function timestampToSeconds(timestamp: string): number {
                const timeParts = timestamp.split(/[^\d]/).map(x => parseInt(x));
                return (
                    (timeParts[0] * 60 * 60)
                    + (timeParts[1] * 60)
                    + (timeParts[2])
                    + timeParts[2] / 100
                );
            }

            const turnSeconds = this.stats.turns.map(turn => timestampToSeconds(turn.timestamp));
            const suddenDeathSeconds = timestampToSeconds(this.stats.suddenDeath);
            for (let i = 0; i < turnSeconds.length; i++) {
                const turnSecond = turnSeconds[i];
                if (turnSecond > suddenDeathSeconds) return i + 1;
            }
            console.warn("Sudden death could not be ordered.");
            return -1;
        })();
    }

    getColorOfUser(user: string): string {
        return this.stats.teams.find(t => t.user === user).color;
    }

    retrieveKills(turnNum: number, victim: string): string[] {
        return this.stats.turns[turnNum].damages
            .filter(d => d.victim == victim)
            .map(d => new Array(d.kills).fill(this.killImage))
            .reduce((acc, curr) => {
                acc.push(...curr);
                return acc;
            }, []);
    }

    linearGradientHealthPoints(turnNum: number): string {
        const pastTurns = this.stats.turns.slice(0, turnNum);
        const healthPoints: { team: GameStats.Team, health: number }[] =
            this.stats.teams.map(team => ({
                team: team,
                health: this.totalHealthPointsPerTeam -
                    this.calcLostHealthPoints(pastTurns, team.user)
            }));

        const gradients = healthPoints.map((health, idx) => {
            const remainingHealth =
                Math.round(health.health / this.totalHealthPointsPerTeam * 10000) / 100;
            const lostHealth = 100 - remainingHealth;
            const teamColor = colors[health.team.color.toLowerCase()];
            let result = '';
            if (idx === 0) {
                result += `#1B2021 0, `;
                result += `#1B2021 ${lostHealth === 100 ? '50' : Math.min(lostHealth / 2, 48)}%, `;
                result += `${teamColor} ${lostHealth === 100 ? '50' : Math.min(lostHealth / 2, 48)}%, `;
                result += `${teamColor} 50%`;
            } else if (idx === 1) {
                result += `${teamColor} 50%, `;
                result += `${teamColor} ${lostHealth === 100 ? '0' : Math.max((remainingHealth / 2) + 50, 52)}%, `;
                result += `#1B2021 ${lostHealth === 100 ? '0' : Math.max((remainingHealth / 2) + 50, 52)}%, `;
                result += `#1B2021 100%`;
            }
            return result
        });

        return `linear-gradient(to right, ${gradients.join(', ')})`;
    }

    private calcLostHealthPoints(turns: GameStats.Turn[], victim: string) {
        return turns
            .reduce<number>((acc: number, curr: GameStats.Turn) =>
                acc + curr.damages
                    .filter(d => d.victim === victim)
                    .reduce((accD, currD) => accD + currD.damage, 0), 0);
    }
}


export declare module GameStats {

    export interface Message {
        timestamp: string;
        user: string;
        body: string;
    }

    export interface Damage {
        damage: number;
        kills: number;
        victim: string;
    }

    export interface Turn {
        timestamp: string;
        user: string;
        weapons: string[];
        damages: Damage[];
        timeUsedSeconds: number;
        retreatSeconds: number;
        lossOfControl: boolean;
    }

    export interface Spectator {
        user: string;
        host: boolean;
    }

    export interface Team {
        color: string;
        user: string;
        team: string;
        localPlayer: boolean;
    }

    export interface TeamTimeTotal {
        team: string;
        user: string;
        turn: string;
        retreat: string;
        total: string;
        turnCount: number;
    }

    export interface WormOfTheRound {
        worm: string;
        team: string;
    }

    export interface MostDamageWithOneShot {
        damage: string;
        worm: string;
        team: string;
    }

    export interface GameStats {
        messages: Message[];
        turns: Turn[];
        suddenDeath?: any;
        spectators: Spectator[];
        teams: Team[];
        teamTimeTotals: TeamTimeTotal[];
        gameId: string;
        startedAt: string;
        engineVersion: string;
        fileFormatVersion: string[];
        exportVersion: string;
        gameEnd: string;
        roundTime: string;
        totalGameTimeElapsed: string;
        winsTheRound: string;
        wormOfTheRound: WormOfTheRound;
        mostDamageWithOneShot: MostDamageWithOneShot;
    }

}
