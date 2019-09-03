import {InjectionToken} from "@angular/core";

// Webpack may be used to replace this file based on the environment.

export interface AppConfig {
    binaryDataStoreEndpoint: string | null;
    apiEndpoint: string;
    captchaKey: string;
}

export const appConfig: AppConfig = {
    apiEndpoint: process.env.apiEndpoint,
    binaryDataStoreEndpoint: process.env.binaryDataStoreEndpoint,
    captchaKey: process.env.captchaKey,
};

export const APP_CONFIG = new InjectionToken<AppConfig>('app.config');

