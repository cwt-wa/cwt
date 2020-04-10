import {Component, ElementRef, ViewChild} from '@angular/core';
import {RequestService} from "../_services/request.service";
import {Toastr} from "../_services/toastr";

@Component({
    selector: 'cwt-admin-extract-stats',
    template: `
        <h1 class="mb-4">Extract stats from replay</h1>

        <div class="row">
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
                            <button class="btn btn-primary mt-3" [disabled]="form.invalid">
                                Extract stats from replay
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

    constructor(private requestService: RequestService, private toastr: Toastr) {
    }

    submit() {
        const formData = new FormData();
        formData.append('replay', this.replayFile.nativeElement.files[0]);
        this.requestService.formDataPost(`binary/game/${this.gameId}/stats`, formData)
            .subscribe(() => this.toastr.success("Replay stats have been persisted."));
    }
}
