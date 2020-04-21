import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {BinaryService} from "../_services/binary.service";
import {finalize} from "rxjs/operators";

@Component({
    selector: 'cwt-map',
    styles: [`
      img.map {
        width: 100%;
        border-radius: .3rem;
      }
    `],
    template: `
        <img *ngIf="loading" src="/loading.gif"/>
        <img #mapImage class="map" alt="map" *ngIf="!error" [hidden]="loading">
        <div class="alert alert-danger" *ngIf="error && !loading">
            The map could not be extracted.
        </div>
    `
})
export class MapComponent implements OnInit {

    @Input() gameId: number;
    @Input() map: string;
    @ViewChild('mapImage') mapImage: ElementRef<HTMLImageElement>;

    loading: boolean = true;
    error: boolean = false;

    constructor(private binaryService: BinaryService) {
    }

    ngOnInit(): void {
        console.log(this.gameId, this.map);
        const mapRelativePath = this.map.split('/');
        this.binaryService.getMap(this.gameId, mapRelativePath[mapRelativePath.length - 1])
            .pipe(finalize(() => this.loading = false))
            .subscribe(res => {
                this.mapImage.nativeElement.src = res;
                this.error = false;
            }, () => this.error = true);
    }
}
