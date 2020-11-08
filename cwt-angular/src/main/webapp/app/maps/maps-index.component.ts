import {Component, ElementRef, OnInit, QueryList, ViewChildren} from "@angular/core";
import {RequestService} from "../_services/request.service";
import {GameMinimalDto, MapDto, PageDto, TournamentDetailDto} from "../custom";
import {BinaryService} from "../_services/binary.service";
import {finalize} from "rxjs/operators";

interface LoopedMapDto {
    game: GameMinimalDto;
    mapPaths: string[];
}

@Component({
    selector: 'cwt-maps-index',
    styles: [`
        img.hell {
            height: 40px;
            margin-bottom: 45px;
            transform: rotate(4deg);
        }

        img.hell.l {
            height: 40px;
            margin-right: 3px;
            margin-bottom: 45px;
            -webkit-transform: scaleX(-1);
            transform: scaleX(-1) rotate(10deg);
        }

        img.lookup {
            position: absolute;
            left: 60%;
            height: 35px;
            top: 70px;
            transform: rotate(4deg);
            margin-left: 43px;
            z-index: -1;
        }

        img.sickle {
            position: absolute;
            top: -80px;
            left: 60%;
            z-index: -2;
        }

        .crespo {
            font-family: Verdana, Arial, Helvetica, sans-serif;
            line-height: 1.2rem;
            font-weight: bold;
            font-size: 1rem;
            position: absolute;
            color: #80FF80;
            padding: 3px 5px;
            background: #000;
            left: 60%;
            border: 1px solid rgba(211, 211, 211, .5);
            border-radius: 3px;
            top: 35px;
            margin-left: 20px;
        }

        img.map {
            width: 100%
        }

        .texture-select-container {
            position: absolute;
            left: 0;
        }

        @media (max-width: 450px) {
            .texture-select-container {
                top: 70px;
            }
        }
    `],
    template: `
        <div class="position-relative">
            <div class="texture-select-container">
                <select (change)="onChangeTexture($event.target.value)" *ngIf="textures" class="form-control">
                    <option [value]="null">All</option>
                    <option *ngFor="let texture of textures" [value]="texture.value">{{texture.label}}</option>
                </select>
            </div>

            <img [src]="img.sickle" class="sickle" title="sickle">
            <img [src]="img.lookup" class="lookup" title="lookup">
            <div class="crespo">Crespo</div>
        </div>
        <h1 class="text-center all-petite-caps text-nowrap">
            Maps
        </h1>

        <div class="d-flex justify-content-center" *ngIf="loading">
            <img [src]="img.loading"/>
        </div>

        <div class="mt-5 row" *ngIf="!loading">
            <div *ngFor="let map of maps" class="col-12 mb-4 mt-4">
                <h2 class="text-center mb-2">
                    <strong>
                        <cwt-user [username]="map.game.homeUser.username"></cwt-user>
                        <a [routerLink]="['/games', map.game.id]">
                            {{map.game.scoreHome}}–{{map.game.scoreAway}}
                        </a>
                        <cwt-user [username]="map.game.awayUser.username"></cwt-user>
                    </strong>
                </h2>
                <img #mapImage *ngFor="let path of map.mapPaths" [attr.data-map-path]="path" class="map mt-3"
                     title="map">
            </div>
            <div class="col-7 offset-2 mt-5">
                <div class="alert alert-info" *ngIf="!maps?.length">
                    There are no maps yet.
                </div>
            </div>
            <div class="col-12">
                <cwt-paginator [page]="page" (goTo)="goTo($event)"></cwt-paginator>
            </div>
        </div>
    `
})
export class MapsIndexComponent implements OnInit {

    loading: boolean = true;
    tournament: TournamentDetailDto;
    maps: LoopedMapDto[];
    img = {
        horn: require('../../img/horn.png'),
        lookup: require('../../img/worms/lookup.png'),
        sickle: require('../../img/misc/sickle.png'),
        loading: require('../../img/loading.gif')
    }
    textures: { value: string, label: string }[];
    page: PageDto<MapDto> = {start: 0, size: 10} as PageDto<MapDto>;
    @ViewChildren('mapImage') public mapImages: QueryList<ElementRef<HTMLImageElement>>;

    constructor(private requestService: RequestService,
                private binaryService: BinaryService) {
    }

    public ngOnInit(): void {
        this.load();
        this.requestService.get<{string: number}>("map/texture")
            .subscribe(res => this.textures = Object.keys(res).map(t => ({
                value: t,
                label: `${t.split('\\').pop()} (${res[t]})`
            })));
    }

    load(texture: String = null) {
        const queryParams = {
                start: this.page.start,
                size: this.page.size,
                ...(texture != null && {texture})
            };
        this.requestService.getPaged<MapDto>('map', queryParams as unknown as PageDto<MapDto>)
            .pipe(finalize(() => this.loading = false))
            .subscribe(res => {
                this.page = res;
                this.maps = this.structureMaps(res.content)
            });
    }

    structureMaps(maps: MapDto[]) {
        const sorted = maps
            .sort((g1, g2) => new Date(
                g2.game.reportedAt).getTime() - new Date(g1.game.reportedAt).getTime());
        sorted.forEach(map => {
            const mapRelativePath = map.mapPath.split('/');
            this.binaryService.getMap(map.game.id, mapRelativePath[mapRelativePath.length - 1])
                .subscribe(src => {
                    const mapImageElem = this.mapImages.find(
                        item => item.nativeElement.getAttribute('data-map-path') === map.mapPath.toString())
                    mapImageElem.nativeElement.src = src;
                });
        })
        return maps.reduce<LoopedMapDto[]>((acc, curr) => {
            const existingGameEntry = acc.find(g => g.game.id === curr.game.id);
            if (existingGameEntry) {
                existingGameEntry.mapPaths.push(curr.mapPath)
            } else {
                acc.push({game: curr.game, mapPaths: [curr.mapPath]})
            }
            return acc;
        }, []);
    }

    onChangeTexture(texture: String) {
        this.load(texture);
    }

    goTo(start: number) {
        this.page.start = start;
        this.load();
    }
}

