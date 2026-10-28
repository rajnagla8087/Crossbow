/**
 * 🏹 CROSSBOW TRANSLATOR — SERVICE WORKER
 * Full Progressive Web App (PWA) Offline & Shell Caching
 */

const CACHE_NAME = 'crossbow-pwa-v1';

const APP_SHELL = [
  '/',
  '/index.html',
  '/style.css',
  '/app.js',
  '/manifest.json',
  '/icon.svg',
  '/icon-192.png',
  '/icon-512.png'
];

// Install Event — Pre-cache App Shell
self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => {
      console.log('[Crossbow SW] Caching App Shell');
      return cache.addAll(APP_SHELL);
    }).then(() => self.skipWaiting())
  );
});

// Activate Event — Clean up outdated caches
self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches.keys().then((keyList) => {
      return Promise.all(
        keyList.map((key) => {
          if (key !== CACHE_NAME) {
            console.log('[Crossbow SW] Purging old cache:', key);
            return caches.delete(key);
          }
        })
      );
    }).then(() => self.clients.claim())
  );
});

// Fetch Event — Offline First with Network Fallback
self.addEventListener('fetch', (event) => {
  const url = new URL(event.request.url);

  // API Requests: Network First with Offline JSON Fallback
  if (url.pathname.startsWith('/api/')) {
    event.respondWith(
      fetch(event.request).catch(() => {
        if (url.pathname.includes('/translate')) {
          return new Response(
            JSON.stringify({
              translatedText: '⚠️ Offline Mode: Internet connection required for live translation engines.',
              sourceLang: 'auto',
              targetLang: 'auto',
              provider: 'Offline Engine'
            }),
            { headers: { 'Content-Type': 'application/json' } }
          );
        }
        return new Response(
          JSON.stringify({ status: 'offline', message: 'You are currently offline' }),
          { headers: { 'Content-Type': 'application/json' } }
        );
      })
    );
    return;
  }

  // App Shell & Static Assets: Stale-While-Revalidate
  event.respondWith(
    caches.match(event.request).then((cachedResponse) => {
      const fetchPromise = fetch(event.request)
        .then((networkResponse) => {
          if (networkResponse && networkResponse.status === 200 && networkResponse.type === 'basic') {
            const responseToCache = networkResponse.clone();
            caches.open(CACHE_NAME).then((cache) => {
              cache.put(event.request, responseToCache);
            });
          }
          return networkResponse;
        })
        .catch(() => {
          // If navigation fails completely, return cached index
          if (event.request.mode === 'navigate') {
            return caches.match('/index.html');
          }
        });

      return cachedResponse || fetchPromise;
    })
  );
});
