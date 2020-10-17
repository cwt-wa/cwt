import {Component, OnInit} from "@angular/core";
import {RequestService} from "../_services/request.service";
import {CurrentTournamentService} from "../_services/current-tournament.service";
import {MapDto, TournamentDetailDto} from "../custom";

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
    `
})
export class MapsIndexComponent implements OnInit {

    tournament: TournamentDetailDto;
    maps: MapDto[];
    img = {
        horn: require(`../../img/horn.png`),
        lookup: require(`../../img/worms/lookup.png`),
        sickle: require(`../../img/misc/sickle.png`),
    }

    constructor(private requestService: RequestService,
                private currentTournamentService: CurrentTournamentService) {
    }

    public ngOnInit(): void {
        this.currentTournamentService.value.then(tournament => {
            this.tournament = tournament
            this.requestService.get<MapDto[]>(`tournament/${tournament.id}/maps`)
                .subscribe(res => this.maps = res)

        })
    }
}
