import {Inject, Pipe, PipeTransform} from '@angular/core';
import {APP_CONFIG, AppConfig} from "../app.config";

@Pipe({
    name: 'cwtReplayLink',
})
export class ReplayLinkPipe implements PipeTransform {

    constructor(@Inject(APP_CONFIG) private appConfig: AppConfig) {
    }

    transform(gameId: number): string | null {
        return gameId
            ? `${this.appConfig.apiEndpoint}binary/game/${gameId}/replay`
            : null;
    }
}
