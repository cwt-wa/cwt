import {Component, Input, OnInit} from '@angular/core';
import {Rating, RatingDto, RatingType} from "../custom";
import {RequestService} from "../_services/request.service";
import {AuthService} from "../_services/auth.service";

@Component({
    selector: 'cwt-rating',
    template: require('./rating.component.html'),
    styles: [
            `
            .rating-container {
                height: 38px;
                white-space: nowrap;
            }`,
            `
            .rating-container > div {
                height: 100%;
                display: inline-block;
                padding: 7px;
            }`
    ]
})
export class RatingComponent implements OnInit {
    @Input()
    public ratings: Rating[];
    @Input()
    public typeA: RatingType;
    @Input()
    public typeB: RatingType;
    @Input()
    public gameId: number;

    public aPercent: number;
    public bPercent: number;
    public readonly typeColorAssignment: { [key: string]: string } = {
        "DARKSIDE": 'darkgray',
        "LIGHTSIDE": 'lightgray',
        "LIKE": 'green',
        "DISLIKE": 'red'
    };
    public readonly typeIconAssignment: { [key: string]: string } = {
        "DARKSIDE": 'fa-lightbulb-o',
        "LIGHTSIDE": 'fa-rocket',
        "LIKE": 'fa-thumbs-up',
        "DISLIKE": 'fa-thumbs-down'
    };
    public rating: RatingType;
    private numOfRatingsForA: number;
    private numOfRatingsForB: number;

    public constructor(private requestService: RequestService, private authService: AuthService) {
    }

    public ngOnInit(): void {
        this.calc();
    }

    private calc() {
        this.numOfRatingsForA = this.ratings
            .filter(r => r.type === this.typeA)
            .length;
        this.numOfRatingsForB = this.ratings
            .filter(r => r.type === this.typeB)
            .length;
        const totalRatings: number = this.numOfRatingsForA + this.numOfRatingsForB;

        if (totalRatings !== 0) {
            this.aPercent = this.numOfRatingsForA === 0 ? 0 : (this.numOfRatingsForA / totalRatings) * 100;
            this.bPercent = this.numOfRatingsForB === 0 ? 0 : (this.numOfRatingsForB / totalRatings) * 100;
        } else {
            this.aPercent = 50;
            this.bPercent = 50;
        }
    }

    public rate(): void {
        const payload: RatingDto = {type: this.rating, user: this.authService.getUserFromTokenPayload().id};
        this.requestService.post<Rating>(`game/${this.gameId}/rating`, payload)
            .subscribe(res => this.ratings.push(res) && this.calc());
    }
}
