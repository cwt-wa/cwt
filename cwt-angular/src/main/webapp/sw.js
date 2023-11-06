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
