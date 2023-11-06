const cacheName = 'cwt-v1';

self.addEventListener('fetch', (event) => {
    event.respondWith(caches.open(cacheName).then((cache) => {
      return fetch(event.request.url).then((fetchedResponse) => {
        cache.put(event.request, fetchedResponse.clone());
        return fetchedResponse;
      }).catch(() => {
        return cache.match(event.request.url);
      });
    }));
});

self.addEventListener('push', event => {
  console.log("received push", event);
  // reference impl
  // https://github.com/mdn/serviceworker-cookbook/tree/master/push-payload
  const payload = event.data ? event.data.text() : 'no payload';
  event.waitUntil(
    self.registration.showNotification('CWT', {
      body: payload,
    })
  );
});
