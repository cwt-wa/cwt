import {Component, OnInit} from '@angular/core';
import {GameMinimalDto, StreamDto} from "../custom";
import {RequestService} from "../_services/request.service";
import {FormBuilder, FormGroup} from "@angular/forms";
import {Toastr} from "../_services/toastr";

@Component({
    selector: 'cwt-stream-linking',
    template: `
        <h1>Stream linking</h1>
        <p class="lead">Link streams to games manually if automated linking couldn’t do it</p>

        <div *ngFor="let stream of streams" class="row mt-5">
            <div class="col-12">
                <h5>{{stream.title}}</h5>
                <p>from {{stream.createdAt | cwtDate}}</p>
            </div>
            <div class="col-12 col-sm-6">
                <form [formGroup]="forms[stream.id]" (ngSubmit)="submit(stream)" class="form-row">
                    <div class="col">
                        <input type="text" placeholder="Game ID" required
                               [formControlName]="'gameId'" class="form-control"/>
                    </div>
                    <div class="col">
                        <input type="submit" value="Submit" class="btn btn-primary"/>
                    </div>
                </form>
            </div>
        </div>
    `
})

export class StreamLinkingComponent implements OnInit {

    streams: StreamDto[];
    games: GameMinimalDto[];
    forms: { [p: string]: FormGroup } = {};

    constructor(private requestService: RequestService,
                private fb: FormBuilder,
                private toastr: Toastr) {
    }

    ngOnInit(): void {
        this.requestService.get<StreamDto[]>('stream')
            .subscribe(res => {
                this.streams = res
                    .filter(s => s.game == null)
                    .sort((s1, s2) => (new Date(s2.createdAt).getTime()
                        - new Date(s1.createdAt).getTime()))
                this.buildForm();
            })
    }

    submit(stream: StreamDto): void {
        if (this.forms[stream.id].invalid) {
            this.toastr.error("Please enter a game ID.");
            return;
        }
        const gameId = this.forms[stream.id].get('gameId').value;
        const confirm = window.confirm(`Link game ${gameId} to stream\n“${stream.title}”?`);
        if (!confirm) return;
        this.requestService.post(`stream/${stream.id}/game/${gameId}/link`)
            .subscribe(() => {
                this.toastr.success("Successfully linked game.");
                this.streams.splice(this.streams.findIndex(s => s.id === stream.id), 1);
            });
    }

    private buildForm(): void {
        for (const stream of this.streams) {
            this.forms[stream.id] = this.fb.group({
                gameId: [''],
                submit: [''],
            })
        }
    }
}
