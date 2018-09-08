export class Utils {

    constructor() {
    }

    /**
     * Perform Fisher–Yates shuffle on the array.
     *
     * @param array The array to shuffle. Called by reference.
     * @see https://stackoverflow.com/a/20791049/2015430
     */
    public static shuffleArray<T>(array: T[]): T[] {
        let m = array.length, t, i;

        while (m) {
            i = Math.floor(Math.random() * m--);

            t = array[m];
            array[m] = array[i];
            array[i] = t;
        }

        return array;
    }
}
