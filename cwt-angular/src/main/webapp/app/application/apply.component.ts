import {Component, OnInit} from '@angular/core';
import {RequestService} from "../_services/request.service";

@Component({
    selector: 'cwt-apply',
    template: require('./apply.component.html')
})

export class ApplyComponent implements OnInit {

    private rules: string;

    constructor(private requestService: RequestService) {
    }

    public ngOnInit(): void {
        this.requestService.get<string[]>('rules/current')
            .subscribe(res => this.rules = res[0]);
    }
}
