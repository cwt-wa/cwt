import {Component, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {RankingDto} from "../custom";

@Component({
    selector: 'cwt-ranking',
    styles: [`
        .wrapper {
            border-radius: .5rem;
            overflow: hidden;
        }
        table {
            width: 100%;
        }
        table {
            color: #aaa;
        }
        table a {
            color: #ccc;
        }
        td {
            padding: .4rem .7rem;
        }
        td.user, td.place, td.points {
            font-weight: strong;
            font-size: 1.2rem;
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
    <h1 class="mb-4">All-Time Ranking</h1>
    <img *ngIf="!rankings?.length" src="/loading.gif">
    <div class="wrapper" *ngIf="rankings?.length">
        <table>
            <!--
            <thead>
            <tr>
                <th></th>
                <th>User</th>
                <th>Points</th>
            </tr>
            </thead>
            -->
            <tbody>
            <tr *ngFor="let r of rankings; index as index" [style.background]="'rgba(50, 42, 33, ' + bgs[r.lastTournament.year] + ')'">
                <td class="lastDiff">
                    <span [class.gain]="r.lastDiff < 0" [class.lose]="r.lastDiff > 0">
                        {{ absLastDiffs[index] }}
                    </span>
                </td>
                <td class="place">
                    {{ index + 1 }}
                </td>
                <td class="user">
                    <a [routerLink]="['/users', r.user.username]">{{r.user.username}}</a>
                </td>
                <td class="points">
                    <strong>{{ r.points }}</strong>
                </td>
                <td class="won">
                    {{ r.won }}
                </td>
                <td class="wonRatio">
                    {{ r.wonRatio }}%
                </td>
                <td class="lost">
                    {{ r.lost }}
                </td>
                <td class="played">
                    {{ r.played }}
                </td>
                <td class="participations">
                    {{ r.participations }}
                </td>
                <td>
                    <a [routerLink]="['/archive', r.lastTournament.year]">
                        {{ r.lastTournament.year }}
                    </a>
                </td>
                <td class="reach">
                    <ng-container *ngFor="let kv of trophies">
                        <div class="imgcont" *ngIf="r[kv[0]] > 0">
                            {{ r[kv[0]] }}x
                            <img [src]="kv[1]">
                        </div>
                    </ng-container>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
    `
})
export class RankingComponent implements OnInit {

    public rankings: RankingDto[];
    public absLastDiffs: number[];
    public trophies = ['gold', 'silver', 'bronze']
        .map(t => ([t, require(`../../img/reach/${t}.png`)]));
    public bgs: {[number]: number};

    constructor(private requestService: RequestService) {
    }

    ngOnInit(): void {
        this.requestService.get<RankingDto[]>('ranking')
            .subscribe(res => {
                this.absLastDiffs = res.map(x => Math.abs(x.lastDiff));
                this.bgs = res
                    .map(r => r.lastTournament.year)
                    .filter((y, idx, arr) => arr.indexOf(y) === idx)
                    .sort()
                    .map((y, idx, arr) => [y, idx / arr.length + (1 - (arr.length-1) / arr.length)])
                    .reduce((acc, [y, op]) => {
                        acc[y] = op
                        return acc;
                    }, {});
                this.rankings = res;
            });
    }
}
