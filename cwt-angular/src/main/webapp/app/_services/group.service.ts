import {Injectable} from '@angular/core';
import {GroupLabel} from "../custom";

@Injectable()
export class GroupService {

    public static readonly labels: GroupLabel[] = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'C', 'Y', 'Z'];

    constructor() {
    }
}
