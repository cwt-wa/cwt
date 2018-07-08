import {InjectionToken} from "@angular/core";

// Webpack may be used to replace this file based on the environment.

export interface AppConfig {
    apiEndpoint: string;
    tournament: {
        advancingPerGroup: 2;
        numberOfGroups: 8;
        usersPerGroup: 4;
    }
}

export const appConfig: AppConfig = {
    apiEndpoint: 'http://192.168.178.25:9000/api/',
    tournament: {
        advancingPerGroup: 2,
        numberOfGroups: 8,
        usersPerGroup: 4,
    }
};

export const APP_CONFIG = new InjectionToken<AppConfig>('app.config');

