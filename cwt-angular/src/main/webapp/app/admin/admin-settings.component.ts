import {Component, OnInit} from '@angular/core';
import {Configuration, ConfigurationKey} from "../custom";
import {ConfigurationService} from "../_services/configuration.service";
import {RequestService} from "../_services/request.service";

const toastr = require('toastr/toastr.js');

@Component({
    selector: 'cwt-admin-settings',
    template: require('./admin-settings.component.html')
})

export class AdminSettingsComponent implements OnInit {

    configurations: Configuration[];

    constructor(private configurationService: ConfigurationService, private requestService: RequestService) {
    }

    ngOnInit(): void {
        this.configurationService.request()
            .subscribe(res => this.configurations = res.filter(c => c.key !== 'NEWS' && c.key !== 'RULES'));
    }

    submit(key: ConfigurationKey, value: string) {
        this.requestService.post("configuration", {key, value})
            .subscribe(() => toastr.success("Successfully saved."))
    }
}
