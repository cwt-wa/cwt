import {Component, Inject} from "@angular/core";
import {StandaloneWebAppService} from "./_services/standalone-web-app.service";

@Component({
    selector: 'my-app',
    template: require('./app.component.html')
})
export class AppComponent {

    public isNavCollapsed: boolean = true;
    public isAppleStandalone: boolean;
    public isStandalone: boolean;

    constructor(@Inject(StandaloneWebAppService) private standaloneWebAppService: StandaloneWebAppService) {
        this.isAppleStandalone = this.standaloneWebAppService.isAppleStandalone;
        this.isStandalone = this.standaloneWebAppService.isStandalone;
    }
}
