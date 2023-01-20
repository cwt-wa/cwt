import {Component, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {RankingDto} from "../custom";

@Component({
    selector: 'cwt-ranking',
    styles: [`
        table {
            color: #aaa;
            width: 100%;
        }
        table a {
            color: #ccc;
            font-weight: bold;
        }
        table tr:first-child td:first-child {
            border-top-left-radius: .5rem;
        }
        table tr:first-child td:last-child {
            border-top-right-radius: .5rem;
        }
        table tr:last-child td:first-child {
            border-bottom-left-radius: .5rem;
        }
        table tr:last-child td:last-child {
            border-bottom-right-radius: .5rem;
        }
        td {
            padding: .4rem .7rem;
        }
        td.user, td.place, td.points {
            font-weight: strong;
            font-size: 1.2rem;
        }
        th {
            padding-left: 1.3rem;
            padding-bottom: .2rem;
            font-size: .8rem;
            font-variant: all-petite-caps;
            font-weight: normal;
            line-height: .7rem;
            vertical-align: bottom;
        }
        th.rounds {
            padding-left: 2.5rem;
        }
        td.lastDiff,
        td.place,
        td.points,
        td.won,
        td.wonRatio,
        td.lost,
        td.played,
        td.participations {
            font-family: monospace;
            text-align: right;
        }
        td.lastDiff span::after {
            content: "\\25CF";
        }
        td.lastDiff span.gain {
            color: rgb(42, 157, 143);
        }
        td.lastDiff span.gain::after {
            content: "\\25B2"
        }
        td.lastDiff span.lose {
            color: rgb(200, 0, 62);
        }
        td.lastDiff span.lose::after {
            content: "\\25BC"
        }
        td.won,
        td.participations {
            padding-left: 2rem;
        }
        td.reach {
            text-align: left;
            font-size: 1.2rem;
            white-space: nowrap;
        }
        td.reach img {
            height: 3rem;
        }
        td.reach .imgcont {
            display: inline-block;
        }
        td.reach .imgcont:not(:first-child) {
            margin-left: 1.5rem;
        }
        td.won {
            color: rgb(42, 157, 143);
        }
        td.wonRatio {
            padding-left: 0;
        }
        td.lost {
            color: rgb(200, 0, 62);
        }
    `],
    template: `
    <div class="float-right mt-4" *ngIf="rankings?.length">
        <label>
            Only show participants since
            <select [(ngModel)]="setting" name="setting" (change)="filter($event)">
                <option *ngFor="let t of tournaments; index as index" value="{{ t }}">
                    {{ t }}{{ index == 0 ? ' (all)' : '' }}
                </option>
            </select>
        </label>
    </div>
    <h1 class="mb-4">All-Time Ranking</h1>
    <img *ngIf="!rankings?.length" src="/loading.gif">
    <table *ngIf="rankings?.length">
        <thead>
        <tr>
            <th colspan="4">
                Change since<br>
                last tournament
            </th>
            <th class="rounds" colspan="4">
                Rounds won, lost, played
            </th>
            <th colspan="3">
                Participantions<br>
                and most recent
            </th>
        </tr>
        </thead>
        <tbody>
        <tr *ngFor="let r of rankings; index as index;">
            <ng-container *ngIf="'rgba(50, 42, 33, ' + bgs[r.lastTournament.year] + ')' as bg">
                <td class="lastDiff" [style.background]="bg">
                    <span [class.gain]="r.lastDiff < 0" [class.lose]="r.lastDiff > 0">
                        {{ absLastDiffs[index] }}
                    </span>
                </td>
                <td class="place" [style.background]="bg">
                    {{ index + 1 }}
                </td>
                <td class="user" [style.background]="bg">
                    <a [routerLink]="['/users', r.user.username]">{{r.user.username}}</a>
                </td>
                <td class="points" [style.background]="bg">
                    <strong>{{ r.points }}</strong>
                </td>
                <td class="won" [style.background]="bg">
                    {{ r.won }}
                </td>
                <td class="wonRatio" [style.background]="bg">
                    {{ r.wonRatio }}%
                </td>
                <td class="lost" [style.background]="bg">
                    {{ r.lost }}
                </td>
                <td class="played" [style.background]="bg">
                    {{ r.played }}
                </td>
                <td class="participations" [style.background]="bg">
                    {{ r.participations }}
                </td>
                <td [style.background]="bg">
                    <a [routerLink]="['/archive', r.lastTournament.year]">
                        {{ r.lastTournament.year }}
                    </a>
                </td>
                <td class="reach" [style.background]="bg">
                    <ng-container *ngFor="let kv of trophies">
                        <div class="imgcont" *ngIf="r[kv[0]] > 0">
                            {{ r[kv[0]] }}x
                            <img [src]="kv[1]">
                        </div>
                    </ng-container>
                </td>
            </ng-container>
        </tr>
        </tbody>
    </table>
    `
})
export class RankingComponent implements OnInit {

    public rankings: RankingDto[];
    public absLastDiffs: number[];
    public trophies = ['gold', 'silver', 'bronze']
        .map(t => ([t, require(`../../img/reach/${t}.png`)]));
    public bgs: {[number]: number};
    public tournaments: number[];
    public setting: number;

    constructor(private requestService: RequestService) {
    }

    ngOnInit(): void {
        this.requestService.get<RankingDto[]>('ranking')
            .subscribe(res => {
                this.absLastDiffs = res.map(x => Math.abs(x.lastDiff));
                this.tournaments = res
                    .map(r => r.lastTournament.year)
                    .filter((y, idx, arr) => arr.indexOf(y) === idx)
                    .sort();
                this.bgs = this.tournaments
                    .map((y, idx, arr) => [y, idx / arr.length + (1 - (arr.length-1) / arr.length)])
                    .reduce((acc, [y, op]) => {
                        acc[y] = op
                        return acc;
                    }, {});
                this.setting = this.tournaments[0];
                this.allRankings = [...res];
                this.rankings = res;
            });
    }

    filter(e): void {
        const y = e.target.value;
        console.log(y, this.setting);
        this.rankings = this.allRankings
            .filter(r => r.lastTournament.year >= y);
    }
}
