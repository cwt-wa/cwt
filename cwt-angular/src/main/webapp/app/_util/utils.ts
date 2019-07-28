export class Utils {

    constructor() {
    }

    /**
     * Perform Fisher–Yates shuffle on the array.
     *
     * @param array The array to shuffle. Called by reference.
     * @see https://stackoverflow.com/a/20791049/2015430
     */
    public shuffleArray<T>(array: T[]): T[] {
        let m = array.length, t, i;

        while (m) {
            i = Math.floor(Math.random() * m--);

            t = array[m];
            array[m] = array[i];
            array[i] = t;
        }

        return array;
    }

    /**
     * Merge two arrays distinctively by their discriminator.
     * In case of duplicates, the item from the first array is kept.
     */
    public mergeDistinctBy<T extends ({[key: string]: any})>(arr1: T[], arr2: T[], discriminator: string): T[] {
        return arr2.reduce((mergerArr, candidateItem) => {
            if (mergerArr.find(mergeArrItem => mergeArrItem[discriminator] === candidateItem[discriminator]) == null) {
                mergerArr.push(candidateItem);
            }
            return mergerArr;
        }, arr1);
    }
}
