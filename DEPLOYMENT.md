# 🚀 Crossbow Translator — Production Deployment Guide

This project is fully deployment-ready for both **Cloud Web/PWA** and **Android Mobile (APK/AAB)**.

---

## 📋 Table of Contents
1. [Option A: 1-Click Deployment to Vercel (Recommended for Web/PWA)](#option-a-1-click-deployment-to-vercel)
2. [Option B: 1-Click Deployment to Render.com](#option-b-1-click-deployment-to-render)
3. [Option C: Containerized Deployment via Docker & Docker Compose](#option-c-docker--docker-compose)
4. [Option D: Deploy to Railway / Heroku](#option-d-railway--heroku)
5. [Option E: Building the Android Release APK / Google Play Bundle](#option-e-android-release-build)
6. [Environment Variables Reference](#environment-variables-reference)

---

## Option A: 1-Click Deployment to Vercel

The project includes a production [`vercel.json`](./vercel.json) manifest.

### Via Vercel CLI:
```bash
npm install -g vercel
vercel
```

### Via Vercel Web Dashboard:
1. Push your repository to **GitHub** or **GitLab**.
2. Go to [vercel.com/new](https://vercel.com/new) and import the repository.
3. Keep default settings (Vercel will automatically read `vercel.json`).
4. Click **Deploy**.
5. Your Crossbow Translator PWA will be live globally on an `https://*.vercel.app` domain with automated HTTPS and edge caching!

---

## Option B: 1-Click Deployment to Render

The repository includes a ready-to-use [`render.yaml`](./render.yaml) blueprint.

1. Push your code to **GitHub**.
2. Go to [dashboard.render.com](https://dashboard.render.com).
3. Click **New +** > **Blueprint**.
4. Select your Crossbow Translator repository.
5. Render will detect `render.yaml` and configure:
   - Node 20 runtime
   - Port 10000 / Auto-scaling
   - Healthcheck on `/api/health`
6. Click **Apply**.

---

## Option C: Docker & Docker Compose

For deploying on any virtual machine (AWS EC2, DigitalOcean, Linode, Hetzner, etc.):

### 1. Using Docker Compose:
```bash
docker compose up -d --build
```
* **Web App & API:** Accessible on `http://YOUR_SERVER_IP:3000`
* **Health Check:** `http://YOUR_SERVER_IP:3000/api/health`
* **Log inspection:** `docker compose logs -f`

### 2. Standalone Docker Image:
```bash
cd backend
docker build -t crossbow-translator .
docker run -d -p 3000:3000 --name crossbow-app --restart unless-stopped crossbow-translator
```

---

## Option D: Railway / Heroku

The project includes a production [`Procfile`](./Procfile):
```
web: cd backend && node server.js
```

### For Railway:
1. Connect your GitHub repository at [railway.app](https://railway.app).
2. Set root directory to repository root.
3. Deploy automatically!

### For Heroku:
```bash
heroku create crossbow-translator
git push heroku main
```

---

## Option E: Android Release Build

### 1. Generate Keystore (First-time setup):
```bash
keytool -genkey -v -keystore crossbow-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias crossbow -storepass YourSecretPassword -keypass YourSecretPassword -dname "CN=Crossbow Translator, OU=Mobile, O=Crossbow, C=US"
```

### 2. Build Release APK via CLI:
```bash
# On Windows:
.\gradlew.bat assembleRelease

# On Linux/macOS:
chmod +x gradlew
./gradlew assembleRelease
```
The APK will be generated at:
```
app/build/outputs/apk/release/app-release-unsigned.apk
```

### 3. Sign the APK with `zipalign` and `apksigner`:
```bash
zipalign -v -p 4 app/build/outputs/apk/release/app-release-unsigned.apk app-aligned.apk
apksigner sign --ks crossbow-release.jks --ks-key-alias crossbow --out app-release.apk app-aligned.apk
```

---

## Environment Variables Reference

| Variable Name | Required | Default / Fallback | Description |
| :--- | :---: | :---: | :--- |
| `PORT` | Optional | `3000` | Port for the HTTP & PWA server |
| `NODE_ENV` | Optional | `production` | Node environment |
| `GOOGLE_TRANSLATE_API_KEY` | Optional | Free MyMemory Engine | Optional Google Cloud Translation API key |
| `DEEPL_API_KEY` | Optional | Free MyMemory Engine | Optional DeepL Pro or Free API key |
| `LIBRETRANSLATE_URL` | Optional | None | Self-hosted LibreTranslate URL |
