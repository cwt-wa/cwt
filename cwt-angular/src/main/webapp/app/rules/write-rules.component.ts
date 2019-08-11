import {Component, OnInit} from '@angular/core';
import {ConfigurationService} from "../_services/configuration.service";
import {RequestService} from "../_services/request.service";
import {Configuration, ConfigurationDto} from "../custom";
import {Toastr} from "../_services/toastr";

@Component({
    selector: 'cwt-write-rules',
    template: require('./write-rules.component.html')
})
export class WriteRulesComponent implements OnInit {
    rules: Configuration;

    constructor(private configurationService: ConfigurationService, private requestService: RequestService,
                private toastr: Toastr) {
    }

    ngOnInit(): void {
        this.configurationService.requestByKeys("RULES")
            .subscribe(res => this.rules = res[0]);
    }

    submit(): void {
        this.requestService.post('configuration', <ConfigurationDto> {value: this.rules.value, key: this.rules.key})
            .subscribe(() => this.toastr.success("Successfully saved."));
    }
}
