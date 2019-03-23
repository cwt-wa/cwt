import {Inject, Injectable} from "@angular/core";
import {Observable, throwError} from "rxjs";
import {APP_CONFIG, AppConfig} from "../app.config";
import {AuthService} from "./auth.service";
import {HttpClient} from "@angular/common/http";
import {PageDto, ServerError, ValueLabel} from "../custom";
import {catchError, map} from "rxjs/operators";

const toastr = require('toastr/toastr.js');

export type QueryParams = { [param: string]: string | string[] };

@Injectable()
export class RequestService {

    constructor(private httpClient: HttpClient, private authService: AuthService, @Inject(APP_CONFIG) private appConfig: AppConfig) {
    }

    private static catch(err: ServerError): Observable<never> {
        toastr.error(err.error && err.error.message != null ? err.error.message : "An unknown error occurred.");
        return throwError(err);
    }

    public getPaged<T>(relativePath: string, params?: PageDto<T>): Observable<PageDto<T, ValueLabel>> {
        params.content != null && delete params.content;
        params.sortables != null && delete params.sortables;

        return this.get<PageDto<T, string>>(relativePath, <QueryParams><any>params).pipe(
            map<PageDto<T, string>, PageDto<T, ValueLabel>>((value: PageDto<T, any>) => {
                value.sortables = value.sortables.map<ValueLabel>(s => {
                    const valueLabel = (s as string).split(",");
                    return {value: valueLabel[0], label: valueLabel[1]};
                });

                return value;
            }));
    }

    public get<T>(relativePath: string, params?: QueryParams): Observable<T> {
        return this.httpClient
            .get<T>(
                this.appConfig.apiEndpoint + relativePath,
                {params, headers: this.generateDefaultHeaders()}).pipe(
            catchError(RequestService.catch));
    }

    public post<T>(relativePath: string, body: any | null = null): Observable<T> {
        return this.httpClient
            .post<T>(
                this.appConfig.apiEndpoint + relativePath, body,
                {headers: this.generateDefaultHeaders()}).pipe(
            catchError(RequestService.catch));
    }

    public put<T>(relativePath: string, body: any | null = null): Observable<T> {
        return this.httpClient
            .put<T>(
                this.appConfig.apiEndpoint + relativePath, body,
                {headers: this.generateDefaultHeaders()}).pipe(
            catchError(RequestService.catch));
    }

    public delete<T>(relativePath: string, params?: QueryParams): Observable<T> {
        return this.httpClient
            .delete<T>(
                this.appConfig.apiEndpoint + relativePath,
                {params, headers: this.generateDefaultHeaders()}).pipe(
            catchError(RequestService.catch));
    }

    public formDataPost<T>(relativePath: string, formData: FormData): Observable<T> {
        return this.httpClient
            .post<T>(
                this.appConfig.apiEndpoint + relativePath, formData,
                {headers: this.generateDefaultHeaders(), reportProgress: true}).pipe(
            catchError(RequestService.catch));
    }

    private generateDefaultHeaders(): { Authorization: string } | {} {
        const authToken = this.authService.getToken();
        return authToken == null ? {} : {'Authorization': authToken};
    }
}
