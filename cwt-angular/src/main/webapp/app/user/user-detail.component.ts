import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {ActivatedRoute} from "@angular/router";
import {UserDetailDto} from "../custom";

@Component({
    selector: 'cwt-user-detail',
    template: require('../user/user-detail.component.html')
})
export class UserDetailComponent implements OnInit {

    @ViewChild("userPhoto") userPhoto: ElementRef<HTMLImageElement>;

    user: UserDetailDto;
    randomPic: any;

    constructor(private requestService: RequestService, private route: ActivatedRoute) {
    }

    ngOnInit(): void {
        this.route.paramMap.subscribe(res => {
            this.requestService.get<UserDetailDto>(`user/${res.get('username')}`)
                .subscribe(res => {
                    res.userStats = res.userStats.sort((a, b) => b.year - a.year);
                    this.user = res;

                    if (!this.user.hasPic) {
                        this.userPhoto.nativeElement.src = require('../../img/albino/' + Math.ceil(Math.random() * 14) + '.jpg');
                        this.userPhoto.nativeElement.alt = 'Random profile photo';
                    } else {
                        this.requestService.getBlob(`user/${this.user.id}/photo`).subscribe(res => {
                            // @ts-ignore
                            this.userPhoto.nativeElement.src = (window.URL || window.webkitURL).createObjectURL(res);
                            this.userPhoto.nativeElement.alt = 'Profile photo of ' + this.user.username;
                        });
                    }
                });
        });
    }
}
