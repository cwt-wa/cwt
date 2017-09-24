import {Inject, Injectable} from "@angular/core";
import {Headers, Http, RequestMethod, RequestOptionsArgs, Response, URLSearchParams} from "@angular/http";
import {Observable} from "rxjs/Observable";
import "rxjs/add/operator/map";
import "rxjs/add/operator/catch";
import {APP_CONFIG, AppConfig} from "../app.config";
import {AuthService} from "./auth.service";

export type QueryParams = string | URLSearchParams | { [key: string]: any | any[]; } | null;

@Injectable()
export class RequestService {

    constructor(private http: Http, private authService: AuthService, @Inject(APP_CONFIG) private appConfig: AppConfig) {
    }

    public get<T>(relativePath: string, params?: QueryParams): Observable<T> {
        return this.request(relativePath, {params, method: RequestMethod.Get});
    }

    public post<T>(relativePath: string, body?: any, params?: QueryParams): Observable<T> {
        return this.request(relativePath, {params, body, method: RequestMethod.Post});
    }

    public delete<T>(relativePath: string, params?: QueryParams): Observable<T> {
        return this.request(relativePath, {params, method: RequestMethod.Delete});
    }

    private request<T>(relativePath: string, options: RequestOptionsArgs = {}): Observable<T> {
        options.headers = options.headers || new Headers();

        const token = this.authService.getToken();
        token && options.headers.append('Authorization', token);

        return this.http.request(this.appConfig.apiEndpoint + relativePath, options)
            .map(this.extractData)
            .catch(this.handleError);
    }

    private extractData(res: Response): any {
        return res.json();
    }

    private handleError(error: Response | any) {
        let errMsg: string;
        if (error instanceof Response) {
            const body = error.json() || '';
            const err = body.error || JSON.stringify(body);
            errMsg = `${error.status} - ${error.statusText || ''} ${err}`;
        } else {
            errMsg = error.message ? error.message : error.toString();
        }
        return Observable.throw(errMsg);
    }

}
