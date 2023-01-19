import {Component, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {RankingDto} from "../custom";

@Component({
    selector: 'cwt-ranking',
    styles: [`
        td {
            padding: .4rem .7rem;
        }
        td.lastDiff, td.place, td.points {
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
            color: rgb(137, 2, 62);
        }
        td.lastDiff span.lose::after {
            content: "\\25BC"
        }
        td.reach {
            text-align: left;
            font-size: 1.2rem;
        }
        td.reach img {
            height: 3rem;
            padding-right: 1rem;
        }
        td.won {
            color: rgb(42, 157, 143);
        }
        td.lost {
            color: rgb(137, 2, 62);
        }
    `],
    template: `
    <img *ngIf="!rankings?.length" src="/loading.gif">
    <ng-container *ngIf="rankings?.length">
        <h1>All-Time Ranking</h1>
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
            <tr *ngFor="let r of rankings; index as index">
                <td class="lastDiff">
                    <span [class.gain]="r.lastDiff < 0" [class.lose]="r.lastDiff > 0">
                        {{ absLastDiffs[index] }}
                    </span>
                </td>
                <td class="place">
                    {{ index + 1 }}
                </td>
                <td>
                    <cwt-user [username]="r.user.username"></cwt-user>
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
                <td>
                    {{ r.played }}
                </td>
                <td>
                    {{ r.participations }}
                </td>
                <td>
                    <a [routerLink]="['/archive', r.lastTournament.year]">
                        {{ r.lastTournament.year }}
                    </a>
                </td>
                <td class="reach">
                    <ng-container *ngFor="let kv of trophies">
                        <ng-container *ngIf="r[kv[0]] > 0">
                            {{ r[kv[0]] }}x
                            <img [src]="kv[1]">
                        </ng-container>
                    </ng-container>
                </td>
            </tr>
            </tbody>
        </table>
        <pre>{{ rankings[0] | json }}</pre>
    </ng-container>
    `
})
export class RankingComponent implements OnInit {

    public rankings: RankingDto[];
    public absLastDiffs: number[];
    public trophies = ['gold', 'silver', 'bronze']
        .map(t => ([t, require(`../../img/reach/${t}.png`)]));

    constructor(private requestService: RequestService) {
        console.log(this.trophies);
    }

    ngOnInit(): void {
        this.requestService.get<RankingDto[]>('ranking')
            .subscribe(res => {
                this.rankings = res;
                this.absLastDiffs = res.map(x => Math.abs(x.lastDiff));
            });
    }
}
