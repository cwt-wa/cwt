import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {AppComponent} from "./app.component";
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {BrowserWindowRef} from "./_utils/browser-window-ref";
import {AppleStandaloneService} from "./_services/apple-standalone.service";

@NgModule({
    imports: [
        BrowserModule,
        NgbModule.forRoot()
    ],
    declarations: [
        AppComponent,
    ],
    providers: [
        BrowserWindowRef,
        AppleStandaloneService
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
