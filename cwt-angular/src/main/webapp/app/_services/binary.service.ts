import {Injectable} from "@angular/core";
import {Observable, OperatorFunction} from "rxjs";
import {map} from "rxjs/operators";
import {RequestService} from "./request.service";

@Injectable()
export class BinaryService {

    constructor(private requestService: RequestService) {
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

        return this.requestService.formDataPost(`binary/user/${userId}/photo`, formData);
    }

    deleteUserPhoto(userId: number): Observable<void> {
        return this.requestService.delete<void>(`binary/user/${userId}/photo`);
    }

    private mapToObjectUrl(): OperatorFunction<Blob, string> {
        // @ts-ignore
        return map(value => (window.URL || window.webkitURL).createObjectURL(value));
    }
}
