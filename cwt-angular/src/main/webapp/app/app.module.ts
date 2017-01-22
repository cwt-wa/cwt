import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {AppComponent} from "./app.component";
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {BrowserWindowRef} from "./_util/browser-window-ref";
import {AppleFullscreenDirective} from "./_util/apple-fullscreen.directive";

@NgModule({
    imports: [
        BrowserModule,
        NgbModule.forRoot()
    ],
    declarations: [
        AppComponent,
        AppleFullscreenDirective
    ],
    providers: [
        BrowserWindowRef
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
