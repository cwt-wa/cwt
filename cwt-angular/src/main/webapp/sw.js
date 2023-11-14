const cacheName = 'cwt-v1';

self.addEventListener('fetch', (event) => {
    if (event.request.method === "GET") {
        event.respondWith(caches.open(cacheName).then((cache) => {
          return fetch(event.request).then((fetchedResponse) => {
            cache.put(event.request, fetchedResponse.clone());
            return fetchedResponse;
          }).catch(() => {
            return cache.match(event.request);
          });
        }));
    }
});

self.addEventListener('push', event => {
  // reference impl
  // https://github.com/mdn/serviceworker-cookbook/tree/master/push-payload
  console.log("received push", event);
  const n = event.data.json();
  console.log("notification:", n);
  event.waitUntil(
    self.registration.showNotification(n.title, {
      ...n.options,
      icon: "/img/icons/masked.png",
      badge: "/img/icons/badge.png",
    })
  );
});

self.addEventListener("notificationclick", event => {
  console.log("On notification click: ", event);
  const n = event.notification;
  n.close();
  event.waitUntil(
    clients
      .openWindow(n.data.link)
      .then(c => (c ? c.focus() : null))
  );
});


