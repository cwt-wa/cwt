import {Component, Input, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {ActivatedRoute} from "@angular/router";

@Component({
    selector: 'cwt-game-stats',
    template: `
        <p>Here's a new component</p>
    `
})
export class GameStatsComponent implements OnInit {

    @Input() gameId: number;

    stats: GameStats.GameStats;

    constructor(private requestService: RequestService, private route: ActivatedRoute) {
    }

    ngOnInit(): void {
        this.route.paramMap.subscribe(routeParam =>
            this.requestService.get<GameStats.GameStats>(`game/${+routeParam.get('id')}/stats`)
                .subscribe(res => this.stats = res));
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
