import {Component, OnInit} from '@angular/core';
import {ConfigurationService} from "../_services/configuration.service";
import {RequestService} from "../_services/request.service";
import {Configuration, ConfigurationDto} from "../custom";

const toastr = require('toastr/toastr.js');

@Component({
    selector: 'cwt-write-admin-news',
    template: require('./write-admin-news.component.html')
})
export class WriteAdminNewsComponent implements OnInit {
    news: Configuration<string>;

    constructor(private configurationService: ConfigurationService, private requestService: RequestService) {
    }

    ngOnInit(): void {
        this.configurationService.requestByKeys<string>("NEWS")
            .subscribe(res => this.news = res[0]);
    }

    submit(): void {
        this.requestService.post('configuration', <ConfigurationDto> {value: this.news.value, key: this.news.key})
            .subscribe(() => toastr.success("Successfully saved."), () => toastr.error("An unknown error occurred."));
    }
}
