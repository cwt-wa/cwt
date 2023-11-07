import {Component, ElementRef, ViewChild} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {Toastr} from "../_services/toastr";
import {finalize} from "rxjs/operators";

@Component({
    selector: 'cwt-admin-extract-stats',
    template: `
        <h1 class="">Replay Stats</h1>
        <p class="lead">Upload a Zip archive of *.WAgame replay files to trigger extraction of stats manually.</p>

        <div class="row mt-4">
            <div class="col-lg-6 col-md-8 col-sm-10">
                <form #form="ngForm" (submit)="submit()" enctype="multipart/form-data">
                    <div class="form-row">
                        <div class="form-group">
                            <label for="gameId" class="sr-only">Game ID</label>
                            <input type="text" placeholder="Enter game ID…"
                                   [(ngModel)]="gameId" id="gameId" required
                                   name="gameId" class="form-control"/>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="col">
                            <label for="replayFile" class="sr-only">Zipped replay</label>
                            <input type="file" #replayFile class="form-control-file" id="replayFile"
                                   [(ngModel)]="replay" required name="replayFile" accept=".zip">
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="col">
                            <button class="btn btn-primary mt-3" [disabled]="form.invalid || submitting">
                                <span *ngIf="!submitting">Extract stats from replay</span>
                                <img src="../../img/loading.gif" class="loading" *ngIf="submitting"/>
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    `
})
export class AdminExtractStatsComponent {

    @ViewChild('replayFile') replayFile: ElementRef<HTMLInputElement>;
    replay: any;
    gameId: number;
    submitting: boolean = false;

    constructor(private requestService: RequestService, private toastr: Toastr) {
    }

    submit() {
        const formData = new FormData();
        formData.append('replay', this.replayFile.nativeElement.files[0]);
        this.submitting = true;
        this.requestService.formDataPost(`binary/game/${this.gameId}/stats`, formData)
            .pipe(finalize(() => this.submitting = false))
            .subscribe(() => this.toastr.success("Replay stats have been persisted."));
    }
}
