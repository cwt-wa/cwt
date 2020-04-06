import {Injectable} from "@angular/core";
import {RequestService} from "./request.service";
import {BehaviorSubject, Subject} from "rxjs";

@Injectable()
export class CanReportService {

    canReport: Subject<boolean> = new BehaviorSubject(false);

    constructor(private requestService: RequestService) {
    }

    requestInitialValue(authUserId: number) {
        this.requestService.get<boolean>(`user/${authUserId}/can-report`)
            .subscribe(res => this.canReport.next(res));
    }
}
