import {Pipe, PipeTransform} from '@angular/core';
import {BinaryService} from "../_services/binary.service";

@Pipe({
    name: 'cwtReplayLink',
})
export class ReplayLinkPipe implements PipeTransform {

    constructor(private binaryService: BinaryService) {
    }

    transform(gameId: number, replayExistsInDb: boolean): string | null {
        if (!gameId) return null;
        return this.binaryService.getReplay(gameId, replayExistsInDb);
    }
}
