# 🏹 Crossbow Translator
> **"Break Language Barriers."**

Crossbow Translator is a premier, production-grade Android translation suite designed with **Kotlin**, **Jetpack Compose**, **Material 3**, and modern reactive **MVVM architecture**. It empowers users to communicate effortlessly across **100+ languages** through instant text translation, real-time voice recognition, two-person conversation mode, camera OCR scanning, comprehensive linguistic dictionary definitions, and interactive language learning practice.

---

## 🌟 Key Features

### 1. 🌐 Text Translation (100+ Languages)
- Real-time text translation of words, sentences, and full paragraphs.
- Automatic source-language detection (`auto`).
- Searchable language modal with national flags and native script names.
- Instant language swapping with source/target text flipping.
- Coverage for English, Hindi, Gujarati, Marathi, Bengali, Tamil, Telugu, Punjabi, Spanish, French, German, Arabic, Chinese, Japanese, Korean, Portuguese, Russian, and 90+ more.

### 2. 📋 Quick Copy, Paste & Share
- Direct clipboard pasting with toast confirmation.
- One-tap copying of original or translated text.
- Native Android Share menu integration for sharing translations via WhatsApp, Telegram, Email, and SMS.

### 3. 🎙️ Voice Translation
- Voice input powered by Android `SpeechRecognizer`.
- Real-time animated audio equalizer visualizing voice input volume (RMSdB).
- Instant speech-to-text conversion and automatic translation into target language.
- Natural speech synthesis via Android `TextToSpeech` (TTS) with audio replay.

### 4. 💬 Two-Way Conversation Mode
- Split two-person bilingual conversation screen styled like a modern messaging app.
- Independent microphone controls for Speaker 1 and Speaker 2.
- Instant translation into each speaker's chosen language with optional auto-play voice output.
- Transcript export (copy full chat or share via Android share menu).

### 5. 📷 Camera Scan & Image OCR
- Powered by **Google ML Kit Text Recognition** and **CameraX**.
- Live camera photo capture and gallery image selection.
- Multi-script OCR support (Latin, Devanagari for Hindi/Marathi/Nepali, Chinese, Japanese, Korean).
- Editable preview before translating.

### 6. 📖 Linguistic Dictionary & Meanings
- Comprehensive lexical dictionary powered by Free Dictionary API.
- Phonetic transcriptions, audio pronunciation guides, and parts of speech (noun, verb, adjective).
- Numbered definitions with realistic example sentences, synonyms, and antonyms.
- One-tap saving to favorites.

### 7. 🎓 Language Practice & Quizzes
- Word-of-the-day and daily vocabulary cards.
- Interactive **3D-animated Flashcards** with tap-to-flip cards.
- Multi-difficulty level selection: **Beginner**, **Intermediate**, **Advanced**.
- Dynamic quizzes with multiple-choice and fill-in-the-blank questions.
- Immediate answer feedback (green/red indicators) and score tracking.
- Daily learning goal tracker (e.g. 5/10 words mastered) and day-streak tracking.

### 8. ⭐ Favorites & Translation History
- **Favorites:** Categorized bookmarks (General, Travel, Business, Food, Social, Dictionary) with custom notes.
- **History:** Searchable chronological translation logs with timestamps and one-tap reuse in the translator.
- **Room Database:** Local SQLite persistence ensuring full offline accessibility.

### 9. 🎨 Design Aesthetics
- Custom Crossbow app icon (glowing neon crossbow bolt with speech bubble & global network).
- Dark Mode (Obsidian & Midnight Indigo) and Light Mode with live theme switching.
- Frosted glassmorphism cards and smooth rounded corners (Material 3).
- Fluid navigation with Navigation Compose and bottom bar navigation.

### 10. 🔒 Secure Architecture
- Strict HTTPS network security config with encrypted endpoints.
- Secure optional backend proxy (`backend/`) preventing API key exposure on client devices.
- Runtime permission management with user-friendly explanations for Microphone and Camera.

---

## 🛠️ Technology Stack

| Layer | Technologies |
|:---|:---|
| **Language** | Kotlin 2.0 |
| **UI Framework** | Jetpack Compose with Material 3 |
| **Architecture** | MVVM (Model-View-ViewModel) + Clean Architecture |
| **Asynchronous Logic** | Kotlin Coroutines & StateFlow |
| **Local Database** | Room Database (SQLite with KSP compiler) |
| **Preferences** | Jetpack DataStore Preferences |
| **Networking** | Retrofit 2 + OkHttp 4 + Gson Converter |
| **Camera & OCR** | AndroidX CameraX + Google ML Kit Text Recognition |
| **Voice & Speech** | Android `SpeechRecognizer` + Android `TextToSpeech` |
| **Navigation** | Navigation Compose |
| **Backend (Optional Proxy)** | Node.js Express Server with Google / DeepL / MyMemory support |

---

## 📂 Project Structure

```
crossbow translater/
├── app/
│   ├── build.gradle.kts           # App-level build configuration & dependencies
│   ├── proguard-rules.pro         # ProGuard rules for Room, Retrofit & ML Kit
│   └── src/main/
│       ├── AndroidManifest.xml    # Permissions & application declaration
│       ├── java/com/crossbow/translator/
│       │   ├── CrossbowApplication.kt   # Application class with DI singletons
│       │   ├── MainActivity.kt          # Single-activity host with Compose theme
│       │   ├── data/
│       │   │   ├── api/                 # Retrofit interfaces & client
│       │   │   ├── local/               # Room Database, DAOs, Entities, DataStore
│       │   │   └── repository/          # Translation, Dictionary, Practice, Favorites repos
│       │   ├── domain/model/            # 100+ Language catalog, TranslationResult, etc.
│       │   ├── ui/
│       │   │   ├── components/          # TopBar, LanguageModal, Cards, GradientButtons
│       │   │   ├── navigation/          # NavGraph & routes
│       │   │   ├── screens/             # Home, Voice, Conversation, Camera, Dictionary, Practice, Favorites, History, Settings
│       │   │   └── theme/               # Color, Theme, Type, Shape
│       │   └── util/                    # SpeechRecognizerHelper, TextToSpeechHelper, CameraHelper, NetworkMonitor, ClipboardHelper
│       └── res/                         # Strings, colors, themes, adaptive icons, file paths
├── backend/                             # Optional Secure Translation Proxy Server
├── gradle/                              # Version catalog (libs.versions.toml) & wrapper
├── gradlew & gradlew.bat                # Gradle wrapper scripts
├── settings.gradle.kts                  # Project settings
├── SETUP_AND_BUILD_GUIDE.md             # Android Studio setup & APK build instructions
├── TESTING_CHECKLIST.md                 # 20-point comprehensive verification checklist
└── README.md                            # Complete documentation
```

---

## 🚀 Quick Setup Instructions

1. **Open in Android Studio:**
   - Open Android Studio, click **Open**, and choose `crossbow translater/`.
   - Android Studio will automatically synchronize Gradle dependencies.

2. **Run on Device or Emulator:**
   - Press **Shift + F10** or click the green **Run (▶)** button.

3. **Build APKs:**
   - Debug APK: Run `gradlew.bat assembleDebug` (Windows) or `./gradlew assembleDebug` (macOS/Linux).
   - See [SETUP_AND_BUILD_GUIDE.md](file:///c:/Users/nagla/OneDrive/Desktop/crossbow%20translater/SETUP_AND_BUILD_GUIDE.md) for full instructions.

---

## 📄 License
This project is licensed under the Apache 2.0 License.

---

## 👨‍💻 Author
Made with ❤️ by [Raj Nagla](https://github.com/rajnagla8087)

