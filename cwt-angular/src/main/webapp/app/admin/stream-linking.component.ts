import {Component, OnInit} from '@angular/core';
import {GameMinimalDto, StreamDto} from "../custom";
import {RequestService} from "../_services/request.service";
import {FormBuilder, FormGroup} from "@angular/forms";
import {Toastr} from "../_services/toastr";
import {finalize} from "rxjs/operators";

@Component({
    selector: 'cwt-stream-linking',
    template: `
        <div class="float-right">
            <img src="/loading.gif" class="mr-1" *ngIf="linkingLoading"/>
            <button class="btn btn-primary" (click)="triggerLinking()" [disabled]="linkingLoading">
                Trigger automatic linking
            </button>
        </div>

        <h1>Stream linking</h1>
        <p class="lead">Manual stream/game linking.</p>

        <div class="row mt-5">
            <h2 class="col">Game to Stream Linking</h2>
        </div>

        <div class="row mt-3">
            <div class="col">
                <form #gameToStreamForm="ngForm" (ngSubmit)="submitGameToStreamLink(gameToStreamForm.valid)" class="form-row">
                    <div class="col">
                        <input type="text" placeholder="Game ID" required name="gameId"
                               [(ngModel)]="gameToStreamLink.gameId" class="form-control"/>
                    </div>
                    <div class="col">
                        <input type="text" placeholder="Twitch Video ID" required name="videoId"
                               [(ngModel)]="gameToStreamLink.videoId" class="form-control"/>
                    </div>
                    <div class="col">
                        <input type="submit" value="Submit" class="btn btn-primary"/>
                    </div>
                </form>
            </div>
        </div>

        <div class="row mt-5">
            <h2 class="col">Stream to Game Linking</h2>
        </div>

        <div *ngFor="let stream of streams" class="row mt-3">
            <div class="col-12">
                <h5>{{stream.title}}</h5>
                <p>from {{stream.createdAt | cwtDate}}</p>
            </div>
            <div class="col-12 col-sm-6">
                <form [formGroup]="forms[stream.id]" (ngSubmit)="submit(stream)" class="form-row">
                    <div class="col">
                        <input type="text" placeholder="Game ID" required
                               formControlName="gameId" class="form-control"/>
                    </div>
                    <div class="col">
                        <input type="submit" value="Submit" class="btn btn-primary"/>
                    </div>
                </form>
            </div>
        </div>

        <div class="row mt-4">
            <div class="col">
                <hr>
            </div>
        </div>

        <h2 class="mt-4">Linked Streams</h2>
        <p class="lead">Streams that have already been linked.</p>

        <div *ngFor="let stream of linkedStreams" class="row mt-5">
            <div class="col-12">
                <h5>{{stream.title}}</h5>
                <p>from {{stream.createdAt | cwtDate}}</p>
            </div>
            <div class="col-12 col-sm-6">
                <form [formGroup]="forms[stream.id]" (ngSubmit)="submit(stream)" class="form-row">
                    <div class="col">
                        <input type="text" placeholder="Game ID" required
                               formControlName="gameId" class="form-control"/>
                    </div>
                    <div class="col">
                        <input type="submit" value="Update" class="btn btn-primary"/>
                    </div>
                </form>
            </div>
        </div>
    `
})

export class StreamLinkingComponent implements OnInit {

    streams: StreamDto[];
    linkedStreams: StreamDto[];
    games: GameMinimalDto[];
    gameToStreamLink: { gameId: number, videoId: string } = {} as any;
    forms: { [p: string]: FormGroup } = {};
    linkingLoading = false;

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
                this.linkedStreams = res
                    .filter(s => s.game != null)
                    .sort((s1, s2) => (new Date(s2.createdAt).getTime()
                        - new Date(s1.createdAt).getTime()))
                this.buildForms();
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
                    const linked = this.streams.splice(
                        this.streams.findIndex(s => s.id === stream.id), 1);
                    this.linkedStreams.push(...linked);
            });
    }

    triggerLinking(): void {
        this.linkingLoading = true
        this.requestService.post<StreamDto[]>('stream/linking')
            .pipe(finalize(() => this.linkingLoading = false))
            .subscribe(res => {
                this.streams = this.streams.filter(s => res.find(s1 => s.id === s1.id) == null);
                if (res.length === 0) this.toastr.info(`No streams could be linked.`);
                else if (res.length === 1) this.toastr.success(`1 stream was linked.`);
                else if (res.length > 1) this.toastr.success(`${res.length} streams were linked.`);
            });
    }

    submitGameToStreamLink(isValid: boolean) {
        if (!isValid) {
            this.toastr.error("Specify game ID and Twitch video ID.");
            return;
        }
        const {gameId, videoId} = this.gameToStreamLink;
        const confirm = window.confirm(
                `Do you really want to link game with ID ${gameId} to stream with video ${videoId}`);
        if (!confirm) return;
        this.requestService.post(`stream/${videoId}/game/${gameId}/link`)
            .subscribe(() => this.toastr.success("Successfully linked game to stream."));
    }

    private buildForms(): void {
        for (const stream of this.streams) {
            this.forms[stream.id] = this.fb.group({gameId: ['']});
        }
        for (const stream of this.linkedStreams) {
            this.forms[stream.id] = this.fb.group({gameId: ['']});
            this.forms[stream.id].setValue({gameId: stream.game.id.toString()});
        }
    }
}
