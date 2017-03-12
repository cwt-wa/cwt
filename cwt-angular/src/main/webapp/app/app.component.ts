import {Component, Inject, ViewChild, AfterViewInit} from "@angular/core";
import {WebAppViewService} from "./_services/web-app-view.service";
import {NgbPopover} from "@ng-bootstrap/ng-bootstrap";
import {GmtClockComponent} from "./gmt-clock.component";

@Component({
    selector: 'my-app',
    template: require('./app.component.html'),
    providers: [GmtClockComponent]
})
export class AppComponent implements AfterViewInit {
    @ViewChild("userPanelIcon")
    private userPanelIcon: NgbPopover;

    public isNavCollapsed: boolean = true;
    public isAppleStandalone: boolean;
    public isStandalone: boolean;

    constructor(@Inject(WebAppViewService) private webAppViewService: WebAppViewService) {
        this.isAppleStandalone = this.webAppViewService.isAppleStandalone;
        this.isStandalone = this.webAppViewService.isStandalone;
    }

    ngAfterViewInit(): void {
        this.webAppViewService.closeUserPanelOnOrientationChange(this.userPanelIcon);
    }
}
