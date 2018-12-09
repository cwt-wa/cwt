import {Component, Inject, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {ActivatedRoute} from "@angular/router";
import {UserDetailDto} from "../custom";
import {APP_CONFIG, AppConfig} from "../app.config";

@Component({
    selector: 'cwt-user-detail',
    template: require('../user/user-detail.component.html')
})
export class UserDetailComponent implements OnInit {
    user: UserDetailDto;
    randomPic: any;

    constructor(private requestService: RequestService, private route: ActivatedRoute,
                @Inject(APP_CONFIG) public appConfig: AppConfig) {
    }

    ngOnInit(): void {
        this.route.paramMap.subscribe(res => {
            this.requestService.get<UserDetailDto>(`user/${res.get('username')}`)
                .subscribe(res => {
                    res.userStats = res.userStats.sort((a, b) => b.year - a.year);
                    this.user = res;

                    if (!this.user.hasPic) {
                        this.randomPic = require('../../img/albino/' + Math.ceil(Math.random() * 14) + '.jpg');
                    }
                });
        });
    }
}
