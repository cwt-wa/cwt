import {Component, OnInit} from "@angular/core";
import {AuthService} from "../_services/auth.service";
import {RequestService} from "../_services/request.service";
import {JwtUser, ScheduleCreationDto, ScheduleDto} from "../custom";

@Component({
    selector: 'cwt-scheduler',
    template: require('./scheduler.component.html')
})
export class SchedulerComponent implements OnInit {

    authUser: JwtUser;
    schedules: ScheduleDto[];
    newSchedule: ScheduleCreationDto = {opponent: null} as ScheduleCreationDto;

    constructor(private authService: AuthService, private requestService: RequestService) {
    }

    public ngOnInit(): void {
        this.authUser = this.authService.getUserFromTokenPayload();

        this.requestService.get<ScheduleDto[]>('schedule')
            .subscribe(res => this.schedules = res);
    }
}
