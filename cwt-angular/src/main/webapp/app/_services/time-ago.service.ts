import {Injectable} from '@angular/core';
import {TimeAgo} from "../custom";

@Injectable()
export class TimeAgoService {

    constructor() {
    }

    timeAgo(subjectDate: Date): TimeAgo {
        const diffToNowInSeconds: number = Math.floor((Date.now() - subjectDate.getTime()) / 1000);

        let intervalForSpecificUnit: number = Math.floor(diffToNowInSeconds / (31536000));
        if (intervalForSpecificUnit > 1) {
            return {value: intervalForSpecificUnit, unit: "YEAR", original: subjectDate};
        }

        intervalForSpecificUnit = Math.floor(diffToNowInSeconds / 2592000);
        if (intervalForSpecificUnit >= 1) {
            return {value: intervalForSpecificUnit, unit: "MONTH", original: subjectDate};
        }

        intervalForSpecificUnit = Math.floor(diffToNowInSeconds / 86400);
        if (intervalForSpecificUnit >= 1) {
            return {value: intervalForSpecificUnit, unit: "DAY", original: subjectDate};
        }

        intervalForSpecificUnit = Math.floor(diffToNowInSeconds / 3600);
        if (intervalForSpecificUnit >= 1) {
            return {value: intervalForSpecificUnit, unit: "HOUR", original: subjectDate};
        }

        intervalForSpecificUnit = Math.floor(diffToNowInSeconds / 60);
        if (intervalForSpecificUnit >= 1) {
            return {value: intervalForSpecificUnit, unit: "MINUTE", original: subjectDate};
        }

        return {value: Math.floor(diffToNowInSeconds), unit: "SECOND", original: subjectDate};
    }
}
