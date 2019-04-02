import {Component, OnInit} from '@angular/core';
import {Configuration, ConfigurationKey} from "../custom";
import {ConfigurationService} from "../_services/configuration.service";
import {RequestService} from "../_services/request.service";
import {Toastr} from "../_services/toastr";

@Component({
    selector: 'cwt-admin-settings',
    template: require('./admin-settings.component.html')
})

export class AdminSettingsComponent implements OnInit {

    configurations: Configuration[];

    constructor(private configurationService: ConfigurationService, private requestService: RequestService,
                private toastr: Toastr) {
    }

    ngOnInit(): void {
        this.configurationService.request()
            .subscribe(res => this.configurations = res.filter(c => c.key !== 'NEWS' && c.key !== 'RULES'));
    }

    submit(key: ConfigurationKey, value: string) {
        this.requestService.post("configuration", {key, value})
            .subscribe(() => this.toastr.success("Successfully saved."))
    }
}
