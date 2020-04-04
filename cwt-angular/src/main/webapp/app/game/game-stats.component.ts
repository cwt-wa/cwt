import {Component, Input, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {ActivatedRoute} from "@angular/router";
import Turn = GameStats.Turn;

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
        background-color: #9D9FFF;
        border-color: #9D9FFF;
      }`, `
      .red {
        background-color: #FF7F7F;
        border-color: #FF7F7F;
      }`, `
      .green {
        background-color: #80FF80;
        border-color: #80FF80;
      }`, `
      .yellow {
        background-color: #FFFF80;
        border-color: #FFFF80;
      }`, `
      .cyan {
        background-color: #80FFFF;
        border-color: #80FFFF;
      }`, `
      .magenta {
        background-color: #FF82FF;
        border-color: #FF82FF;
      }`,
    ],
    template: `
        <pre>
            {{totalHealthPointsPerTeam}}
            {{losingUser}}
            {{winningUser}}
        </pre>

        <div *ngFor="let turn of stats?.turns">
            <div class="turn" [ngStyle]="{'background-image': linearGradientHealthPoints()}">

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

    linearGradientHealthPoints(): string {
        // W = G * p%
        // todo
        return 'linear-gradient(to right, #9D9FFF 20%, #FF7F7F 20%)';
    }

    private loadStats(gameId: number) {
        return this.requestService.get<GameStats.GameStats>(`game/${gameId}/stats`)
            .subscribe(res => {
                this.stats = res;
                this.losingUser = this.stats.teams.find(t => t.team !== this.stats.winsTheRound).user;
                this.winningUser = this.stats.teams.find(t => t.team === this.stats.winsTheRound).user;
                this.totalHealthPointsPerTeam =
                    this.stats.turns
                        .reduce<number>((acc: number, curr: Turn) =>
                            acc + curr.damages
                                .filter(d => d.victim === this.losingUser)
                                .reduce((accD, currD) => accD + currD.damage, 0), 0)
            });
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
