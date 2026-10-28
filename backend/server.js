const express = require('express');
const cors = require('cors');
const axios = require('axios');
const path = require('path');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 3000;

// Trust reverse proxies (Vercel, Render, Railway, AWS ALB, Nginx)
app.set('trust proxy', 1);

// Production Security Headers Middleware
app.use((req, res, next) => {
  res.setHeader('X-Content-Type-Options', 'nosniff');
  res.setHeader('X-Frame-Options', 'SAMEORIGIN');
  res.setHeader('X-XSS-Protection', '1; mode=block');
  res.setHeader('Referrer-Policy', 'strict-origin-when-cross-origin');
  next();
});

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.static(path.join(__dirname, 'public'), {
  maxAge: process.env.NODE_ENV === 'production' ? '1d' : '0'
}));

// Health Check
app.get('/api/health', (req, res) => {
  res.json({
    status: 'online',
    app: 'Crossbow Translator Backend',
    version: '1.0.0',
    googleConfigured: !!process.env.GOOGLE_TRANSLATE_API_KEY,
    deeplConfigured: !!process.env.DEEPL_API_KEY,
    libreConfigured: !!process.env.LIBRETRANSLATE_URL,
    timestamp: new Date().toISOString()
  });
});

// Secure Translation Route
app.post('/api/translate', async (req, res) => {
  try {
    const { text, source, target } = req.body;

    if (!text || !target) {
      return res.status(400).json({ error: 'Text and target language code are required.' });
    }

    const sourceLang = source && source !== 'auto' ? source : '';

    // 1. Google Cloud Translation API (if configured in environment)
    if (process.env.GOOGLE_TRANSLATE_API_KEY) {
      try {
        const response = await axios.post(
          `https://translation.googleapis.com/language/translate/v2?key=${process.env.GOOGLE_TRANSLATE_API_KEY}`,
          {
            q: text,
            source: sourceLang || undefined,
            target: target,
            format: 'text'
          }
        );

        const translation = response.data.data.translations[0];
        return res.json({
          translatedText: translation.translatedText,
          sourceLang: translation.detectedSourceLanguage || sourceLang || 'auto',
          targetLang: target,
          provider: 'Google Cloud Translation'
        });
      } catch (gErr) {
        console.error('Google Cloud Translation error, falling back to free engine:', gErr.message);
      }
    }

    // 2. DeepL API (if configured in environment)
    if (process.env.DEEPL_API_KEY) {
      try {
        const deeplUrl = process.env.DEEPL_API_KEY.endsWith(':fx')
          ? 'https://api-free.deepl.com/v2/translate'
          : 'https://api.deepl.com/v2/translate';

        const params = new URLSearchParams();
        params.append('text', text);
        params.append('target_lang', target.toUpperCase());
        if (sourceLang) params.append('source_lang', sourceLang.toUpperCase());

        const response = await axios.post(deeplUrl, params, {
          headers: { 'Authorization': `DeepL-Auth-Key ${process.env.DEEPL_API_KEY}` }
        });

        const translation = response.data.translations[0];
        return res.json({
          translatedText: translation.text,
          sourceLang: translation.detected_source_language?.toLowerCase() || sourceLang,
          targetLang: target,
          provider: 'DeepL API'
        });
      } catch (dErr) {
        console.error('DeepL translation error, falling back to free engine:', dErr.message);
      }
    }

    // 3. Fallback High Availability Engine (MyMemory Open API)
    const pair = `${sourceLang || 'auto'}|${target}`;
    const myMemoryUrl = `https://api.mymemory.translated.net/get?q=${encodeURIComponent(text)}&langpair=${encodeURIComponent(pair)}`;
    
    const response = await axios.get(myMemoryUrl);
    if (response.data && response.data.responseData) {
      const translated = response.data.responseData.translatedText;
      return res.json({
        translatedText: translated,
        sourceLang: sourceLang || 'auto',
        targetLang: target,
        provider: 'MyMemory Open Engine'
      });
    }

    return res.status(502).json({ error: 'Translation providers returned no data.' });
  } catch (error) {
    console.error('Translation service exception:', error.message);
    res.status(500).json({ error: error.message || 'Internal server translation error.' });
  }
});

// Dictionary Proxy Route (Free Dictionary API)
app.get('/api/dictionary/:word', async (req, res) => {
  try {
    const word = encodeURIComponent(req.params.word.trim());
    const response = await axios.get(`https://api.dictionaryapi.dev/api/v2/entries/en/${word}`);
    return res.json(response.data);
  } catch (err) {
    if (err.response && err.response.status === 404) {
      return res.status(404).json({ message: 'No definitions found for this word.' });
    }
    return res.status(500).json({ error: 'Failed to fetch dictionary definition.' });
  }
});

// Fallback for single page application
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

const server = app.listen(PORT, '0.0.0.0', () => {
  console.log(`🏹 Crossbow Translator Backend listening securely on port ${PORT}`);
});

// Graceful Shutdown Handling
const shutdown = (signal) => {
  console.log(`Received ${signal}. Shutting down gracefully...`);
  server.close(() => {
    console.log('HTTP server closed successfully.');
    process.exit(0);
  });
  // Force close after 10s timeout
  setTimeout(() => {
    console.error('Could not close connections in time, forcefully shutting down');
    process.exit(1);
  }, 10000);
};

process.on('SIGTERM', () => shutdown('SIGTERM'));
process.on('SIGINT', () => shutdown('SIGINT'));
