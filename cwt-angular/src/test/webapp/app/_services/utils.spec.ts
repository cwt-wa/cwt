import {Utils} from "../../../../main/webapp/app/_util/utils";

describe('Utils', () => {
    it('can merge two arrays distinctively by a given discriminator.', () => {
        const utils = new Utils();

        const actual1 = utils.mergeDistinctBy(
            [{id: 1, text: 'Example for 1'}],
            [{id: 1, text: 'Example for 1 again'}, {id: 2, text: 'Example for 2'}],
            'id'
        );

        console.log(actual1);
        expect(actual1).toEqual([{id: 1, text: 'Example for 1'}, {id: 2, text: 'Example for 2'}]);

        const actual2 = utils.mergeDistinctBy(
            [{id: 1, text: 'Example for 1'}],
            [{id: 1, text: 'Example for 1 again'}, {id: 2, text: 'Example for 2'}],
            'text'
        );

        console.log(actual2);
        expect(actual2).toEqual([{id: 1, text: 'Example for 1'}, {id: 1, text: 'Example for 1 again'}, {id: 2, text: 'Example for 2'}])
    });

    it('can parse Twitch duration formats', () => {
        const utils = new Utils();
        expect(utils.parseTwitchDurationFormat('1m13s')).toBe(73);
        expect(utils.parseTwitchDurationFormat('3h12m9s')).toBe(11529);
        expect(utils.parseTwitchDurationFormat('40m13s')).toBe(2413);
    });
});
