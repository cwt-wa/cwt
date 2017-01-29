import {Component} from "@angular/core";
import "../css/styles.css";
import {StandaloneWebAppService} from "./_services/standalone-web-app.service";

@Component({
    selector: 'my-app',
    templateUrl: './app.component.html'
})
export class AppComponent {

    public isNavCollapsed: boolean = true;
    public isAppleStandalone: boolean;
    public isStandalone: boolean;

    constructor(private standaloneWebAppService: StandaloneWebAppService) {
        this.isAppleStandalone = this.standaloneWebAppService.isAppleStandalone;
        this.isStandalone = this.standaloneWebAppService.isStandalone;
    }
}
