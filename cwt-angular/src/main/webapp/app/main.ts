import "../polyfill";
import "../vendor";
import "../scss/main.scss";
import "../css/archive.css";
import "../css/group-games.css";
import {platformBrowserDynamic} from "@angular/platform-browser-dynamic";
import {enableProdMode} from "@angular/core";
import {AppModule} from "./app.module";

import "../tetris/grid/grid";
import "../tetris/sketch";

import '../img/favicon.png'; // for the icon in manifest.json
import '../img/favicon.ico'; // for the favicon
import '../manifest.json';

if (process.env.ENV === 'production') {
    enableProdMode();
}

platformBrowserDynamic().bootstrapModule(AppModule);
