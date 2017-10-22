import {Injectable} from '@angular/core';
import {RequestService} from "./request.service";
import {Configuration, ConfigurationKey} from "../custom";
import {Observable} from "rxjs/Observable";

@Injectable()
export class ConfigurationService {

    constructor(private requestService: RequestService) {
    }

    public requestByKeys<T>(...configurationKeys: ConfigurationKey[]): Observable<Configuration<T>[]> {
        return this.requestService.get<Configuration<T>[]>('configuration', {keys: configurationKeys});
    }

    public request<T>(): Observable<Configuration<T>> {
        return this.requestService.get<Configuration<T>>('configuration');
    }
}
