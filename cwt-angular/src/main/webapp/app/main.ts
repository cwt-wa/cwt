import "../polyfill";
import "../vendor";
import "../scss/main.scss";
import "../css/archive.css"; // TODO Make this component specific
import "../css/group-games.css"; // TODO Make this component specific
import {platformBrowserDynamic} from "@angular/platform-browser-dynamic";
import {enableProdMode} from "@angular/core";
import {AppModule} from "./app.module";

import "../tetris/grid/grid";
import "../tetris/sketch";

if (process.env.ENV === 'production') {
    enableProdMode();
}

platformBrowserDynamic().bootstrapModule(AppModule);
