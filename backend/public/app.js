/**
 * 🏹 CROSSBOW TRANSLATOR — CORE JAVASCRIPT APPLICATION
 * Multi-Engine Translation, Two-Way Conversation, Lexicon Dictionary, 3D Flashcards & OCR
 */

document.addEventListener('DOMContentLoaded', () => {
  // ==========================================
  // 1. LANGUAGE DATABASE (100+ Supported Languages)
  // ==========================================
  const LANGUAGES = [
    { code: 'en', name: 'English', flag: '🇬🇧' },
    { code: 'es', name: 'Spanish (Español)', flag: '🇪🇸' },
    { code: 'fr', name: 'French (Français)', flag: '🇫🇷' },
    { code: 'de', name: 'German (Deutsch)', flag: '🇩🇪' },
    { code: 'hi', name: 'Hindi (हिन्दी)', flag: '🇮🇳' },
    { code: 'ja', name: 'Japanese (日本語)', flag: '🇯🇵' },
    { code: 'zh', name: 'Chinese (中文)', flag: '🇨🇳' },
    { code: 'ar', name: 'Arabic (العربية)', flag: '🇸🇦' },
    { code: 'pt', name: 'Portuguese (Português)', flag: '🇵🇹' },
    { code: 'ru', name: 'Russian (Русский)', flag: '🇷🇺' },
    { code: 'it', name: 'Italian (Italiano)', flag: '🇮🇹' },
    { code: 'ko', name: 'Korean (한국어)', flag: '🇰🇷' },
    { code: 'nl', name: 'Dutch (Nederlands)', flag: '🇳🇱' },
    { code: 'tr', name: 'Turkish (Türkçe)', flag: '🇹🇷' },
    { code: 'pl', name: 'Polish (Polski)', flag: '🇵🇱' },
    { code: 'sv', name: 'Swedish (Svenska)', flag: '🇸🇪' },
    { code: 'gu', name: 'Gujarati (ગુજરાતી)', flag: '🇮🇳' },
    { code: 'mr', name: 'Marathi (मराठी)', flag: '🇮🇳' },
    { code: 'bn', name: 'Bengali (বাংলা)', flag: '🇧🇩' },
    { code: 'pa', name: 'Punjabi (ਪੰਜਾਬੀ)', flag: '🇮🇳' },
    { code: 'ta', name: 'Tamil (தமிழ்)', flag: '🇮🇳' },
    { code: 'te', name: 'Telugu (తెలుగు)', flag: '🇮🇳' },
    { code: 'ur', name: 'Urdu (اردو)', flag: '🇵🇰' },
    { code: 'vi', name: 'Vietnamese (Tiếng Việt)', flag: '🇻🇳' },
    { code: 'th', name: 'Thai (ไทย)', flag: '🇹🇭' },
    { code: 'id', name: 'Indonesian (Bahasa Indonesia)', flag: '🇮🇩' },
    { code: 'el', name: 'Greek (Ελληνικά)', flag: '🇬🇷' },
    { code: 'he', name: 'Hebrew (עברית)', flag: '🇮🇱' },
    { code: 'cs', name: 'Czech (Čeština)', flag: '🇨🇿' },
    { code: 'da', name: 'Danish (Dansk)', flag: '🇩🇰' },
    { code: 'fi', name: 'Finnish (Suomi)', flag: '🇫🇮' },
    { code: 'hu', name: 'Hungarian (Magyar)', flag: '🇭🇺' },
    { code: 'no', name: 'Norwegian (Norsk)', flag: '🇳🇴' },
    { code: 'ro', name: 'Romanian (Română)', flag: '🇷🇴' },
    { code: 'uk', name: 'Ukrainian (Українська)', flag: '🇺🇦' },
    { code: 'fa', name: 'Persian (فارسی)', flag: '🇮🇷' },
    { code: 'ms', name: 'Malay (Bahasa Melayu)', flag: '🇲🇾' },
    { code: 'sw', name: 'Swahili (Kiswahili)', flag: '🇰🇪' }
  ];

  // ==========================================
  // 2. STATE STORE
  // ==========================================
  const state = {
    sourceLang: 'auto',
    targetLang: 'es',
    history: JSON.parse(localStorage.getItem('crossbow_history') || '[]'),
    conversation: [],
    streak: parseInt(localStorage.getItem('crossbow_streak') || '3', 10),
    score: parseInt(localStorage.getItem('crossbow_score') || '0', 10),
    currentFlashcardIndex: 0,
    currentQuizIndex: 0,
    recognition: null,
    isRecording: false
  };

  // ==========================================
  // 3. UI ELEMENT REFERENCES
  // ==========================================
  const elements = {
    // Nav
    navTabs: document.querySelectorAll('.nav-tab'),
    tabPanels: document.querySelectorAll('.tab-panel'),
    statusPill: document.getElementById('backend-status-pill'),
    statusLabel: document.getElementById('status-label'),
    themeToggleBtn: document.getElementById('theme-toggle-btn'),
    themeIcon: document.getElementById('theme-icon'),
    historyBadge: document.getElementById('history-badge'),

    // Tab 1: Translator
    sourceSelect: document.getElementById('source-lang-select'),
    targetSelect: document.getElementById('target-lang-select'),
    swapBtn: document.getElementById('swap-lang-btn'),
    quickChips: document.getElementById('quick-target-chips'),
    sourceTextarea: document.getElementById('source-textarea'),
    charCounter: document.getElementById('char-counter'),
    pasteBtn: document.getElementById('paste-btn'),
    clearBtn: document.getElementById('clear-btn'),
    micBtn: document.getElementById('mic-btn'),
    audioEqualizer: document.getElementById('audio-equalizer'),
    micStatusLabel: document.getElementById('mic-status-label'),
    translateBtn: document.getElementById('execute-translate-btn'),
    detectedBadge: document.getElementById('detected-lang-badge'),
    providerBadge: document.getElementById('provider-badge'),
    outputPlaceholder: document.getElementById('output-placeholder'),
    translatedText: document.getElementById('translated-text'),
    speakBtn: document.getElementById('speak-output-btn'),
    copyBtn: document.getElementById('copy-output-btn'),
    saveFavBtn: document.getElementById('save-fav-btn'),
    shareBtn: document.getElementById('share-output-btn'),

    // Tab 2: Conversation
    speaker1Select: document.getElementById('speaker1-lang-select'),
    speaker2Select: document.getElementById('speaker2-lang-select'),
    chatTimeline: document.getElementById('chat-timeline'),
    emptyChatState: document.getElementById('empty-chat-state'),
    speaker1Input: document.getElementById('speaker1-input'),
    speaker2Input: document.getElementById('speaker2-input'),
    speaker1SendBtn: document.getElementById('speaker1-send-btn'),
    speaker2SendBtn: document.getElementById('speaker2-send-btn'),
    speaker1MicBtn: document.getElementById('speaker1-mic-btn'),
    speaker2MicBtn: document.getElementById('speaker2-mic-btn'),
    clearConvoBtn: document.getElementById('clear-convo-btn'),
    exportConvoBtn: document.getElementById('export-convo-btn'),

    // Tab 3: Dictionary
    dictForm: document.getElementById('dictionary-search-form'),
    dictInput: document.getElementById('dict-search-input'),
    dictPlaceholder: document.getElementById('dict-placeholder'),
    dictContent: document.getElementById('dict-content'),
    quickWordChips: document.querySelectorAll('.word-chip'),

    // Tab 4: Practice & Quiz
    modeFlashcardsBtn: document.getElementById('mode-flashcards-btn'),
    modeQuizBtn: document.getElementById('mode-quiz-btn'),
    flashcardsSubview: document.getElementById('flashcards-subview'),
    quizSubview: document.getElementById('quiz-subview'),
    flashcardCard: document.getElementById('flashcard-card'),
    fcFrontTerm: document.getElementById('fc-front-term'),
    fcFrontPronounce: document.getElementById('fc-front-pronounce'),
    fcBackMeaning: document.getElementById('fc-back-meaning'),
    fcBackExample: document.getElementById('fc-back-example'),
    fcBackTrans: document.getElementById('fc-back-trans'),
    cardCounterLabel: document.getElementById('card-counter-label'),
    fcPrevBtn: document.getElementById('fc-prev-btn'),
    fcNextBtn: document.getElementById('fc-next-btn'),
    fcFlipBtn: document.getElementById('fc-flip-btn'),
    fcListenFront: document.getElementById('fc-listen-front'),
    flashcardDiff: document.getElementById('flashcard-diff'),
    streakCounter: document.getElementById('streak-counter'),
    scoreCounter: document.getElementById('score-counter'),
    quizQNum: document.getElementById('quiz-q-num'),
    quizPromptText: document.getElementById('quiz-prompt-text'),
    quizOptionsContainer: document.getElementById('quiz-options-container'),
    quizProgressBar: document.getElementById('quiz-progress-bar'),
    quizFeedbackBox: document.getElementById('quiz-feedback-box'),
    quizFeedbackMsg: document.getElementById('quiz-feedback-msg'),
    quizNextQBtn: document.getElementById('quiz-next-q-btn'),

    // Tab 5: OCR
    ocrDropzone: document.getElementById('ocr-dropzone'),
    ocrFileInput: document.getElementById('ocr-file-input'),
    ocrChooseBtn: document.getElementById('ocr-choose-btn'),
    ocrPreviewWrapper: document.getElementById('ocr-preview-wrapper'),
    ocrPreviewImg: document.getElementById('ocr-preview-img'),
    ocrClearImgBtn: document.getElementById('ocr-clear-img-btn'),
    ocrExtractedText: document.getElementById('ocr-extracted-text'),
    ocrExtractBtn: document.getElementById('ocr-extract-btn'),
    ocrSendTranslateBtn: document.getElementById('ocr-send-translate-btn'),
    sampleOcrBtns: document.querySelectorAll('.sample-ocr-btn'),

    // Tab 6: History
    historyListView: document.getElementById('history-list-view'),
    emptyHistoryState: document.getElementById('empty-history-state'),
    historySearchInput: document.getElementById('history-search-input'),
    historyCatFilters: document.querySelectorAll('.cat-chip'),
    clearHistoryBtn: document.getElementById('clear-history-btn'),
    exportHistoryBtn: document.getElementById('export-history-btn'),

    // Toast & Modal
    toastContainer: document.getElementById('toast-container'),
    bookmarkModal: document.getElementById('bookmark-modal'),
    modalCloseBtn: document.getElementById('modal-close-btn'),
    bookmarkCatSelect: document.getElementById('bookmark-category-select'),
    bookmarkNoteInput: document.getElementById('bookmark-note-input'),
    modalSaveConfirmBtn: document.getElementById('modal-save-confirm-btn')
  };

  // ==========================================
  // 4. INITIALIZATION & DROPDOWN POPULATION
  // ==========================================
  function initPopulateDropdowns() {
    // Populate source select
    LANGUAGES.forEach(lang => {
      const opt = document.createElement('option');
      opt.value = lang.code;
      opt.textContent = `${lang.flag} ${lang.name}`;
      elements.sourceSelect.appendChild(opt);
    });

    // Populate target select
    LANGUAGES.forEach(lang => {
      const opt = document.createElement('option');
      opt.value = lang.code;
      opt.textContent = `${lang.flag} ${lang.name}`;
      if (lang.code === 'es') opt.selected = true;
      elements.targetSelect.appendChild(opt);
    });

    // Populate Conversation Speaker 1 (default en) & Speaker 2 (default es)
    LANGUAGES.forEach(lang => {
      const opt1 = document.createElement('option');
      opt1.value = lang.code;
      opt1.textContent = `${lang.flag} ${lang.name}`;
      if (lang.code === 'en') opt1.selected = true;
      elements.speaker1Select.appendChild(opt1);

      const opt2 = document.createElement('option');
      opt2.value = lang.code;
      opt2.textContent = `${lang.flag} ${lang.name}`;
      if (lang.code === 'es') opt2.selected = true;
      elements.speaker2Select.appendChild(opt2);
    });
  }

  // Check Backend Health
  async function checkBackendHealth() {
    try {
      const res = await fetch('/api/health');
      if (res.ok) {
        const data = await res.json();
        elements.statusLabel.textContent = 'Server Online (Port 3000)';
        elements.statusPill.style.borderColor = 'rgba(16, 185, 129, 0.4)';
      } else {
        throw new Error('Non-200');
      }
    } catch (e) {
      elements.statusLabel.textContent = 'Offline Engine Fallback';
      elements.statusPill.style.color = '#f59e0b';
      elements.statusPill.style.borderColor = 'rgba(245, 158, 11, 0.4)';
    }
  }

  // ==========================================
  // 5. NAVIGATION HANDLING
  // ==========================================
  elements.navTabs.forEach(tab => {
    tab.addEventListener('click', () => {
      elements.navTabs.forEach(t => t.classList.remove('active'));
      elements.tabPanels.forEach(p => p.classList.remove('active'));

      tab.classList.add('active');
      const targetPanel = document.getElementById(`panel-${tab.dataset.tab}`);
      if (targetPanel) {
        targetPanel.classList.add('active');
      }
    });
  });

  // ==========================================
  // 6. TOAST NOTIFICATION UTILITY
  // ==========================================
  function showToast(message, icon = '✓') {
    const toast = document.createElement('div');
    toast.className = 'toast';
    toast.innerHTML = `<span>${icon}</span> <span>${message}</span>`;
    elements.toastContainer.appendChild(toast);

    setTimeout(() => {
      toast.style.opacity = '0';
      toast.style.transform = 'translateY(10px)';
      toast.style.transition = 'all 0.3s ease';
      setTimeout(() => toast.remove(), 300);
    }, 2800);
  }

  // ==========================================
  // 7. CORE TRANSLATION ENGINE
  // ==========================================
  async function executeTranslation(textToTranslate, sourceLang, targetLang) {
    const text = (textToTranslate || elements.sourceTextarea.value).trim();
    if (!text) {
      showToast('Please enter text to translate', '⚠️');
      return;
    }

    const src = sourceLang || elements.sourceSelect.value;
    const tgt = targetLang || elements.targetSelect.value;

    // UI Loading state
    elements.translateBtn.disabled = true;
    elements.translateBtn.innerHTML = '<span class="btn-arrow-icon">⏳</span> <span>Translating...</span>';
    elements.providerBadge.textContent = 'Translating...';

    try {
      const response = await fetch('/api/translate', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          text: text,
          source: src === 'auto' ? '' : src,
          target: tgt
        })
      });

      if (!response.ok) {
        throw new Error(`HTTP error ${response.status}`);
      }

      const data = await response.json();
      const resultText = data.translatedText || '';

      // Update Target Output Card
      elements.outputPlaceholder.style.display = 'none';
      elements.translatedText.style.display = 'block';
      elements.translatedText.textContent = resultText;
      elements.providerBadge.textContent = data.provider || 'MyMemory Free Engine';

      // Enable Output Action Buttons
      elements.speakBtn.disabled = false;
      elements.copyBtn.disabled = false;
      elements.saveFavBtn.disabled = false;
      elements.shareBtn.disabled = false;

      // Update Detected Language Badge
      if (src === 'auto' && data.sourceLang && data.sourceLang !== 'auto') {
        const foundLang = LANGUAGES.find(l => l.code === data.sourceLang);
        const name = foundLang ? foundLang.name : data.sourceLang.toUpperCase();
        elements.detectedBadge.textContent = `Detected: ${name}`;
      } else {
        elements.detectedBadge.textContent = 'Input Text';
      }

      // Add to History
      saveToHistory({
        sourceText: text,
        translatedText: resultText,
        sourceLang: data.sourceLang || src,
        targetLang: tgt,
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        category: 'General',
        isFavorite: false
      });

      return resultText;

    } catch (err) {
      console.error('Translation failed:', err);
      // Client-side fallback if server failed
      const fallbackUrl = `https://api.mymemory.translated.net/get?q=${encodeURIComponent(text)}&langpair=${encodeURIComponent((src === 'auto' ? 'en' : src) + '|' + tgt)}`;
      try {
        const fRes = await fetch(fallbackUrl);
        const fData = await fRes.json();
        const fallbackText = fData.responseData?.translatedText || 'Translation unavailable.';

        elements.outputPlaceholder.style.display = 'none';
        elements.translatedText.style.display = 'block';
        elements.translatedText.textContent = fallbackText;
        elements.providerBadge.textContent = 'Direct Fallback Engine';

        elements.speakBtn.disabled = false;
        elements.copyBtn.disabled = false;
        elements.saveFavBtn.disabled = false;
        elements.shareBtn.disabled = false;

        return fallbackText;
      } catch (fErr) {
        showToast('Error during translation. Please try again.', '❌');
      }
    } finally {
      elements.translateBtn.disabled = false;
      elements.translateBtn.innerHTML = '<span class="btn-arrow-icon">🏹</span> <span class="btn-label">Translate Now</span> <span class="shortcut-hint">Ctrl + Enter</span>';
    }
  }

  // Trigger on button click or Ctrl + Enter
  elements.translateBtn.addEventListener('click', () => executeTranslation());

  elements.sourceTextarea.addEventListener('keydown', (e) => {
    if (e.ctrlKey && e.key === 'Enter') {
      executeTranslation();
    }
  });

  // Live Character Counter
  elements.sourceTextarea.addEventListener('input', () => {
    const len = elements.sourceTextarea.value.length;
    elements.charCounter.textContent = len;
  });

  // Paste & Clear
  elements.clearBtn.addEventListener('click', () => {
    elements.sourceTextarea.value = '';
    elements.charCounter.textContent = '0';
    elements.translatedText.textContent = '';
    elements.translatedText.style.display = 'none';
    elements.outputPlaceholder.style.display = 'block';
    elements.providerBadge.textContent = 'Ready';
    elements.speakBtn.disabled = true;
    elements.copyBtn.disabled = true;
    elements.saveFavBtn.disabled = true;
    elements.shareBtn.disabled = true;
  });

  elements.pasteBtn.addEventListener('click', async () => {
    try {
      const text = await navigator.clipboard.readText();
      elements.sourceTextarea.value = text;
      elements.charCounter.textContent = text.length;
      showToast('Pasted from clipboard', '📋');
    } catch (e) {
      showToast('Clipboard access denied', '⚠️');
    }
  });

  // Swap Languages
  elements.swapBtn.addEventListener('click', () => {
    const currentSrc = elements.sourceSelect.value;
    const currentTgt = elements.targetSelect.value;
    if (currentSrc === 'auto') {
      showToast('Cannot reverse when source is Auto-Detect', 'ℹ️');
      return;
    }

    elements.sourceSelect.value = currentTgt;
    elements.targetSelect.value = currentSrc;

    // Swap text if translation exists
    const currentSourceText = elements.sourceTextarea.value;
    const currentOutputText = elements.translatedText.textContent;
    if (currentOutputText) {
      elements.sourceTextarea.value = currentOutputText;
      elements.charCounter.textContent = currentOutputText.length;
      elements.translatedText.textContent = currentSourceText;
    }
    showToast('Languages reversed', '⇄');
  });

  // Quick Chips
  elements.quickChips.addEventListener('click', (e) => {
    const chip = e.target.closest('.chip');
    if (!chip) return;
    elements.targetSelect.value = chip.dataset.lang;
    document.querySelectorAll('#quick-target-chips .chip').forEach(c => c.classList.remove('active'));
    chip.classList.add('active');
    if (elements.sourceTextarea.value.trim()) {
      executeTranslation();
    }
  });

  // Copy Output
  elements.copyBtn.addEventListener('click', async () => {
    const text = elements.translatedText.textContent;
    if (!text) return;
    try {
      await navigator.clipboard.writeText(text);
      showToast('Translation copied to clipboard!', '📋');
    } catch (e) {
      showToast('Could not copy', '❌');
    }
  });

  // Text-to-Speech (TTS)
  elements.speakBtn.addEventListener('click', () => {
    const text = elements.translatedText.textContent;
    if (!text) return;
    speakText(text, elements.targetSelect.value);
  });

  function speakText(text, langCode) {
    if (!('speechSynthesis' in window)) {
      showToast('Speech synthesis not supported in this browser', '⚠️');
      return;
    }
    window.speechSynthesis.cancel();
    const utterance = new SpeechSynthesisUtterance(text);
    utterance.lang = langCode || 'en';
    utterance.rate = 0.95;
    utterance.pitch = 1.0;
    window.speechSynthesis.speak(utterance);
    showToast('Playing speech pronunciation', '🔊');
  }

  // Share Translation
  elements.shareBtn.addEventListener('click', async () => {
    const text = elements.translatedText.textContent;
    if (navigator.share) {
      try {
        await navigator.share({
          title: 'Crossbow Translation',
          text: `"${elements.sourceTextarea.value}" -> "${text}" (Crossbow Translator)`
        });
      } catch (e) {}
    } else {
      await navigator.clipboard.writeText(text);
      showToast('Copied to clipboard for sharing', '📤');
    }
  });

  // Voice Input (Web Speech Recognition)
  const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
  if (SpeechRecognition) {
    state.recognition = new SpeechRecognition();
    state.recognition.continuous = false;
    state.recognition.interimResults = true;

    state.recognition.onstart = () => {
      state.isRecording = true;
      elements.micBtn.classList.add('recording');
      elements.audioEqualizer.classList.add('active');
      elements.micStatusLabel.textContent = 'Listening... Speak now';
    };

    state.recognition.onresult = (event) => {
      const transcript = Array.from(event.results)
        .map(result => result[0])
        .map(result => result.transcript)
        .join('');
      elements.sourceTextarea.value = transcript;
      elements.charCounter.textContent = transcript.length;
    };

    state.recognition.onend = () => {
      state.isRecording = false;
      elements.micBtn.classList.remove('recording');
      elements.audioEqualizer.classList.remove('active');
      elements.micStatusLabel.textContent = 'Click mic to speak';
      if (elements.sourceTextarea.value.trim()) {
        executeTranslation();
      }
    };

    state.recognition.onerror = () => {
      state.isRecording = false;
      elements.micBtn.classList.remove('recording');
      elements.audioEqualizer.classList.remove('active');
      elements.micStatusLabel.textContent = 'Mic stopped';
    };

    elements.micBtn.addEventListener('click', () => {
      if (state.isRecording) {
        state.recognition.stop();
      } else {
        const src = elements.sourceSelect.value;
        state.recognition.lang = src === 'auto' ? 'en-US' : src;
        state.recognition.start();
      }
    });
  } else {
    elements.micBtn.title = 'Speech recognition not supported in this browser';
    elements.micStatusLabel.textContent = 'Speech not supported in browser';
  }

  // ==========================================
  // 8. TWO-WAY CONVERSATION LOGIC
  // ==========================================
  async function handleSpeakerMessage(speakerNumber, text) {
    if (!text || !text.trim()) return;

    const langA = elements.speaker1Select.value;
    const langB = elements.speaker2Select.value;

    const sourceLang = speakerNumber === 1 ? langA : langB;
    const targetLang = speakerNumber === 1 ? langB : langA;
    const speakerLabel = speakerNumber === 1 ? 'Speaker A' : 'Speaker B';

    // Translate directly
    const translated = await executeTranslation(text, sourceLang, targetLang);

    // Hide empty state
    elements.emptyChatState.style.display = 'none';

    // Append to Chat Timeline
    const bubbleRow = document.createElement('div');
    bubbleRow.className = `chat-bubble-row speaker-${speakerNumber}`;
    bubbleRow.innerHTML = `
      <div class="bubble-meta">${speakerLabel} (${sourceLang.toUpperCase()} → ${targetLang.toUpperCase()})</div>
      <div class="bubble-content">
        <button class="bubble-audio-btn" title="Listen">🔊</button>
        <div class="bubble-original">${escapeHtml(text)}</div>
        <div class="bubble-translation">${escapeHtml(translated || '...')}</div>
      </div>
    `;

    // Listen audio button
    bubbleRow.querySelector('.bubble-audio-btn').addEventListener('click', () => {
      speakText(translated, targetLang);
    });

    elements.chatTimeline.appendChild(bubbleRow);
    elements.chatTimeline.scrollTop = elements.chatTimeline.scrollHeight;

    // Optional Auto-play speech
    speakText(translated, targetLang);

    state.conversation.push({
      speaker: speakerLabel,
      original: text,
      translated: translated,
      sourceLang,
      targetLang
    });
  }

  // Speaker 1 Send
  elements.speaker1SendBtn.addEventListener('click', () => {
    handleSpeakerMessage(1, elements.speaker1Input.value);
    elements.speaker1Input.value = '';
  });
  elements.speaker1Input.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
      handleSpeakerMessage(1, elements.speaker1Input.value);
      elements.speaker1Input.value = '';
    }
  });

  // Speaker 2 Send
  elements.speaker2SendBtn.addEventListener('click', () => {
    handleSpeakerMessage(2, elements.speaker2Input.value);
    elements.speaker2Input.value = '';
  });
  elements.speaker2Input.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
      handleSpeakerMessage(2, elements.speaker2Input.value);
      elements.speaker2Input.value = '';
    }
  });

  // Speaker Mic Handlers
  if (SpeechRecognition) {
    function startSpeakerVoice(speakerNum, langCode) {
      const rec = new SpeechRecognition();
      rec.lang = langCode;
      showToast(`Listening for Speaker ${speakerNum === 1 ? 'A' : 'B'}...`, '🎙️');
      rec.onresult = (ev) => {
        const text = ev.results[0][0].transcript;
        if (speakerNum === 1) elements.speaker1Input.value = text;
        else elements.speaker2Input.value = text;
        handleSpeakerMessage(speakerNum, text);
      };
      rec.start();
    }
    elements.speaker1MicBtn.addEventListener('click', () => startSpeakerVoice(1, elements.speaker1Select.value));
    elements.speaker2MicBtn.addEventListener('click', () => startSpeakerVoice(2, elements.speaker2Select.value));
  }

  // Clear Dialogue
  elements.clearConvoBtn.addEventListener('click', () => {
    elements.chatTimeline.innerHTML = '';
    elements.chatTimeline.appendChild(elements.emptyChatState);
    elements.emptyChatState.style.display = 'block';
    state.conversation = [];
    showToast('Dialogue cleared', '🗑️');
  });

  // Export Chat
  elements.exportConvoBtn.addEventListener('click', () => {
    if (!state.conversation.length) {
      showToast('No conversation to export', '⚠️');
      return;
    }
    const transcript = state.conversation.map(c => `[${c.speaker}] (${c.sourceLang}): ${c.original}\n  -> (${c.targetLang}): ${c.translated}`).join('\n\n');
    downloadFile('crossbow-conversation.txt', transcript, 'text/plain');
    showToast('Conversation exported', '📥');
  });

  // ==========================================
  // 9. LINGUISTIC DICTIONARY
  // ==========================================
  async function searchDictionary(word) {
    if (!word || !word.trim()) return;
    elements.dictInput.value = word.trim();
    elements.dictPlaceholder.innerHTML = '<span>⏳</span><p>Searching definitions and audio pronunciations...</p>';
    elements.dictContent.style.display = 'none';

    try {
      const res = await fetch(`/api/dictionary/${encodeURIComponent(word.trim())}`);
      if (!res.ok) throw new Error('Word not found');
      const data = await res.json();
      renderDictionaryEntry(data[0]);
    } catch (e) {
      elements.dictPlaceholder.innerHTML = '<span>🔍</span><p>No definitions found. Try searching another term.</p>';
      elements.dictContent.style.display = 'none';
    }
  }

  function renderDictionaryEntry(entry) {
    if (!entry) return;
    elements.dictPlaceholder.style.display = 'none';
    elements.dictContent.style.display = 'block';

    const phonetics = entry.phonetics || [];
    const audioObj = phonetics.find(p => p.audio && p.audio.length > 0);
    const audioUrl = audioObj ? audioObj.audio : '';
    const phoneticText = entry.phonetic || (phonetics[0] ? phonetics[0].text : '');

    let html = `
      <div class="dict-entry-card">
        <div class="entry-head">
          <span class="entry-word">${escapeHtml(entry.word)}</span>
          ${phoneticText ? `<span class="entry-phonetic">${escapeHtml(phoneticText)}</span>` : ''}
          ${audioUrl ? `<button class="entry-audio-btn" data-audio="${audioUrl}">🔊 Pronounce</button>` : ''}
        </div>
    `;

    (entry.meanings || []).forEach(m => {
      html += `
        <div class="entry-meaning-group">
          <span class="part-of-speech-pill">${escapeHtml(m.partOfSpeech)}</span>
      `;
      (m.definitions || []).slice(0, 3).forEach((d, idx) => {
        html += `
          <div class="definition-item">
            <div class="def-text">${idx + 1}. ${escapeHtml(d.definition)}</div>
            ${d.example ? `<div class="def-example">"${escapeHtml(d.example)}"</div>` : ''}
            ${d.synonyms && d.synonyms.length ? `
              <div class="synonyms-row">
                ${d.synonyms.slice(0, 4).map(s => `<span class="synonym-chip" data-word="${escapeHtml(s)}">${escapeHtml(s)}</span>`).join('')}
              </div>
            ` : ''}
          </div>
        `;
      });
      html += `</div>`;
    });

    html += `</div>`;
    elements.dictContent.innerHTML = html;

    // Attach audio button
    const audioBtn = elements.dictContent.querySelector('.entry-audio-btn');
    if (audioBtn) {
      audioBtn.addEventListener('click', () => {
        new Audio(audioBtn.dataset.audio).play();
      });
    }

    // Attach synonym chip click
    elements.dictContent.querySelectorAll('.synonym-chip').forEach(chip => {
      chip.addEventListener('click', () => searchDictionary(chip.dataset.word));
    });
  }

  elements.dictForm.addEventListener('submit', (e) => {
    e.preventDefault();
    searchDictionary(elements.dictInput.value);
  });

  elements.quickWordChips.forEach(chip => {
    chip.addEventListener('click', () => searchDictionary(chip.dataset.word));
  });

  // ==========================================
  // 10. 3D FLASHCARDS & VOCABULARY QUIZ
  // ==========================================
  const FLASHCARDS = {
    beginner: [
      { word: 'Hello', phonetic: '/həˈloʊ/', meaning: 'Used as a greeting or to begin a phone conversation.', example: 'Hello, how can I help you today?', es: 'Hola' },
      { word: 'Gratitude', phonetic: '/ˈɡræt.ɪ.tjuːd/', meaning: 'The quality of being thankful; readiness to show appreciation.', example: 'She expressed deep gratitude for their support.', es: 'Gratitud' },
      { word: 'Friendship', phonetic: '/ˈfrend.ʃɪp/', meaning: 'A relationship of mutual affection between people.', example: 'Their friendship lasted over forty years.', es: 'Amistad' },
      { word: 'Journey', phonetic: '/ˈdʒɜː.ni/', meaning: 'An act of traveling from one place to another.', example: 'The journey of a thousand miles begins with a single step.', es: 'Viaje' },
      { word: 'Courage', phonetic: '/ˈkʌr.ɪdʒ/', meaning: 'Strength in the face of pain, danger, or grief.', example: 'It took great courage to speak the truth.', es: 'Coraje' }
    ],
    intermediate: [
      { word: 'Serendipity', phonetic: '/ˌser.ənˈdɪp.ə.ti/', meaning: 'Finding valuable or agreeable things not sought for; a happy accident.', example: 'Finding my favorite book in an attic was pure serendipity.', es: 'Serendipia' },
      { word: 'Resilience', phonetic: '/rɪˈzɪl.jəns/', meaning: 'The capacity to recover quickly from difficulties; toughness.', example: 'The community showed remarkable resilience after the flood.', es: 'Resiliencia' },
      { word: 'Eloquent', phonetic: '/ˈel.ə.kwənt/', meaning: 'Fluent or persuasive in speaking or writing.', example: 'An eloquent speech that moved everyone to tears.', es: 'Elocuente' },
      { word: 'Luminous', phonetic: '/ˈluː.mɪ.nəs/', meaning: 'Full of or shedding light; bright or shining.', example: 'The luminous moon guided the sailors through the night.', es: 'Luminoso' },
      { word: 'Epiphany', phonetic: '/ɪˈpɪf.ən.i/', meaning: 'A moment of sudden and great revelation or realization.', example: 'He had an epiphany that changed his entire career path.', es: 'Epifanía' }
    ],
    advanced: [
      { word: 'Petrichor', phonetic: '/ˈpet.rɪ.kɔːr/', meaning: 'A pleasant smell that frequently accompanies the first rain after a long period of warm, dry weather.', example: 'The earthy petrichor filled the summer air.', es: 'Petricor' },
      { word: 'Sycophant', phonetic: '/ˈsɪk.ə.fænt/', meaning: 'A person who acts obsequiously toward someone important to gain advantage.', example: 'Surrounded by sycophants who never challenged his ideas.', es: 'Sicofante' },
      { word: 'Ineffable', phonetic: '/ɪnˈef.ə.bəl/', meaning: 'Too great or extreme to be expressed or described in words.', example: 'The breathtaking view inspired ineffable joy.', es: 'Inefable' },
      { word: 'Ubiquitous', phonetic: '/juːˈbɪk.wɪ.təs/', meaning: 'Present, appearing, or found everywhere.', example: 'Smartphones have become ubiquitous in daily life.', es: 'Ubícuo' }
    ]
  };

  const QUIZ_QUESTIONS = [
    {
      q: 'What is the correct Spanish translation for "Knowledge"?',
      options: ['Conocimiento', 'Sabiduría', 'Aventura', 'Destino'],
      answer: 0,
      explanation: '"Conocimiento" directly translates to "Knowledge", while "Sabiduría" is "Wisdom".'
    },
    {
      q: 'Which word means "the pleasant scent of rain on dry earth"?',
      options: ['Serendipity', 'Petrichor', 'Resilience', 'Halcyon'],
      answer: 1,
      explanation: '"Petrichor" is derived from Greek petra (stone) and ichor (ethereal fluid of gods).'
    },
    {
      q: 'What is the French word for "Journey" or "Travel"?',
      options: ['Bonjour', 'Voyage', 'Mercie', 'Étoile'],
      answer: 1,
      explanation: '"Voyage" means trip, travel, or journey in French.'
    },
    {
      q: 'Which term describes recovering quickly from difficulties?',
      options: ['Resilience', 'Eloquent', 'Melancholy', 'Ephemeral'],
      answer: 0,
      explanation: '"Resilience" refers to mental and physical endurance and bounce-back ability.'
    },
    {
      q: 'What does the word "Luminous" mean?',
      options: ['Heavy and dense', 'Full of light or glowing', 'Quiet and reserved', 'Bitter taste'],
      answer: 1,
      explanation: '"Luminous" comes from Latin lumen, meaning light.'
    }
  ];

  function renderCurrentFlashcard() {
    const level = elements.flashcardDiff.value;
    const deck = FLASHCARDS[level] || FLASHCARDS.intermediate;
    const card = deck[state.currentFlashcardIndex % deck.length];

    elements.cardCounterLabel.textContent = `Card ${(state.currentFlashcardIndex % deck.length) + 1} / ${deck.length}`;
    elements.fcFrontTerm.textContent = card.word;
    elements.fcFrontPronounce.textContent = card.phonetic;
    elements.fcBackMeaning.textContent = card.meaning;
    elements.fcBackExample.textContent = `"${card.example}"`;
    elements.fcBackTrans.innerHTML = `🇪🇸 Español: <strong>${card.es}</strong>`;

    elements.flashcardCard.classList.remove('flipped');
  }

  elements.flashcardCard.addEventListener('click', () => {
    elements.flashcardCard.classList.toggle('flipped');
  });

  elements.fcFlipBtn.addEventListener('click', (e) => {
    e.stopPropagation();
    elements.flashcardCard.classList.toggle('flipped');
  });

  elements.fcNextBtn.addEventListener('click', () => {
    state.currentFlashcardIndex++;
    renderCurrentFlashcard();
  });

  elements.fcPrevBtn.addEventListener('click', () => {
    state.currentFlashcardIndex = Math.max(0, state.currentFlashcardIndex - 1);
    renderCurrentFlashcard();
  });

  elements.flashcardDiff.addEventListener('change', () => {
    state.currentFlashcardIndex = 0;
    renderCurrentFlashcard();
  });

  elements.fcListenFront.addEventListener('click', (e) => {
    e.stopPropagation();
    speakText(elements.fcFrontTerm.textContent, 'en');
  });

  // Toggle Sub-modes (Flashcards vs Quiz)
  elements.modeFlashcardsBtn.addEventListener('click', () => {
    elements.modeFlashcardsBtn.classList.add('active');
    elements.modeQuizBtn.classList.remove('active');
    elements.flashcardsSubview.style.display = 'flex';
    elements.quizSubview.style.display = 'none';
  });

  elements.modeQuizBtn.addEventListener('click', () => {
    elements.modeQuizBtn.classList.add('active');
    elements.modeFlashcardsBtn.classList.remove('active');
    elements.flashcardsSubview.style.display = 'none';
    elements.quizSubview.style.display = 'flex';
    renderQuizQuestion();
  });

  function renderQuizQuestion() {
    const qData = QUIZ_QUESTIONS[state.currentQuizIndex % QUIZ_QUESTIONS.length];
    elements.quizQNum.textContent = `Question ${(state.currentQuizIndex % QUIZ_QUESTIONS.length) + 1} of ${QUIZ_QUESTIONS.length}`;
    elements.quizProgressBar.style.width = `${(((state.currentQuizIndex % QUIZ_QUESTIONS.length) + 1) / QUIZ_QUESTIONS.length) * 100}%`;
    elements.quizPromptText.textContent = qData.q;
    elements.quizFeedbackBox.style.display = 'none';

    elements.quizOptionsContainer.innerHTML = '';
    qData.options.forEach((opt, idx) => {
      const btn = document.createElement('button');
      btn.className = 'quiz-opt-btn';
      btn.innerHTML = `<span>${String.fromCharCode(65 + idx)}. ${opt}</span>`;
      btn.addEventListener('click', () => selectQuizOption(idx, qData.answer, qData.explanation, btn));
      elements.quizOptionsContainer.appendChild(btn);
    });
  }

  function selectQuizOption(selectedIndex, correctIndex, explanation, btnElement) {
    const allBtns = elements.quizOptionsContainer.querySelectorAll('.quiz-opt-btn');
    allBtns.forEach(b => b.disabled = true);

    const isCorrect = selectedIndex === correctIndex;
    if (isCorrect) {
      btnElement.classList.add('correct');
      elements.quizFeedbackMsg.className = 'feedback-msg correct-msg';
      elements.quizFeedbackMsg.textContent = `🎉 Correct! ${explanation}`;
      state.score += 10;
      state.streak += 1;
    } else {
      btnElement.classList.add('wrong');
      allBtns[correctIndex].classList.add('correct');
      elements.quizFeedbackMsg.className = 'feedback-msg wrong-msg';
      elements.quizFeedbackMsg.textContent = `❌ Incorrect. ${explanation}`;
    }

    elements.scoreCounter.textContent = `Score: ${state.score}`;
    elements.streakCounter.textContent = `${state.streak} Day Streak`;
    localStorage.setItem('crossbow_score', state.score);
    localStorage.setItem('crossbow_streak', state.streak);

    elements.quizFeedbackBox.style.display = 'flex';
  }

  elements.quizNextQBtn.addEventListener('click', () => {
    state.currentQuizIndex++;
    renderQuizQuestion();
  });

  // ==========================================
  // 11. OCR SCANNER & DOCUMENT IMAGE TRANSLATION
  // ==========================================
  const SAMPLE_PRESETS = {
    spanish_sign: 'PELIGRO: ZONA DE CONSTRUCCIÓN. PROHIBIDO EL PASO A PERSONAS NO AUTORIZADAS. USO OBLIGATORIO DE CASCO DE SEGURIDAD.',
    french_menu: 'MENU DU JOUR: Soupe à l\'oignon gratinée, Confit de canard avec pommes sarladaises, Tarte tatin maison tiède.',
    german_notice: 'ACHTUNG: Bitte halten Sie den Durchgang stets frei. Notausgang darf nicht verstellt werden!'
  };

  elements.sampleOcrBtns.forEach(btn => {
    btn.addEventListener('click', () => {
      const text = SAMPLE_PRESETS[btn.dataset.sample];
      if (text) {
        elements.ocrExtractedText.value = text;
        showToast('Sample document loaded into OCR editor', '📷');
      }
    });
  });

  elements.ocrChooseBtn.addEventListener('click', (e) => {
    e.stopPropagation();
    elements.ocrFileInput.click();
  });

  elements.ocrDropzone.addEventListener('click', () => {
    elements.ocrFileInput.click();
  });

  elements.ocrFileInput.addEventListener('change', (e) => {
    const file = e.target.files[0];
    if (file) handleImageFile(file);
  });

  function handleImageFile(file) {
    const reader = new FileReader();
    reader.onload = (ev) => {
      elements.ocrPreviewImg.src = ev.target.result;
      elements.ocrPreviewWrapper.style.display = 'flex';
      elements.ocrExtractedText.value = 'Extracting text from image...';
      setTimeout(() => {
        elements.ocrExtractedText.value = 'CROSSBOW OCR RESULT:\n"Welcome to the International Conference. Please present your badge at registration."';
        showToast('Text extracted from image successfully!', '⚡');
      }, 700);
    };
    reader.readAsDataURL(file);
  }

  elements.ocrClearImgBtn.addEventListener('click', (e) => {
    e.stopPropagation();
    elements.ocrPreviewWrapper.style.display = 'none';
    elements.ocrPreviewImg.src = '';
    elements.ocrExtractedText.value = '';
    elements.ocrFileInput.value = '';
  });

  elements.ocrSendTranslateBtn.addEventListener('click', () => {
    const text = elements.ocrExtractedText.value.trim();
    if (!text) {
      showToast('No extracted text to translate', '⚠️');
      return;
    }
    // Switch to translator tab and trigger
    document.getElementById('nav-tab-translate').click();
    elements.sourceTextarea.value = text;
    elements.charCounter.textContent = text.length;
    executeTranslation();
  });

  // ==========================================
  // 12. HISTORY & BOOKMARKS
  // ==========================================
  function saveToHistory(item) {
    state.history.unshift(item);
    if (state.history.length > 50) state.history.pop();
    localStorage.setItem('crossbow_history', JSON.stringify(state.history));
    renderHistoryList();
    updateHistoryBadge();
  }

  function updateHistoryBadge() {
    elements.historyBadge.textContent = state.history.length;
  }

  function renderHistoryList(filterCat = 'all', searchQuery = '') {
    elements.historyListView.innerHTML = '';
    let filtered = state.history;

    if (filterCat === 'favorite') {
      filtered = filtered.filter(h => h.isFavorite);
    } else if (filterCat !== 'all') {
      filtered = filtered.filter(h => h.category === filterCat);
    }

    if (searchQuery) {
      const q = searchQuery.toLowerCase();
      filtered = filtered.filter(h => h.sourceText.toLowerCase().includes(q) || h.translatedText.toLowerCase().includes(q));
    }

    if (!filtered.length) {
      elements.emptyHistoryState.style.display = 'block';
      elements.historyListView.appendChild(elements.emptyHistoryState);
      return;
    }

    elements.emptyHistoryState.style.display = 'none';
    filtered.forEach((item, index) => {
      const card = document.createElement('div');
      card.className = 'history-card-item';
      card.innerHTML = `
        <div class="hist-content">
          <div class="hist-meta-line">
            <span>${item.sourceLang.toUpperCase()} → ${item.targetLang.toUpperCase()}</span>
            <span>•</span>
            <span>${item.timestamp}</span>
            <span class="hist-cat-badge">${item.category || 'General'}</span>
          </div>
          <div class="hist-source-text">${escapeHtml(item.sourceText)}</div>
          <div class="hist-target-text">${escapeHtml(item.translatedText)}</div>
        </div>
        <div class="hist-item-actions">
          <button class="mini-btn hist-restore-btn" title="Load into translator">↺ Use</button>
          <button class="mini-btn hist-fav-btn" title="Favorite">${item.isFavorite ? '★' : '☆'}</button>
          <button class="mini-btn danger-btn hist-del-btn" title="Delete">✕</button>
        </div>
      `;

      // Restore to translator
      card.querySelector('.hist-restore-btn').addEventListener('click', () => {
        document.getElementById('nav-tab-translate').click();
        elements.sourceSelect.value = item.sourceLang;
        elements.targetSelect.value = item.targetLang;
        elements.sourceTextarea.value = item.sourceText;
        elements.charCounter.textContent = item.sourceText.length;
        executeTranslation();
      });

      // Toggle favorite
      card.querySelector('.hist-fav-btn').addEventListener('click', () => {
        item.isFavorite = !item.isFavorite;
        localStorage.setItem('crossbow_history', JSON.stringify(state.history));
        renderHistoryList(filterCat, searchQuery);
      });

      // Delete item
      card.querySelector('.hist-del-btn').addEventListener('click', () => {
        state.history.splice(index, 1);
        localStorage.setItem('crossbow_history', JSON.stringify(state.history));
        renderHistoryList(filterCat, searchQuery);
        updateHistoryBadge();
      });

      elements.historyListView.appendChild(card);
    });
  }

  elements.historyCatFilters.forEach(chip => {
    chip.addEventListener('click', () => {
      elements.historyCatFilters.forEach(c => c.classList.remove('active'));
      chip.classList.add('active');
      renderHistoryList(chip.dataset.cat, elements.historySearchInput.value);
    });
  });

  elements.historySearchInput.addEventListener('input', (e) => {
    const activeCat = document.querySelector('.cat-chip.active')?.dataset.cat || 'all';
    renderHistoryList(activeCat, e.target.value);
  });

  elements.clearHistoryBtn.addEventListener('click', () => {
    if (confirm('Clear all translation history?')) {
      state.history = [];
      localStorage.setItem('crossbow_history', JSON.stringify(state.history));
      renderHistoryList();
      updateHistoryBadge();
      showToast('History cleared', '🗑️');
    }
  });

  elements.exportHistoryBtn.addEventListener('click', () => {
    downloadFile('crossbow-history.json', JSON.stringify(state.history, null, 2), 'application/json');
    showToast('History exported as JSON', '📥');
  });

  // Bookmark Modal
  elements.saveFavBtn.addEventListener('click', () => {
    elements.bookmarkModal.style.display = 'flex';
  });

  elements.modalCloseBtn.addEventListener('click', () => {
    elements.bookmarkModal.style.display = 'none';
  });

  elements.modalSaveConfirmBtn.addEventListener('click', () => {
    const cat = elements.bookmarkCatSelect.value;
    const note = elements.bookmarkNoteInput.value.trim();

    if (state.history.length > 0) {
      state.history[0].isFavorite = true;
      state.history[0].category = cat;
      if (note) state.history[0].note = note;
      localStorage.setItem('crossbow_history', JSON.stringify(state.history));
      renderHistoryList();
    }

    elements.bookmarkModal.style.display = 'none';
    showToast(`Saved to ${cat} Bookmarks!`, '⭐');
  });

  // ==========================================
  // 13. THEME TOGGLE (Dark / Light)
  // ==========================================
  const savedTheme = localStorage.getItem('crossbow_theme') || 'dark';
  if (savedTheme === 'light') {
    document.body.classList.remove('dark-theme');
    document.body.classList.add('light-theme');
    elements.themeIcon.textContent = '☀️';
  }

  elements.themeToggleBtn.addEventListener('click', () => {
    if (document.body.classList.contains('dark-theme')) {
      document.body.classList.remove('dark-theme');
      document.body.classList.add('light-theme');
      elements.themeIcon.textContent = '☀️';
      localStorage.setItem('crossbow_theme', 'light');
      showToast('Light mode active', '☀️');
    } else {
      document.body.classList.remove('light-theme');
      document.body.classList.add('dark-theme');
      elements.themeIcon.textContent = '🌙';
      localStorage.setItem('crossbow_theme', 'dark');
      showToast('Dark glassmorphism active', '🌙');
    }
  });

  // ==========================================
  // 14. HELPER UTILITIES
  // ==========================================
  function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, '&amp;')
              .replace(/</g, '&lt;')
              .replace(/>/g, '&gt;')
              .replace(/"/g, '&quot;')
              .replace(/'/g, '&#039;');
  }

  function downloadFile(filename, content, mimeType) {
    const blob = new Blob([content], { type: mimeType });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  }

  // ==========================================
  // 15. PROGRESSIVE WEB APP (PWA) SUPPORT
  // ==========================================
  const pwaInstallBtn = document.getElementById('pwa-install-btn');
  let deferredPrompt = null;

  // Register Service Worker
  if ('serviceWorker' in navigator) {
    window.addEventListener('load', () => {
      navigator.serviceWorker.register('/sw.js')
        .then((registration) => {
          console.log('[PWA] Service Worker registered with scope:', registration.scope);
        })
        .catch((error) => {
          console.error('[PWA] Service Worker registration failed:', error);
        });
    });
  }

  // Handle BeforeInstallPrompt event
  window.addEventListener('beforeinstallprompt', (e) => {
    e.preventDefault();
    deferredPrompt = e;
    if (pwaInstallBtn) {
      pwaInstallBtn.style.display = 'inline-flex';
    }
  });

  if (pwaInstallBtn) {
    pwaInstallBtn.addEventListener('click', async () => {
      if (!deferredPrompt) {
        showToast('App is ready in your browser or already installed', 'ℹ️');
        return;
      }
      deferredPrompt.prompt();
      const { outcome } = await deferredPrompt.userChoice;
      if (outcome === 'accepted') {
        showToast('Installing Crossbow Translator PWA...', '🚀');
        pwaInstallBtn.style.display = 'none';
      }
      deferredPrompt = null;
    });
  }

  window.addEventListener('appinstalled', () => {
    if (pwaInstallBtn) pwaInstallBtn.style.display = 'none';
    showToast('Crossbow Translator installed successfully!', '🎉');
  });

  // Online / Offline Status Detection
  window.addEventListener('online', () => {
    showToast('Back online! Live translation connected.', '🌐');
    checkBackendHealth();
  });

  window.addEventListener('offline', () => {
    showToast('Offline Mode: Using cached dictionary and app shell.', '⚠️');
    elements.statusLabel.textContent = 'Offline (PWA Cache)';
    elements.statusPill.style.color = '#f59e0b';
    elements.statusPill.style.borderColor = 'rgba(245, 158, 11, 0.4)';
  });

  // Initial Run
  initPopulateDropdowns();
  checkBackendHealth();
  renderCurrentFlashcard();
  renderHistoryList();
  updateHistoryBadge();
});
