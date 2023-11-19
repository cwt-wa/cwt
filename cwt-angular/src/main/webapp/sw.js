const CACHE = 'cwt-v1';

self.addEventListener('fetch', (event) => {
  const pathname = new URL(event.request.url).pathname;
  if (event.request.method === "GET"
  && pathname !== "/api/message/listen"
  && !pathname.endsWith("/stats-listen")
  && !pathname.endsWith("/produce")) {
      event.respondWith(caches.open(CACHE).then((cache) => {
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
  const tag = n.tag;
  let link = "/";
  if (tag.startsWith("chat-")
    || tag.startsWith("pm-")
    || tag.startsWith("schedule-")
    || tag.startsWith("stream_schedule-")
    || tag.startsWith("announcement-")
    || tag.startsWith("stream_live-")) {
    link = "/";
  } else if (tag.startsWith("report-")
    || tag.startsWith("void-")
    || tag.startsWith("rating-")
    || tag.startsWith("comment-")
    || tag.startsWith("stream_record-")) {
    link = "/games/" + tag.split("-")[1];
  }
  event.waitUntil(
    clients
      .openWindow(link)
      .then(c => (c ? c.focus() : null))
  );
});


