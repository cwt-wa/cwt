import {Inject, Injectable} from "@angular/core";
import {Observable} from "rxjs/Observable";
import "rxjs/add/operator/map";
import "rxjs/add/operator/catch";
import {APP_CONFIG, AppConfig} from "../app.config";
import {AuthService} from "./auth.service";
import {HttpClient} from "@angular/common/http";
import {ServerError} from "../custom";
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
