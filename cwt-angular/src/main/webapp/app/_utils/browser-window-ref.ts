import {Injectable} from "@angular/core";

// TODO Window vs document?
// TODO If document is fine, couldn't I just use it directly, like I do for `setInterval`?
function _window(): Window {
    return window;
}

@Injectable()
export class BrowserWindowRef {
    get window(): Window {
        return _window();
    }
}
