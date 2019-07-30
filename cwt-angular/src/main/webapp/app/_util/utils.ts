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

    /**
     * Parse a Twitch duration which looks like 2h30m45s to just a seconds number.
     */
    public parseTwitchDurationFormat(str: string) {
        const split = str.split(/[^\d]/).filter(s => s).map(s => parseInt(s));
        let seconds = 0;
        seconds += split[split.length - 1]; // seconds
        if (split[split.length - 2] != null) seconds += split[split.length - 2] * 60; // minutes
        if (split[split.length - 3] != null) seconds += split[split.length - 3] * 60 * 60; // hours
        return seconds;
    }
}
