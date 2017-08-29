import {Component, Input, OnInit} from '@angular/core';
import {Rating, RatingType} from "../custom";

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

    public aPercent: number;
    public bPercent: number;
    public readonly typeColorAssignment: { [key: string]: string } = {
        "DARKSIDE": 'darkgray',
        "LIGHTSIDE": 'lightgray',
        "LIKE": 'green',
        "DISLIKE": 'red'
    };
    private numOfRatingsForA: number;
    private numOfRatingsForB: number;

    public ngOnInit(): void {
        this.numOfRatingsForA = this.ratings
            .filter(r => r.type === this.typeA)
            .length;
        this.numOfRatingsForB = this.ratings
            .filter(r => r.type === this.typeB)
            .length;
        const totalRatings: number = this.numOfRatingsForA + this.numOfRatingsForB;

        if (totalRatings !== 0) {
            this.aPercent = this.numOfRatingsForA === 0 ? 0 : totalRatings / this.numOfRatingsForA;
            this.bPercent = this.numOfRatingsForB === 0 ? 0 : totalRatings / this.numOfRatingsForB;
        } else {
            this.aPercent = 50;
            this.bPercent = 50;
        }
    }
}
