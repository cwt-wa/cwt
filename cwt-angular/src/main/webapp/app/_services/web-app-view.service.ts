import {Injectable} from "@angular/core";
import {NgbPopover} from "@ng-bootstrap/ng-bootstrap";

@Injectable()
export class WebAppViewService {

    public isAppleStandalone: boolean;
    public isStandalone: boolean;

    constructor() {
        this.isAppleStandalone = !!(window.navigator && (window.navigator as any).standalone);
        this.isStandalone = this.isAppleStandalone || window.matchMedia('(display-mode: standalone)').matches;
    }

    public closeUserPanelOnOrientationChange(userPanelIcon: NgbPopover): void {
        return window.addEventListener("orientationchange", () => {
            userPanelIcon.close();
        });
    }
}
