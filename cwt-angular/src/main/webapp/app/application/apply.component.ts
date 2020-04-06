import {Component, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {AuthService} from "../_services/auth.service";
import {JwtUser} from "../custom";
import {Router} from "@angular/router";
import {Toastr} from "../_services/toastr";

@Component({
    selector: 'cwt-apply',
    template: require('./apply.component.html')
})
export class ApplyComponent implements OnInit {

    agreeToRules: boolean;
    rules: string;

    constructor(private requestService: RequestService, private authService: AuthService,
                private router: Router, private toastr: Toastr) {
    }

    public ngOnInit(): void {
        this.requestService.get<string[]>('rules/current')
            .subscribe(res => this.rules = res[0]);
    }

    public submit(): void {
        const authenticatedUser: JwtUser = this.authService.getUserFromTokenPayload();

        this.requestService.post(`user/${authenticatedUser.id}/application`)
            .subscribe(() => {
                this.toastr.success('You have applied successfully.');
                this.router.navigateByUrl('/applicants');
            })
    }
}
