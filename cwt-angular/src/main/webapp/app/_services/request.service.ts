import {Inject, Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {APP_CONFIG, AppConfig} from "../app.config";
import {AuthService} from "./auth.service";
import {HttpClient} from "@angular/common/http";
import {PageDto, ValueLabel} from "../custom";
import {map} from "rxjs/operators";

export type QueryParams = { [param: string]: string | string[] };

@Injectable()
export class RequestService {

    constructor(private httpClient: HttpClient, private authService: AuthService,
                @Inject(APP_CONFIG) private appConfig: AppConfig) {
    }

    public get<T>(relativePath: string, params?: QueryParams): Observable<T> {
        return this.httpClient
            .get<T>(
                this.appConfig.apiEndpoint + relativePath,
                {params, headers: this.generateDefaultHeaders()});
    }

    public getBlob(relativePath: string): Observable<Blob> {
        return this.httpClient
            .get(
                this.appConfig.apiEndpoint + relativePath,
                {responseType: 'blob', observe: 'body', headers: this.generateDefaultHeaders()});
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

    public post<T>(relativePath: string, body: any | null = null): Observable<T> {
        return this.httpClient
            .post<T>(
                this.appConfig.apiEndpoint + relativePath, body,
                {headers: this.generateDefaultHeaders()});
    }

    public put<T>(relativePath: string, body: any | null = null): Observable<T> {
        return this.httpClient
            .put<T>(
                this.appConfig.apiEndpoint + relativePath, body,
                {headers: this.generateDefaultHeaders()});
    }

    public delete<T>(relativePath: string, params?: QueryParams): Observable<T> {
        return this.httpClient
            .delete<T>(
                this.appConfig.apiEndpoint + relativePath,
                {params, headers: this.generateDefaultHeaders()});
    }

    public formDataPost<T>(relativePath: string, formData: FormData): Observable<T> {
        return this.httpClient
            .post<T>(
                this.appConfig.apiEndpoint + relativePath, formData,
                {headers: this.generateDefaultHeaders(), reportProgress: true});
    }

    private generateDefaultHeaders(): { Authorization: string } | {} {
        const authToken = this.authService.getToken();
        return authToken == null ? {} : {'Authorization': authToken};
    }
}
