import {AfterViewInit, Component} from "@angular/core";
import {WebAppViewService} from "./_services/web-app-view.service";
import {GmtClockComponent} from "./gmt-clock.component";
import {Router} from "@angular/router";

@Component({
    selector: 'my-app',
    template: require('./app.component.html'),
    providers: [GmtClockComponent]
})
export class AppComponent implements AfterViewInit {
    public isNavCollapsed: boolean = true;
    public isAppleStandalone: boolean;
    public isStandalone: boolean;

    constructor(private webAppViewService: WebAppViewService, private router: Router) {
        this.isAppleStandalone = this.webAppViewService.isAppleStandalone;
        this.isStandalone = this.webAppViewService.isStandalone;
    }

    ngAfterViewInit(): void {
        this.router.setUpLocationChangeListener();
    }
}
