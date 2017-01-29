import {Injectable} from "@angular/core";

function _window(): Window {
    return window;
}

@Injectable()
export class BrowserWindowRef {
    get window(): Window {
        return _window();
    }
}
