import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {ActivatedRoute} from "@angular/router";
import {UserDetailDto} from "../custom";
import {BinaryService} from "../_services/binary.service";

@Component({
    selector: 'cwt-user-detail',
    template: require('../user/user-detail.component.html')
})
export class UserDetailComponent implements OnInit {

    @ViewChild("userPhoto") userPhoto: ElementRef<HTMLImageElement>;

    user: UserDetailDto;
    randomPic: any;

    constructor(private requestService: RequestService, private route: ActivatedRoute,
                private binaryService: BinaryService) {
    }

    ngOnInit(): void {
        const res = this.route.snapshot.paramMap;
        if (res.get('username') != null) {
            this.requestService.get<UserDetailDto>(`user/${res.get('username')}`)
                .subscribe(this.onUser.bind(this));
        } else if (res.get('id') != null) {
            this.requestService.get<UserDetailDto>(`user/${res.get('id')}`)
                .subscribe(this.onUser.bind(this));
        }
    }

    private onUser(res: UserDetailDto) {
        res.userStats = res.userStats.sort((a, b) => b.year - a.year);
        this.user = res;

        this.binaryService.getUserPhoto(this.user.id)
            .subscribe(
                res => this.userPhoto.nativeElement.src = res,
                () => this.userPhoto.nativeElement.src = this.binaryService.randomPic());
        this.userPhoto.nativeElement.alt = 'Profile photo of ' + this.user.username;
    }
}
