import {Component, OnInit} from '@angular/core';
import {Configuration} from "../custom";
import {ConfigurationService} from "../_services/configuration.service";

@Component({
    selector: 'cwt-admin-settings',
    template: require('./admin-settings.component.html')
})

export class AdminSettingsComponent implements OnInit {

    configurations: Configuration[];

    constructor(private configurationService: ConfigurationService) {
    }

    ngOnInit(): void {
        this.configurationService.request()
            .subscribe(res => this.configurations = res.filter(c => c.key !== 'NEWS' && c.key !== 'RULES'));
    }
}
