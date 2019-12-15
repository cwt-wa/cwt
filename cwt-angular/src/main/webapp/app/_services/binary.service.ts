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

    getUserPhoto(userId: number): Observable<string> {
        return this.requestService
            .getBlob(`binary/user/${userId}/photo`)
            .pipe(this.mapToObjectUrl());
    }

    saveUserPhoto(userId: number, file: File) {
        const formData = new FormData();
        formData.append('photo', file);

        if (this.useDatabaseStorage()) {
            return this.requestService.formDataPost(`user/${userId}/photo`, formData);
        }

        return this.httpClient
            .post(
                `${this.appConfig.binaryDataStoreEndpoint}user/${userId}/photo`, formData,
                {headers: {'Authorization': this.authService.getToken()}, reportProgress: true});
    }

    deleteUserPhoto(userId: number): Observable<void> {
        const opts = {headers: {'Authorization': this.authService.getToken()}};
        return this.useDatabaseStorage()
            ? this.httpClient.delete<void>(`${this.appConfig.apiEndpoint}user/${userId}/photo`, opts)
            : this.httpClient.delete<void>(`${this.appConfig.binaryDataStoreEndpoint}user/${userId}/photo`, opts);
    }

    getReplay(gameId: number, replayExistsInDb: boolean) {
        if (replayExistsInDb) {
            return this.appConfig.apiEndpoint + `game/${gameId}/replay`;
        }

        if (!this.useDatabaseStorage()) {
            return this.appConfig.binaryDataStoreEndpoint + `game/${gameId}/replay`;
        }

        return null;
    }

    private useDatabaseStorage() {
        return this.appConfig.binaryDataStoreEndpoint == null;
    }

    private mapToObjectUrl(): OperatorFunction<Blob, string> {
        // @ts-ignore
        return map(value => (window.URL || window.webkitURL).createObjectURL(value));
    }
}
