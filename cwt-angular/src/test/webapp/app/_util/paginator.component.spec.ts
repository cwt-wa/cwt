import {PaginatorComponent} from "../../../../main/webapp/app/_util/paginator.component";

describe('PaginatorComponent', () => {
    it('suggests the expected starts', () => {
        const component = new PaginatorComponent();

        expect(component.getSuggestedStarts(0, 50, 7))
            .toEqual([0, 1, 2, 3, 4, 5, 6]);

        expect(component.getSuggestedStarts(48, 50, 7))
            .toEqual([43, 44, 45, 46, 47, 48, 49]);

        expect(component.getSuggestedStarts(10, 20, 7))
            .toEqual([7, 8, 9, 10, 11, 12, 13]);

        expect(component.getSuggestedStarts(3, 5, 7))
            .toEqual([0, 1, 2, 3, 4]);

        expect(component.getSuggestedStarts(9, 10, 7))
            .toEqual([3, 4, 5, 6, 7, 8, 9]);

        expect(() => component.getSuggestedStarts(5, 2, 7))
            .toThrowError("currentStart may not be greater or equal to totalPages.");

        expect(() => component.getSuggestedStarts(4, 10, 6))
            .toThrowError("oddMaxSuggestions may only be an odd number.");
    });
});
