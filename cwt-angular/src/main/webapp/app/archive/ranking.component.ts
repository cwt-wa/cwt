import {Component, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {RankingDto} from "../custom";

@Component({
    selector: 'cwt-ranking',
    template: `
    <h1>All-Time Ranking</h1>
    <table>
        <thead>
        <tr>
            <th></th>
            <th>User</th>
            <th>Points</th>
        </tr>
        </thead>
        <tbody>
        <tr *ngFor="let r of rankings; index as index">
            <td>
                {{ index + 1 }}
            </td>
            <td>
                <cwt-user [username]="r.user.username"></cwt-user>
            </td>
            <td>
                <strong>{{ r.points }}</strong>
            </td>
        </tr>
        </tbody>
    </table>
    `
})
export class RankingComponent implements OnInit {

    public rankings: RankingDto[]

    constructor(private requestService: RequestService) {
    }

    ngOnInit(): void {
        this.requestService.get<RankingDto[]>('ranking')
            .subscribe(res => this.rankings = res);
    }
}
