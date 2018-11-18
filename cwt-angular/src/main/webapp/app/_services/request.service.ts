import {Inject, Injectable} from "@angular/core";
import {Observable} from "rxjs/Observable";
import "rxjs/add/operator/map";
import "rxjs/add/operator/catch";
import {APP_CONFIG, AppConfig} from "../app.config";
import {AuthService} from "./auth.service";
import {HttpClient} from "@angular/common/http";
import {PageDto, ServerError, ValueLabel} from "../custom";
import {_throw} from 'rxjs/observable/throw';

const toastr = require('toastr/toastr.js');

export type QueryParams = { [param: string]: string | string[] };

@Injectable()
export class RequestService {

    constructor(private httpClient: HttpClient, private authService: AuthService, @Inject(APP_CONFIG) private appConfig: AppConfig) {
    }

    private static catch<T>(err: ServerError): Observable<T> {
        toastr.error(err.error && err.error.message != null ? err.error.message : "An unknown error occurred.");
        return _throw(err);
    }

    public getPaged<T>(relativePath: string, params?: PageDto<T>): Observable<PageDto<T, ValueLabel>> {
        params.content != null && delete params.content;
        params.sortables != null && delete params.sortables;

        // @ts-ignore string cannot be assigned to ValueLabel (in generics).
        return this.get<PageDto<T, ValueLabel>>(relativePath, <QueryParams><any>params)
            .map<PageDto<T, string>, PageDto<T, ValueLabel>>((value: PageDto<T, string>) => {
                // @ts-ignore string cannot be assigned to ValueLabel (in generics).
                value.sortables = value.sortables.map<ValueLabel>(s => {
                    const valueLabel = s.split(",");
                    return {value: valueLabel[0], label: valueLabel[1]} as ValueLabel;
                });

                return value;
            });
    }

    public get<T>(relativePath: string, params?: QueryParams): Observable<T> {
        return this.httpClient
            .get<T>(
                this.appConfig.apiEndpoint + relativePath,
                {params, headers: this.generateDefaultHeaders()})
            .catch<T, T>(RequestService.catch);
    }

    public post<T>(relativePath: string, body: any | null = null): Observable<T> {
        return this.httpClient
            .post<T>(
                this.appConfig.apiEndpoint + relativePath, body,
                {headers: this.generateDefaultHeaders()})
            .catch<T, T>(RequestService.catch);
    }

    public delete<T>(relativePath: string, params?: QueryParams): Observable<T> {
        return this.httpClient
            .delete<T>(
                this.appConfig.apiEndpoint + relativePath,
                {params, headers: this.generateDefaultHeaders()})
            .catch<T, T>(RequestService.catch);
    }

    public formDataPost<T>(relativePath: string, formData: FormData): Observable<T> {
        return this.httpClient
            .post<T>(
                this.appConfig.apiEndpoint + relativePath, formData,
                {headers: this.generateDefaultHeaders(), reportProgress: true})
            .catch<T, T>(RequestService.catch);
    }

    private generateDefaultHeaders(): { Authorization: string } {
        return {'Authorization': this.authService.getToken() || ''};
    }
}
