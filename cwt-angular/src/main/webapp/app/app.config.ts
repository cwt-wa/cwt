import {InjectionToken} from "@angular/core";

// Webpack may be used to replace this file based on the environment.

export interface AppConfig {
    apiEndpoint: string;
    captchaKey: string;
    liveStreamProducer: string;
}

export const appConfig: AppConfig = {
    apiEndpoint: process.env.apiEndpoint,
    captchaKey: process.env.captchaKey,
    liveStreamProducer: process.env.liveStreamProducer
};

export const APP_CONFIG = new InjectionToken<AppConfig>('app.config');

