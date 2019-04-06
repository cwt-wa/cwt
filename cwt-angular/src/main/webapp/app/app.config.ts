import {InjectionToken} from "@angular/core";

// Webpack may be used to replace this file based on the environment.

export interface AppConfig {
    apiEndpoint: string;
}

export const appConfig: AppConfig = {
    apiEndpoint: 'http://localhost:9000/api/',
};

export const APP_CONFIG = new InjectionToken<AppConfig>('app.config');

