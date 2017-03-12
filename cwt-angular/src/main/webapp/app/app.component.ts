import {Component, Inject, ViewChild, AfterViewInit} from "@angular/core";
import {StandaloneWebAppService} from "./_services/standalone-web-app.service";
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

    constructor(@Inject(StandaloneWebAppService) private standaloneWebAppService: StandaloneWebAppService) {
        this.isAppleStandalone = this.standaloneWebAppService.isAppleStandalone;
        this.isStandalone = this.standaloneWebAppService.isStandalone;
    }

    ngAfterViewInit(): void {
        window.addEventListener("orientationchange", () => {
            this.userPanelIcon.close();
        });
    }
}
