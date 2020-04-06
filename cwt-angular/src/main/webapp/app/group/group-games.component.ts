import {Component, Input} from '@angular/core';
import {GameMinimalDto} from "../custom";

@Component({
    selector: 'cwt-group-games',
    template: require('./group-games.component.html')
})
export class GroupGamesComponent {

    @Input()
    games: GameMinimalDto[];
}
