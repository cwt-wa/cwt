import {Component, ElementRef, OnInit, QueryList, ViewChildren} from "@angular/core";
import {RequestService} from "../_services/request.service";
import {CurrentTournamentService} from "../_services/current-tournament.service";
import {GameMinimalDto, MapDto, TournamentDetailDto} from "../custom";
import {BinaryService} from "../_services/binary.service";
import {finalize} from "rxjs/operators";

interface HellMapDto {
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
            top: 109px;
            transform: rotate(4deg);
            margin-left: 43px;
            z-index: -1;
        }

        img.sickle {
            position: absolute;
            top: -40px;
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
            top: 75px;
            margin-left: 20px;
        }

        blockquote.hellisnice {
            padding-top: 90px;
            text-indent: -0.5em;
        }
        
        .other-maps {
            position: absolute;
            right: 0;
        }

        img.map {
            width: 100%
        }

        @media (min-width: 576px) {
            blockquote.hellisnice {
                padding-right: 220px;
                padding-top: 0;
            }
        }

        @media (min-width: 768px) {
            blockquote.hellisnice {
                padding-right: 380px;
                padding-top: 0;
            }
        }

        @media (min-width: 992px) {
            blockquote.hellisnice {
                padding-right: 480px;
                padding-top: 0;
                padding-left: 100px;
            }
        }

        @media (min-width: 1200px) {
            blockquote.hellisnice {
                padding-right: 600px;
                padding-top: 0;
            }
        }
    `],
    template: `
        <div class="position-relative">
            <a routerLink="/maps" class="other-maps">Other maps</a>
            <img [src]="img.sickle" class="sickle" title="sickle">
            <img [src]="img.lookup" class="lookup" title="lookup">
            <div class="crespo">Crespo</div>
        </div>
        <h1 class="text-center all-petite-caps text-nowrap">
            <img [src]="img.horn" class="hell l" title="horn">
            Hell
            <img [src]="img.horn" class="hell" title="horn">
        </h1>

        <blockquote class="hellisnice">
            “But Hell is so cool! Especially Retro Hell. Pitchforks, demons and so on, love it.”
        </blockquote>

        <div class="d-flex justify-content-center" *ngIf="loading">
            <img [src]="img.loading"/>
        </div>

        <div class="mt-5 row" *ngIf="!loading">
            <div *ngFor="let map of maps" class="col-12 mb-5">
                <h2 class="text-center mb-2">
                    <strong>
                        <cwt-user [username]="map.game.homeUser.username"></cwt-user>
                        <a [routerLink]="['/games', map.game.id]">
                            {{map.game.scoreHome}}–{{map.game.scoreAway}}
                        </a>
                        <cwt-user [username]="map.game.awayUser.username"></cwt-user>
                    </strong>
                    played&nbsp;Hell
                    {map.mapPaths.length, select,
                            1 {}
                            2 {twice}
                            other {{{map.mapPaths.length}}&nbsp;times}
                            }
                </h2>
                <img #mapImage *ngFor="let path of map.mapPaths" [attr.data-map-path]="path" class="map mt-2"
                     title="hell map">
            </div>
            <div class="col-7 offset-2 mt-3">
                <div class="alert alert-info" *ngIf="!maps?.length">
                    No one has dared to play Hell in the 2020 playoffs yet.
                </div>
            </div>
        </div>
    `
})
export class HellMapsComponent implements OnInit {

    loading: boolean = true;
    tournament: TournamentDetailDto;
    maps: HellMapDto[];
    img = {
        horn: require('../../img/horn.png'),
        lookup: require('../../img/worms/lookup.png'),
        sickle: require('../../img/misc/sickle.png'),
        loading: require('../../img/loading.gif')
    }
    @ViewChildren('mapImage') public mapImages: QueryList<ElementRef<HTMLImageElement>>;

    constructor(private requestService: RequestService,
                private currentTournamentService: CurrentTournamentService,
                private binaryService: BinaryService) {
    }

    public ngOnInit(): void {
        this.currentTournamentService.value.then(tournament => {
            this.tournament = tournament
            this.requestService.get<MapDto[]>(`tournament/${tournament.id}/maps`)
                .pipe(finalize(() => this.loading = false))
                .subscribe(res => {
                    const maps = res
                        .filter(g => new Date(g.game.reportedAt).getTime() > 1602892800000)
                        .filter(g => g.texture != null && g.texture.split('\\').pop().toLowerCase() === "hell")
                        .sort((g1, g2) => new Date(
                            g2.game.reportedAt).getTime() - new Date(g1.game.reportedAt).getTime());
                    maps.forEach(map => {
                        const mapRelativePath = map.mapPath.split('/');
                        this.binaryService.getMap(map.game.id, mapRelativePath[mapRelativePath.length - 1])
                            .subscribe(res => {
                                const mapImageElem = this.mapImages.find(
                                    item => item.nativeElement.getAttribute('data-map-path') === map.mapPath.toString())
                                mapImageElem.nativeElement.src = res;
                            });
                    })
                    this.maps = maps
                        .reduce<HellMapDto[]>((acc, curr) => {
                            const existingGameEntry = acc.find(g => g.game.id === curr.game.id);
                            if (existingGameEntry) {
                                existingGameEntry.mapPaths.push(curr.mapPath)
                            } else {
                                acc.push({game: curr.game, mapPaths: [curr.mapPath]})
                            }
                            return acc;
                        }, []);
                })
        })
    }
}
