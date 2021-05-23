import "../polyfill";
import "../vendor";
import "../scss/main.scss";
import "../css/archive.css";
import "../css/group-games.css";
import {platformBrowserDynamic} from "@angular/platform-browser-dynamic";
import {enableProdMode} from "@angular/core";
import {AppModule} from "./_modules/app.module";

if (process.env.ENV === 'production') {
    enableProdMode();
}

platformBrowserDynamic().bootstrapModule(AppModule);
