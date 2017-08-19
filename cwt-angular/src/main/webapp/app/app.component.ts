import {AfterViewInit, Component, OnInit} from "@angular/core";
import {WebAppViewService} from "./_services/web-app-view.service";
import {GmtClockComponent} from "./_util/gmt-clock.component";
import {Router} from "@angular/router";
import {RequestService} from "./_services/request.service";
import {AuthService} from "./_services/auth.service";

@Component({
    selector: 'my-app',
    template: require('./app.component.html'),
    providers: [GmtClockComponent]
})
export class AppComponent implements AfterViewInit, OnInit {

    public isNavCollapsed: boolean = true;
    public isAppleStandalone: boolean;
    public isStandalone: boolean;
    private username: string;

    constructor(private webAppViewService: WebAppViewService, private requestService: RequestService,
                private router: Router, private authService: AuthService) {
        this.isAppleStandalone = this.webAppViewService.isAppleStandalone;
        this.isStandalone = this.webAppViewService.isStandalone;
    }

    public ngOnInit(): void {
        this.requestService.get<{ token: string }>('auth/refresh').subscribe(
            res => {
                this.authService.storeToken(res.token);
                this.username = this.authService.getTokenPayload().sub;
            },
            () => this.authService.voidToken()
        );
    }

    public ngAfterViewInit(): void {
        this.router.setUpLocationChangeListener();
    }

    public logOut(): void {
        this.authService.voidToken();
        this.username = null;
    }
}
