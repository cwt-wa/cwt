import {Component, OnInit} from '@angular/core';
import {ConfigurationService} from "../_services/configuration.service";
import {Configuration} from "../custom";

@Component({
    selector: 'cwt-write-rules',
    template: require('./help.component.html')
})
export class HelpComponent implements OnInit {

    rules: Configuration;

    constructor(private configurationService: ConfigurationService) {
    }

    ngOnInit(): void {
        this.configurationService.requestByKeys("RULES")
            .subscribe(res => this.rules = res[0]);
    }
}
