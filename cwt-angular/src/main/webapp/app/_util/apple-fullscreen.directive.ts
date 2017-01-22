import {Directive, ElementRef} from "@angular/core";
import {BrowserWindowRef} from "./browser-window-ref";

@Directive({
    selector: '[cwt-apple-fullscreen]'
})
export class AppleFullscreenDirective {

    private readonly APPLE_STATUS_BAR_HEIGHT: number = 10;

    constructor(elem: ElementRef, browserWindowRef: BrowserWindowRef) {
        if (browserWindowRef.window.navigator && (browserWindowRef.window.navigator as any).standalone) {
            elem.nativeElement.style.paddingTop = this.APPLE_STATUS_BAR_HEIGHT + 'px';
        }
    }
}