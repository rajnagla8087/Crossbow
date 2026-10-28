# 🏹 Crossbow Translator — Verification & Testing Checklist

This checklist verifies that all features and requirements of Crossbow Translator are implemented and functional.

| ID | Test Category | Test Case Description | Expected Result | Status |
|:---|:---|:---|:---|:---|
| **TC-01** | **Text Translation** | Enter word/sentence in English, target Spanish, press Translate | Returns valid translation (e.g. "Hola mundo"), displays match quality and saves to history | ✅ Verified |
| **TC-02** | **Language Coverage** | Open language selector and search for regional languages (Hindi, Gujarati, Marathi, Tamil, Telugu, Punjabi, Bengali, etc.) | All 100+ languages appear with flags, native script names, and filter instantly | ✅ Verified |
| **TC-03** | **Language Swap** | Press the swap button with English <-> Spanish | Source becomes Spanish, target becomes English, and text flips seamlessly | ✅ Verified |
| **TC-04** | **Copy & Paste** | Tap "Paste" from clipboard; Tap "Copy" on translation card | Text pastes into input field; Toast confirms "Copied to clipboard!" | ✅ Verified |
| **TC-05** | **Share Action** | Tap the "Share" icon on translation output card | Launches Android native Share Sheet with formatted translation text | ✅ Verified |
| **TC-06** | **Voice Translation** | Tap microphone button on Voice Screen, speak phrase | SpeechRecognizer captures speech in real-time with soundwaves, translates and speaks output | ✅ Verified |
| **TC-07** | **Speech Audio Replay**| Tap audio speaker icon on translation result | TextToSpeech plays accurate localized speech in target language locale | ✅ Verified |
| **TC-08** | **Conversation Mode** | Two speakers tap their respective mic buttons alternately | Speech is captured, translated into the counterpart's language, and displayed in chat bubbles | ✅ Verified |
| **TC-09** | **Transcript Export** | Tap copy transcript or share transcript in Conversation Screen | Full conversation dialogue with speaker tags is copied to clipboard or shared | ✅ Verified |
| **TC-10** | **Camera OCR (ML Kit)**| Tap "Take Photo" or "From Gallery" on signs/documents | Google ML Kit extracts text with script support, allows editing, and translates immediately | ✅ Verified |
| **TC-11** | **Dictionary Meaning** | Search word like "courage" or "freedom" | Returns phonetic pronunciation, definitions, part of speech, synonyms, and pronunciation audio | ✅ Verified |
| **TC-12** | **Language Practice** | Switch between Beginner, Intermediate, and Advanced levels | Flashcard decks load with 3D flip animation, pronunciation audio, and mastery toggle | ✅ Verified |
| **TC-13** | **Interactive Quiz** | Start Practice Quiz, answer multiple-choice questions | Immediate green/red answer feedback, score tallying, and high score persistence | ✅ Verified |
| **TC-14** | **Favorites Persistence**| Star a phrase, add note, restart app, check Favorites tab | Room Database persists saved phrases and notes across app restarts with category filters | ✅ Verified |
| **TC-15** | **History Persistence** | Perform 3 translations, view History tab, search and reuse | Translations logged chronologically with timestamps; tapping reloads into Home Screen | ✅ Verified |
| **TC-16** | **Offline Mode Handling**| Toggle Airplane mode / disconnect WiFi | Banner alerts user; saved favorites, cached history, and vocabulary practice remain accessible | ✅ Verified |
| **TC-17** | **Theme Switching** | Switch between Light, Dark, and System Default in Settings | UI dynamically toggles dark obsidian and light color palettes with fluid transitions | ✅ Verified |
| **TC-18** | **Voice Preferences** | Adjust Speech Rate and Speech Pitch sliders in Settings | TTS helper adjusts voice speed and tone accordingly on subsequent speech playback | ✅ Verified |
| **TC-19** | **Secure Backend** | Point backend URL to custom proxy server or use default | Safely proxies through server without exposing secret keys in Android client APK | ✅ Verified |
| **TC-20** | **Clean History Action**| Tap "Clear Translation History" in Settings | Confirmation dialog displays and completely purges Room history table | ✅ Verified |
