# 🏹 Crossbow Translator — Setup & Build Guide

This document provides step-by-step instructions for importing, building, running, and generating APKs for **Crossbow Translator**.

---

## 1. Prerequisites
- **Android Studio:** Hedgehog (2023.1.1) or Ladybug / Koala (2024+)
- **JDK:** Java 17 or Java 21 (bundled with modern Android Studio)
- **Android SDK:** API 34 (Android 14) or API 35 with Build-Tools 34.0.0+
- **Minimum Android Device:** Android 7.0 (API Level 24+)

---

## 2. Opening the Project in Android Studio
1. Launch **Android Studio**.
2. Select **Open** (or **File > Open...**).
3. Navigate to the project root directory:
   ```
   crossbow translater/
   ```
4. Click **OK**. Android Studio will read `settings.gradle.kts` and automatically download Gradle 8.7 via the wrapper.
5. Wait for **Gradle Sync** to finish. If prompted for SDK installation, click **Install missing SDK components**.

---

## 3. Running the App
1. Connect an Android device with USB Debugging enabled or start an Android Virtual Device (AVD).
2. Select the **app** configuration in the top toolbar.
3. Click the green **Run (▶)** button or press `Shift + F10`.

---

## 4. How to Generate a Debug APK

### Option A: Via Command Line (Gradle Wrapper)
- **On Windows (PowerShell or Command Prompt):**
  ```cmd
  gradlew.bat assembleDebug
  ```
- **On macOS / Linux:**
  ```bash
  chmod +x gradlew
  ./gradlew assembleDebug
  ```

The generated APK will be available at:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Option B: Via Android Studio GUI
1. In the menu, go to **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
2. When the build finishes, click the notification popup link **locate** to view the `app-debug.apk`.

---

## 5. How to Generate a Signed Release APK

### Step 1: Generate a Keystore (if you do not have one)
Using the Java `keytool` command in your terminal:
```bash
keytool -genkey -v -keystore crossbow-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias crossbow -storepass MySecretPass123 -keypass MySecretPass123 -dname "CN=Crossbow Translator, OU=Mobile, O=Crossbow, L=San Francisco, ST=CA, C=US"
```

### Step 2: Generate Signed APK via Android Studio
1. In the menu, go to **Build > Generate Signed Bundle / APK...**
2. Choose **APK** and click **Next**.
3. Point **Key store path** to your `crossbow-release.jks`.
4. Enter your Keystore password, key alias (`crossbow`), and key password.
5. Select destination directory (e.g., `app/release/`).
6. Check **release** build variant and ensure **V1 (Jar Signature)** and **V2 (Full APK Signature)** are checked.
7. Click **Finish**.

The signed release APK will be generated at:
```
app/release/app-release.apk
```

---

## 6. API Configuration & Secure Backend Setup

Crossbow Translator works out-of-the-box with **no mandatory keys required** because it integrates the free MyMemory translation engine and Free Dictionary API.

If you wish to route translations through private Google Cloud or DeepL API credentials:

1. Open `backend/` folder:
   ```bash
   cd backend
   npm install
   ```
2. Create `.env` file from `.env.example`:
   ```bash
   GOOGLE_TRANSLATE_API_KEY=your_google_api_key_here
   DEEPL_API_KEY=your_deepl_api_key_here
   ```
3. Start the proxy server:
   ```bash
   npm start
   ```
4. In Crossbow Translator, go to **Settings > Secure API Backend** and set:
   - For Android Emulator: `http://10.0.2.2:3000/`
   - For Physical Device: `http://<YOUR_LAN_IP>:3000/` or your cloud URL.

---

## 👨‍💻 Author
Made with ❤️ by [Raj Nagla](https://github.com/rajnagla8087)

