import {Component, Input, OnInit} from '@angular/core';
import {RequestService} from "./_services/request.service";
import {JwtUser} from "./user/model/jwt-token-payload";

@Component({
    selector: 'cwt-apply',
    template: require('./apply.component.html')
})
export class ApplyComponent implements OnInit {

    @Input()
    public authenticatedUser: JwtUser;

    public canApply: boolean;

    constructor(private requestService: RequestService) {
    }

    public ngOnInit(): void {
        this.requestService.get<boolean>(`user/${this.authenticatedUser.id}/can-apply`)
            .subscribe(res => this.canApply = res);
    }
}
