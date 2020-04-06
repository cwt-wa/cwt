import {Component, OnInit} from '@angular/core';
import {ConfigurationService} from "../_services/configuration.service";
import {RequestService} from "../_services/request.service";
import {Configuration, ConfigurationDto} from "../custom";
import {Toastr} from "../_services/toastr";

@Component({
    selector: 'cwt-write-admin-news',
    template: require('./write-admin-news.component.html')
})
export class WriteAdminNewsComponent implements OnInit {
    news: Configuration;

    constructor(private configurationService: ConfigurationService, private requestService: RequestService,
                private toastr: Toastr) {
    }

    ngOnInit(): void {
        this.configurationService.requestByKeys("NEWS")
            .subscribe(res => this.news = res[0]);
    }

    submit(): void {
        this.requestService.post('configuration', <ConfigurationDto> {value: this.news.value, key: this.news.key})
            .subscribe(() => this.toastr.success("Successfully saved."));
    }
}
