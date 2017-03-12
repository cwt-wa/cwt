import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {AppComponent} from "./app.component";
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {StandaloneWebAppService} from "./_services/standalone-web-app.service";
import {GmtClockComponent} from "./gmt-clock.component";

@NgModule({
    imports: [
        BrowserModule,
        NgbModule.forRoot()
    ],
    declarations: [
        AppComponent,
        GmtClockComponent
    ],
    providers: [
        StandaloneWebAppService
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
