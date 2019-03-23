import {Observable} from "rxjs";
import {Injectable} from "@angular/core";
import {CanDeactivate} from "@angular/router";

export interface Deactivatable {
    canDeactivate(): Observable<boolean> | Promise<boolean> | boolean;
}

@Injectable()
export class CanDeactivateGuard implements CanDeactivate<Deactivatable> {

    canDeactivate(component: Deactivatable) {
        return component.canDeactivate ? component.canDeactivate() : true;
    }
}
