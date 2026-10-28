# 🏹 Crossbow Translator — Secure Backend Proxy

This backend service acts as a secure intermediary between the Crossbow Translator Android client and proprietary translation API providers (such as Google Cloud Translation API and DeepL API).

### Why use this backend?
1. **Zero Client Secret Exposure:** API keys remain strictly on this server and are never decompiled or extracted from the APK.
2. **Multi-Provider Fallback:** Automatically tries Google Cloud Translation, then DeepL, then MyMemory Open Engine if keys are unavailable.
3. **Rate Limiting & Monitoring:** Protects quotas and enables enterprise logging.

### Quick Start

1. Install dependencies:
   ```bash
   npm install
   ```

2. Configure environment (optional):
   ```bash
   cp .env.example .env
   # Add your GOOGLE_TRANSLATE_API_KEY or DEEPL_API_KEY if available
   ```

3. Launch server:
   ```bash
   npm start
   ```

4. Test health endpoint:
   ```bash
   curl http://localhost:3000/api/health
   ```

5. Point Crossbow Translator App:
   In the Android App **Settings > Secure API Backend**, enter:
   - For Android Emulator: `http://10.0.2.2:3000/`
   - For Physical Device: `http://<YOUR_COMPUTER_IP>:3000/` or your deployed HTTPS URL (e.g. Railway, Render, GCP, AWS).

---

## 👨‍💻 Author
Made with ❤️ by [Raj Nagla](https://github.com/rajnagla8087)

