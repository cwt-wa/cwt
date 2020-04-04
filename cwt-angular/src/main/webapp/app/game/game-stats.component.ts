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

@Component({
    selector: 'cwt-game-stats',
    styles: [`
      .turn {
        display: flex;
      }`, `
      .weapon {
        display: inline-block;
        background-clip: padding-box;
        margin: .3rem;
        padding: .2rem;
        box-shadow: 0 0 .25rem #000;
      }`, `
      .blue {
        background-color: ${colors.blue};
        border-color: ${colors.blue};
      }`, `
      .red {
        background-color: ${colors.red};
        border-color: ${colors.red};
      }`, `
      .green {
        background-color: ${colors.green};
        border-color: ${colors.green};
      }`, `
      .yellow {
        background-color: ${colors.yellow};
        border-color: ${colors.yellow};
      }`, `
      .cyan {
        background-color: ${colors.cyan};
        border-color: ${colors.cyan};
      }`, `
      .magenta {
        background-color: ${colors.magenta};
        border-color: ${colors.magenta};
      }`,
    ],
    template: `
        <pre>
            {{totalHealthPointsPerTeam}}
            {{losingUser}}
            {{winningUser}}
        </pre>

        <div *ngFor="let turn of stats?.turns; let index = index">
            <div class="turn" [ngStyle]="{'background-image': linearGradientHealthPoints(index + 1)}">

                {{turn.user}}
                <div *ngFor="let weapon of turn.weapons">
                    <div class="weapon {{getColorOfUser(turn.user).toLowerCase()}}">
                        <cwt-weapon [weapon]="weapon"></cwt-weapon>
                    </div>
                </div>
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

    constructor(private requestService: RequestService, private route: ActivatedRoute) {
    }

    ngOnInit(): void {
        this.gameId
            ? this.loadStats(this.gameId)
            : this.route.paramMap.subscribe(routeParam => this.loadStats(+routeParam.get('id')));
    }


    getColorOfUser(user: string): string {
        return this.stats.teams.find(t => t.user === user).color;
    }

    linearGradientHealthPoints(turnNum: number): string {
        const pastTurns = this.stats.turns.slice(0, turnNum);
        const healthPoints: { team: GameStats.Team, health: number }[] =
            this.stats.teams.map(team => ({
                team: team,
                health: this.totalHealthPointsPerTeam -
                    this.calcLostHealthPoints(pastTurns, team.user)
            }));

        const totalCurrentHealthPoints =
            healthPoints.reduce<number>((acc: number, {health}: { health: number }) => acc + health, 0);

        let previousPercentalPartOfTotalHealth = 0.00;
        const gradients = healthPoints.map(health => {
            const percentalPartOfTotalHealth = Math.round(health.health / totalCurrentHealthPoints * 10000) / 100;
            const teamColor = colors[health.team.color.toLowerCase()];
            const result = `${teamColor} ${previousPercentalPartOfTotalHealth}%, ${teamColor} ${percentalPartOfTotalHealth}%`;
            previousPercentalPartOfTotalHealth = percentalPartOfTotalHealth;
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
