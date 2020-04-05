import {Component, Input, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {ActivatedRoute} from "@angular/router";

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
        padding: .8rem .5rem;
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
      }

      .weapons {
        display: flex;
        justify-content: center;
        flex-wrap: nowrap;
        flex-grow: 1;
        white-space: nowrap;
        align-items: center;
      }

      .kills {
        width: 44px;
        padding: 0 3px
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
                <div class="turn" [ngStyle]="{'background-image': linearGradientHealthPoints(index + 1)}">
                    <div class="kills">
                        <img *ngFor="let kill of retrieveKills(index, stats.teams[0].user)" [src]="kill"/>
                    </div>
                    <div class="weapons">
                        <div *ngFor="let weapon of turn.weapons">
                            <div class="weapon {{getColorOfUser(turn.user).toLowerCase()}}">
                                <cwt-weapon [weapon]="weapon"></cwt-weapon>
                            </div>
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
                {{stats.roundTime}}
            </div>
        </div>
    `
})
export class GameStatsComponent implements OnInit {

    @Input() gameId: number;

    stats: GameStats.GameStats;
    totalHealthPointsPerTeam: number;
    numberOfTeams: number;
    losingUser: string;
    winningUser: string;
    killImage: string = require('../../img/grave.png');
    waterImage: string = require('../../img/water.gif');
    suddenDeathBeforeTurn: number;
    averageTurnTimes: number[];

    constructor(private requestService: RequestService, private route: ActivatedRoute) {
    }

    ngOnInit(): void {
        console.log(this.killImage);

        this.gameId
            ? this.loadStats(this.gameId)
            : this.route.paramMap.subscribe(routeParam => this.loadStats(+routeParam.get('id')));
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
                result += `#1B2021 ${lostHealth / 2}%, `;
                result += `${teamColor} ${lostHealth / 2}%, `;
                result += `${teamColor} 50%`;
            } else if (idx === 1) {
                result += `${teamColor} 50%, `;
                result += `${teamColor} ${(remainingHealth / 2) + 50}%, `;
                result += `#1B2021 ${(remainingHealth / 2) + 50}%, `;
                result += `#1B2021 100%`;
            }
            return result
        });

        return `linear-gradient(to right, ${gradients.join(', ')})`;
    }

    private loadStats(gameId: number) {
        return this.requestService.get<GameStats.GameStats>(`game/${gameId}/stats`)
            .subscribe(res => {
                this.stats = res;
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
            });
    }

    private calcLostHealthPoints(turns: GameStats.Turn[], victim: string) {
        return turns
            .reduce<number>((acc: number, curr: GameStats.Turn) =>
                acc + curr.damages
                    .filter(d => d.victim === victim)
                    .reduce((accD, currD) => accD + currD.damage, 0), 0);
    }
}


declare module GameStats {

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
