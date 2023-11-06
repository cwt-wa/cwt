import "../polyfill";
import "../vendor";
import "../scss/main.scss";
import "../css/archive.css";
import "../css/group-games.css";
import {platformBrowserDynamic} from "@angular/platform-browser-dynamic";
import {enableProdMode} from "@angular/core";
import {AppModule} from "./_modules/app.module";

import '../img/favicon.png'; // for the icon in manifest.json
import '../img/favicon.ico'; // for the favicon

import '../img/icons/icon-72x72.png';
import '../img/icons/icon-96x96.png';
import '../img/icons/icon-128x128.png';
import '../img/icons/icon-144x144.png';
import '../img/icons/icon-152x152.png';
import '../img/icons/icon-192x192.png';
import '../img/icons/icon-384x384.png';
import '../img/icons/icon-512x512.png';

import '../manifest.json';
import '../sw.js';

if ("serviceWorker" in navigator) {
  // Register a service worker hosted at the root of the
  // site using the default scope.
  navigator.serviceWorker.register("/sw.js").then(
    (registration) => {
      console.log("Service worker registration succeeded:", registration);
    },
    err => {
      console.error("Service worker registration failed:", err);
    },
  );
} else {
  console.error("Service workers are not supported.");
}

if (process.env.ENV === 'production') {
    enableProdMode();
}

platformBrowserDynamic().bootstrapModule(AppModule);
