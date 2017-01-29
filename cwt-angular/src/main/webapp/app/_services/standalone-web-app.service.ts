import {Injectable} from "@angular/core";
import {BrowserWindowRef} from "../_utils/browser-window-ref";

@Injectable()
export class StandaloneWebAppService {

    public isAppleStandalone: boolean;
    public isStandalone: boolean;

    constructor(private browserWindowRef: BrowserWindowRef) {
        this.isAppleStandalone =
            !!(this.browserWindowRef.window.navigator && (this.browserWindowRef.window.navigator as any).standalone);
        this.isStandalone =
            this.isAppleStandalone || this.browserWindowRef.window.matchMedia('(display-mode: standalone)').matches;
    }
}
