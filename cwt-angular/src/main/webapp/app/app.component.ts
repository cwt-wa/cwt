import {Component} from "@angular/core";
import "../css/styles.css";
import {AppleStandaloneService} from "./_services/apple-standalone.service";

@Component({
    selector: 'my-app',
    templateUrl: './app.component.html'
})
export class AppComponent {

    public isAppleStandalone: boolean;

    constructor(private appleStandaloneService: AppleStandaloneService) {
        this.isAppleStandalone = this.appleStandaloneService.isAppleStandalone;
    }
}
