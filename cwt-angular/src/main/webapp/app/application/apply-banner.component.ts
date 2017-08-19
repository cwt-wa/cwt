import {Component, Input, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";

@Component({
    selector: 'cwt-apply-banner',
    template: require('./apply-banner.component.html')
})
export class ApplyBannerComponent implements OnInit {

    @Input()
    public userId: number;

    public canApply: boolean;

    constructor(private requestService: RequestService) {
    }

    public ngOnInit(): void {
        this.requestService.get<boolean>(`user/${this.userId}/can-apply`)
            .subscribe(res => this.canApply = res);
    }
}
