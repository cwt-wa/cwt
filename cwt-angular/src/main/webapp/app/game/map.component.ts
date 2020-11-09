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
        <img *ngIf="loading" [src]="loadingImg"/>
        <a routerLink="/maps" [queryParams]="{terrain: terrain}">
            <img #mapImage class="map" alt="map" *ngIf="!error" [ngbTooltip]="terrain" [hidden]="loading">
        </a>
        <div class="alert alert-danger" *ngIf="error && !loading">
            The map could not be extracted.
        </div>
    `
})
export class MapComponent implements OnInit {

    @Input() gameId: number;
    @Input() map: string;
    @Input() texture: string;
    @ViewChild('mapImage') mapImage: ElementRef<HTMLImageElement>;

    loading: boolean = true;
    loadingImg = require('../../img/loading.gif');
    error: boolean = false;
    terrain?: string;

    constructor(private binaryService: BinaryService) {
    }

    ngOnInit(): void {
        this.terrain = this.texture.split('\\').pop();
        const mapRelativePath = this.map.split('/');
        this.binaryService.getMap(this.gameId, mapRelativePath[mapRelativePath.length - 1])
            .pipe(finalize(() => this.loading = false))
            .subscribe(res => {
                this.mapImage.nativeElement.src = res;
                this.error = false;
            }, () => this.error = true);
    }
}
