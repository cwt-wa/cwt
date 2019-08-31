import {InjectionToken} from "@angular/core";

// Webpack may be used to replace this file based on the environment.

export interface AppConfig {
    binaryDataStoreEndpoint: string | null;
    apiEndpoint: string;
}

export const appConfig: AppConfig = {
    apiEndpoint: 'http://localhost:9000/api/',
    binaryDataStoreEndpoint: null,
};

export const APP_CONFIG = new InjectionToken<AppConfig>('app.config');

