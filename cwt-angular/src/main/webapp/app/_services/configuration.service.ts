import {Injectable} from '@angular/core';
import {RequestService} from "./request.service";
import {Configuration, ConfigurationKey} from "../custom";
import {Observable} from "rxjs/Observable";

@Injectable()
export class ConfigurationService {

    constructor(private requestService: RequestService) {
    }

    public requestByKeys(...configurationKeys: ConfigurationKey[]): Observable<Configuration[]> {
        return this.requestService.get<Configuration[]>('configuration', {keys: configurationKeys});
    }

    public request(): Observable<Configuration> {
        return this.requestService.get<Configuration>('configuration');
    }
}
