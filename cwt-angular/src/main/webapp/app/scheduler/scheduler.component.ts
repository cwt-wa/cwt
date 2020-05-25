import {Component, OnInit, ViewChild} from "@angular/core";
import {AuthService} from "../_services/auth.service";
import {RequestService} from "../_services/request.service";
import {ChannelDto, JwtUser, ScheduleCreationDto, ScheduleDto, User} from "../custom";
import {NgForm} from "@angular/forms";
import {Toastr} from "../_services/toastr";

@Component({
    selector: 'cwt-scheduler',
    template: require('./scheduler.component.html'),
})
export class SchedulerComponent implements OnInit {

    @ViewChild('scheduleForm') scheduleForm: NgForm;

    authUser: JwtUser;
    schedules: ScheduleDto[];
    newSchedule: ScheduleCreationDto = {opponent: null} as ScheduleCreationDto;
    remainingOpponents: User[];
    readonly minAppointmentDateTime: Date = new Date();
    authUserChannel: ChannelDto;

    constructor(private authService: AuthService, private requestService: RequestService,
                private toastr: Toastr) {
    }

    public async ngOnInit() {
        this.authUser = await this.authService.authState;

        this.requestService.get<ScheduleDto[]>('schedule').subscribe(res => {
            this.schedules = res.sort((a, b) => new Date(a.appointment).getTime() - new Date(b.appointment).getTime());

            if (this.authUser != null) {
                this.requestService
                    .get<User[]>(`user/${this.authUser.id}/remaining-opponents`)
                    .subscribe(remainingOpponents => this.filterByAlreadyScheduledAgainst(remainingOpponents));
            }
        });

        if (this.authUser != null) {
            this.requestService.get<ChannelDto[]>('channel', {user: `${this.authUser.id}`})
                .subscribe(res => this.authUserChannel = res[0])
        }
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
                this.schedules.sort((a, b) => new Date(a.appointment).getTime() - new Date(b.appointment).getTime());
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
        this.remainingOpponents.length === 1 && (this.newSchedule.opponent = this.remainingOpponents[0].id);
    }

    scheduleStream(schedule: ScheduleDto) {
        const idxOfAlreadyScheduledStream = schedule.streams.findIndex(s => s.id === this.authUserChannel.id);
        const channelAlreadyScheduled = idxOfAlreadyScheduledStream !== -1;

        if (channelAlreadyScheduled) {
            this.requestService
                .delete<ChannelDto>(`schedule/${schedule.id}/channel/${this.authUserChannel.id}`)
                .subscribe(() => {
                    this.toastr.success("Successfully deleted scheduled stream.");
                    schedule.streams.splice(idxOfAlreadyScheduledStream, 1);
                    // this.schedules[this.schedules.findIndex(s => s.id === schedule.id)].streams.push(res);
                });
        } else {
            this.requestService
                .post<ChannelDto>(`schedule/${schedule.id}/channel/${this.authUserChannel.id}`)
                .subscribe(res => {
                    this.toastr.success("Successfully scheduled stream for game.");
                    schedule.streams.push(res);
                });
        }
    }
}
