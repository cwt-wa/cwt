import {Injectable} from "@angular/core";

@Injectable()
export class StandaloneWebAppService {

    public isAppleStandalone: boolean;
    public isStandalone: boolean;

    constructor() {
        this.isAppleStandalone = !!(window.navigator && (window.navigator as any).standalone);
        this.isStandalone = this.isAppleStandalone || window.matchMedia('(display-mode: standalone)').matches;
    }
}
