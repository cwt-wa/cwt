import {Component, OnInit, ViewChild} from "@angular/core";
import {AuthService} from "../_services/auth.service";
import {RequestService} from "../_services/request.service";
import {JwtUser, ScheduleCreationDto, ScheduleDto, User} from "../custom";
import {NgForm} from "@angular/forms";
import {Toastr} from "../_services/toastr";

@Component({
    selector: 'cwt-scheduler',
    template: require('./scheduler.component.html')
})
export class SchedulerComponent implements OnInit {

    @ViewChild('scheduleForm') scheduleForm: NgForm;

    authUser: JwtUser;
    schedules: ScheduleDto[];
    newSchedule: ScheduleCreationDto = {opponent: null} as ScheduleCreationDto;
    remainingOpponents: User[];
    readonly minAppointmentDateTime: Date = new Date();

    constructor(private authService: AuthService, private requestService: RequestService,
                private toastr: Toastr) {
    }

    public ngOnInit(): void {
        this.authUser = this.authService.getUserFromTokenPayload();

        const remainingOpponentsObservable = this.requestService
            .get<User[]>(`user/${this.authUser.id}/remaining-opponents`);

        this.requestService.get<ScheduleDto[]>('schedule').subscribe(res => {
            this.schedules = res;

            remainingOpponentsObservable.subscribe(remainingOpponents =>
                this.filterByAlreadyScheduledAgainst(remainingOpponents));
        });

        remainingOpponentsObservable
            .subscribe(res => {
                this.filterByAlreadyScheduledAgainst(res);
                this.remainingOpponents.length === 1 && (this.newSchedule.opponent = this.remainingOpponents[0].id);
            });
    }

    public submit(valid: boolean) {
        if (!valid) {
            if (this.scheduleForm.control.get('appointment').errors.cwtDateTimeInput) {
                this.toastr.error('Invalid date/time format.');
            } else if (this.scheduleForm.control.get('appointment').errors.cwtDateTimeInputAfter) {
                this.toastr.error('You mustn’t schedule in the past.');
            }
            return;
        }

        this.newSchedule.author = this.authUser.id;

        this.requestService.post<ScheduleDto>('schedule', this.newSchedule)
            .subscribe(res => {
                this.schedules.push(res);
                this.newSchedule = {opponent: null} as ScheduleCreationDto;
                this.filterByAlreadyScheduledAgainst(this.remainingOpponents);
            });
    }

    deleteSchedule(schedule: ScheduleDto) {
        this.requestService.delete(`schedule/${schedule.id}`)
            .subscribe(() => {
                this.toastr.success('Successfully deleted schedule.');
                this.schedules.splice(this.schedules.findIndex(s => s.id === schedule.id), 1);

                // @ts-ignore
                this.remainingOpponents.push([schedule.homeUser, schedule.awayUser].find(u => u.id !== this.authUser.id));

                this.remainingOpponents.length === 1 && (this.newSchedule.opponent = this.remainingOpponents[0].id);
            });
    }

    private filterByAlreadyScheduledAgainst(remainingOpponents: User[]) {
        const opponentsAlreadyScheduledAgainst = this.schedules.reduce((prev, curr) => {
            if (curr.homeUser.id === this.authUser.id) prev.push(curr.awayUser.id);
            else if (curr.awayUser.id === this.authUser.id) prev.push(curr.homeUser.id);
            return prev;
        }, []);
        this.remainingOpponents = remainingOpponents.filter(rO => opponentsAlreadyScheduledAgainst.indexOf(rO.id) === -1);
    }
}
