import {Component, Inject, ViewChild, AfterViewInit} from "@angular/core";
import {StandaloneWebAppService} from "./_services/standalone-web-app.service";
import {BrowserWindowRef} from "./_utils/browser-window-ref";
import {NgbPopover} from "@ng-bootstrap/ng-bootstrap";

@Component({
    selector: 'my-app',
    template: require('./app.component.html')
})
export class AppComponent implements AfterViewInit {
    @ViewChild("userPanelIcon")
    private userPanelIcon: NgbPopover;

    public isNavCollapsed: boolean = true;
    public isAppleStandalone: boolean;
    public isStandalone: boolean;

    constructor(@Inject(StandaloneWebAppService) private standaloneWebAppService: StandaloneWebAppService,
                @Inject(BrowserWindowRef) private browserWindowRef: BrowserWindowRef) {
        this.isAppleStandalone = this.standaloneWebAppService.isAppleStandalone;
        this.isStandalone = this.standaloneWebAppService.isStandalone;
    }

    ngAfterViewInit(): void {
        this.browserWindowRef.window.addEventListener("orientationchange", () => {
            this.userPanelIcon.close();
        });
    }
}
