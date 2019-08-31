import {Inject, Injectable} from "@angular/core";
import {APP_CONFIG, AppConfig} from "../app.config";
import {Observable, OperatorFunction} from "rxjs";
import {map} from "rxjs/operators";
import {RequestService} from "./request.service";
import {HttpClient} from "@angular/common/http";
import {AuthService} from "./auth.service";

@Injectable()
export class BinaryService {

    constructor(@Inject(APP_CONFIG) private appConfig: AppConfig,
                private requestService: RequestService,
                private httpClient: HttpClient,
                private authService: AuthService) {
    }

    randomPic() {
        return require('../../img/albino/' + Math.ceil(Math.random() * 14) + '.jpg');
    }

    getUserPhoto(userId: number, hasPicInDb?: boolean): Observable<string> {
        if (this.useDatabaseStorage()) {
            return !hasPicInDb
                ? new Observable<string>(observer => {
                    observer.next(this.randomPic());
                    observer.complete();
                })
                : this.requestService.getBlob(`user/${userId}/photo`)
                    .pipe(this.mapToObjectUrl());
        }

        return this.httpClient
            .get(`${this.appConfig.binaryDataStoreEndpoint}user/${userId}/photo`,
                {responseType: 'blob', observe: 'body'})
            // @ts-ignore
            .pipe(this.mapToObjectUrl());
    }

    saveUserPhoto(userId: number, file: File) {
        const formData = new FormData();
        formData.append('photo', file);

        if (this.useDatabaseStorage()) {
            return this.requestService.formDataPost(`user/${userId}/change-photo`, formData);
        }

        return this.httpClient
            .post(
                `${this.appConfig.binaryDataStoreEndpoint}user/${userId}/photo`, formData,
                {headers: {'Authorization': this.authService.getToken()}, reportProgress: true});
    }

    private useDatabaseStorage() {
        return this.appConfig.binaryDataStoreEndpoint == null;
    }

    private mapToObjectUrl(): OperatorFunction<Blob, string> {
        // @ts-ignore
        return map(value => (window.URL || window.webkitURL).createObjectURL(value));
    }
}
