import {inject, TestBed} from '@angular/core/testing';
import {TimeAgoService} from '../../../main/webapp/app/_services/time-ago.service';
import {TimeAgo} from "../../../main/webapp/app/custom";

describe('TimeAgoService', () => {

    beforeEach(() => {

        TestBed.configureTestingModule({
            providers: [
                TimeAgoService,
            ]
        });
    });

    it('works well', inject([TimeAgoService], (timeAgoService: TimeAgoService) => {
        let timeAgo: TimeAgo;
        const fakeNow: Date = new Date('2015-05-05T15:05:05.555Z');
        spyOn<DateConstructor>(Date, "now").and.callFake(() => fakeNow);

        timeAgo = timeAgoService.timeAgo(new Date('2015-05-05T15:05:04.555Z'));
        expect(timeAgo.unit).toEqual("SECOND");
        expect(timeAgo.value).toEqual(1);

        timeAgo = timeAgoService.timeAgo(new Date('2015-05-05T15:04:05.555Z'));
        expect(timeAgo.unit).toEqual("MINUTE");
        expect(timeAgo.value).toEqual(1);

        timeAgo = timeAgoService.timeAgo(new Date('2015-05-05T15:03:05.555Z'));
        expect(timeAgo.unit).toEqual("MINUTE");
        expect(timeAgo.value).toEqual(2);

        timeAgo = timeAgoService.timeAgo(new Date('2015-05-05T14:03:05.555Z'));
        expect(timeAgo.unit).toEqual("HOUR");
        expect(timeAgo.value).toEqual(1);

        timeAgo = timeAgoService.timeAgo(new Date('2015-05-05T14:06:05.555Z'));
        expect(timeAgo.unit).toEqual("MINUTE");
        expect(timeAgo.value).toEqual(59);

        timeAgo = timeAgoService.timeAgo(new Date('2011-05-05T14:06:05.555Z'));
        expect(timeAgo.unit).toEqual("YEAR");
        expect(timeAgo.value).toEqual(4);
    }));
});
