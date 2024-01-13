import {Component, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {RankingDto} from "../custom";

@Component({
    selector: 'cwt-ranking',
    styles: [`
        h1 {
            white-space: nowrap;
            position: relative;
        }
        h1 .hyphen {
            color: transparent;
            width: 1.5rem;
            display: inline-block;
            text-align: center;
        }
        @media screen and (max-width: 400px) {
            td:not(:nth-child(2)) {
                padding-left: .1rem !important;
                padding-right: .1rem !important;
            }
        }
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
            white-space: nowrap;
        }
        td.user, td.place, td.points {
            font-weight: strong;
            font-size: 1.2rem;
        }
        th {
            text-align: center;
            padding-bottom: .2rem;
            font-size: .8rem;
            font-variant: all-petite-caps;
            font-weight: normal;
            line-height: .7rem;
            vertical-align: bottom;
            white-space: nowrap;
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
        .histw {
            position: absolute;
            left: 1.8rem;
            z-index: -1;
            display: inline-block;
            height: 50px;
            width: 50px;
            transform: scaleX(-1);
        }
        .histw img {
            height: 100%;
        }
        .histw .mustache {
            position: absolute;
            height: 4px;
            top: 28px;
            border-radius: 100%;
            background-color: gray;
        }
        .histw .mustache.mustache-1 {
            left: 5px;
            width: 12px;
            transform: rotate(10deg);
        }
        .histw .mustache.mustache-2 {
            left: 18px;
            width: 16px;
            transform: rotate(-13deg);
        }
    `],
    template: `
    <div class="row mb-5">
        <div class="col">
            <h1>
                <div class="histw">
                    <img src="${require(`../../img/worms/worm.png`)}">
                    <div class="mustache mustache-1"></div>
                    <div class="mustache mustache-2"></div>
                </div>
                All<span class="hyphen">-</span>Time Ranking
            </h1>
        </div>
        <div class="col text-right mt-md-4" *ngIf="rankings?.length">
            <label>
                Only show participants since
                <select [(ngModel)]="setting" name="setting" (change)="filter($event)">
                    <option *ngFor="let t of tournaments; index as index" value="{{ t }}">
                        {{ t }}{{ index == 0 ? ' (all)' : '' }}
                    </option>
                </select>
            </label>
        </div>
    </div>
    <img *ngIf="!rankings?.length" src="/loading.gif">
    <table *ngIf="rankings?.length">
        <thead>
        <tr>
            <th class="place" colspan="4">
                <span class="d-none d-sm-table-cell">
                    Change since<br>
                    last tournament
                </span>
            </th>
            <th class="rounds d-none d-lg-table-cell" colspan="4">
                Rounds won, lost, played
            </th>
            <th class="d-none d-md-table-cell participations" colspan="2">
                Participantions<br>
                and most recent
            </th>
        </tr>
        </thead>
        <tbody>
        <tr *ngFor="let r of rankings; index as index;">
            <ng-container *ngIf="'rgba(50, 42, 33, ' + bgs[r.lastTournament.year] + ')' as bg">
                <td class="lastDiff" [style.background]="bg">
                    <span class="d-none d-sm-table-cell"
                          [class.gain]="r.lastDiff < 0" [class.lose]="r.lastDiff > 0">
                        {{ absLastDiffs[r.user.id] }}
                    </span>
                </td>
                <td class="place" [style.background]="bg">
                    {{ place[r.user.id] }}
                </td>
                <td class="user" [style.background]="bg">
                    <a [routerLink]="['/users', r.user.username]">{{r.user.username}}</a>
                </td>
                <td class="points" [style.background]="bg">
                    <strong>{{ r.points }}</strong>
                </td>
                <td class="won d-none d-lg-table-cell" [style.background]="bg">
                    {{ r.won }}
                </td>
                <td class="wonRatio d-none d-lg-table-cell" [style.background]="bg">
                    {{ r.wonRatio }}%
                </td>
                <td class="lost d-none d-lg-table-cell" [style.background]="bg">
                    {{ r.lost }}
                </td>
                <td class="played d-none d-lg-table-cell" [style.background]="bg">
                    {{ r.played }}
                </td>
                <td class="d-none d-md-table-cell participations" [style.background]="bg">
                    {{ r.participations }}
                </td>
                <td class="d-none d-md-table-cell " [style.background]="bg">
                    <a [routerLink]="['/archive', r.lastTournament.year]">
                        {{ r.lastTournament.year }}
                    </a>
                </td>
                <td class="reach" [style.background]="bg">
                    <div class="d-none d-xl-table-cell">
                        <ng-container *ngFor="let kv of trophies">
                            <div class="imgcont" *ngIf="r[kv[0]] > 0">
                                {{ r[kv[0]] }}x
                                <img [src]="kv[1]">
                            </div>
                        </ng-container>
                    </div>
                </td>
            </ng-container>
        </tr>
        </tbody>
    </table>
    <div *ngIf="rankings?.length" class="mt-5 alert alert-info">
        This ranking has been generated
        <strong>{{ updatedAt | cwtDate }}</strong>
        using the
        <strong><a class="text-danger" href="https://github.com/Zemke/relrank" target="_blank">Relative Ranking System</a></strong>
        originally developed for
        <strong><a class="text-danger" href="https://wl.zemke.io" target="_blank">Worms League</a></strong>.<br>
        Its main purpose is to satisfy curiosity and it might be used as a seed for groups or playoffs.
        CWT is an annual tournament and this ranking is not meant to and cannot possibly accurately
        display actual skill level of players.
    </div>
    `
})
export class RankingComponent implements OnInit {

    public rankings: RankingDto[];
    public allRankings: RankingDto[];
    public absLastDiffs: {[key: number]: number} = {};
    public place: {[key: number]: number} = {};
    public trophies = ['gold', 'silver', 'bronze']
        .map(t => ([t, require(`../../img/reach/${t}.png`)]));
    public bgs: {[key: number]: number} = {};
    public tournaments: number[];
    public setting: number;
    public updatedAt: string;

    constructor(private requestService: RequestService) {
    }

    ngOnInit(): void {
        this.requestService.get<RankingDto[]>('ranking').subscribe(res => {
            this.tournaments = res
                .map(r => r.lastTournament.year)
                .filter((y, idx, arr) => arr.indexOf(y) === idx)
                .sort();
            this.bgs = this.tournaments
                .map((y, idx, arr) => [y, idx / arr.length + (1 - (arr.length-1) / arr.length)])
                .reduce((acc: {[key: number]: number}, [y, op]) => {
                    acc[y] = op
                    return acc;
                }, {});
            this.setting = this.tournaments[0];
            this.allRankings = [...res];
            this.updatedAt = res
                .map(r => r.modified)
                .sort((a,b) => (new Date(b)).getTime() - (new Date(a)).getTime())[0];
            res.forEach((r, idx) => {
                this.absLastDiffs[r.user.id] = Math.abs(r.lastDiff);
                this.place[r.user.id] = idx + 1;
            });
            this.rankings = res;
        });
    }

    filter(e: Event): void {
        const y = parseInt((e.target as HTMLSelectElement).value);
        this.rankings = this.allRankings.filter(r => r.lastTournament.year >= y);
    }
}

