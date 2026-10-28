package com.crossbow.translator.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.crossbow.translator.CrossbowApplication
import com.crossbow.translator.data.local.entity.TranslationHistoryEntity
import com.crossbow.translator.domain.model.Language
import com.crossbow.translator.domain.model.TranslationData
import com.crossbow.translator.domain.model.TranslationUiState
import com.crossbow.translator.domain.model.VocabularyCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as CrossbowApplication
    private val translationRepo = app.translationRepository
    private val favoritesRepo = app.favoritesRepository
    private val historyRepo = app.historyRepository
    private val practiceRepo = app.practiceRepository
    private val appPreferences = app.appPreferences
    val clipboardHelper = app.clipboardHelper
    val textToSpeechHelper = app.textToSpeechHelper
    val speechRecognizerHelper = app.speechRecognizerHelper
    val networkMonitor = app.networkMonitor

    var inputText = MutableStateFlow("")
        private set

    private val _sourceLang = MutableStateFlow(Language.AUTO)
    val sourceLang: StateFlow<Language> = _sourceLang.asStateFlow()

    private val _targetLang = MutableStateFlow(Language.findByCode("es"))
    val targetLang: StateFlow<Language> = _targetLang.asStateFlow()

    private val _translationState = MutableStateFlow<TranslationUiState>(TranslationUiState.Idle)
    val translationState: StateFlow<TranslationUiState> = _translationState.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    val recentHistory: StateFlow<List<TranslationHistoryEntity>> = historyRepo.recentHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyWords: StateFlow<List<VocabularyCard>> = practiceRepo.getDailyWords(3)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            appPreferences.defaultTargetLang.collect { code ->
                _targetLang.value = Language.findByCode(code)
            }
        }
    }

    fun onInputTextChanged(text: String) {
        inputText.value = text
        if (text.isBlank()) {
            _translationState.value = TranslationUiState.Idle
        }
    }

    fun setSourceLanguage(lang: Language) {
        _sourceLang.value = lang
        if (inputText.value.isNotBlank()) translate()
    }

    fun setTargetLanguage(lang: Language) {
        _targetLang.value = lang
        if (inputText.value.isNotBlank()) translate()
    }

    fun swapLanguages() {
        if (_sourceLang.value.code == "auto") {
            // Can't swap auto to target, set source to current target and target to English
            val oldTarget = _targetLang.value
            _sourceLang.value = oldTarget
            _targetLang.value = Language.findByCode("en")
        } else {
            val temp = _sourceLang.value
            _sourceLang.value = _targetLang.value
            _targetLang.value = temp
        }

        // Also swap text if translated text exists
        val currentResult = (_translationState.value as? TranslationUiState.Success)?.data
        if (currentResult != null) {
            inputText.value = currentResult.translatedText
            translate()
        }
    }

    fun translate() {
        val text = inputText.value.trim()
        if (text.isEmpty()) return

        viewModelScope.launch {
            _translationState.value = TranslationUiState.Loading
            val result = translationRepo.translate(text, _sourceLang.value, _targetLang.value)
            _translationState.value = result

            if (result is TranslationUiState.Success) {
                checkIfFavorite(result.data.sourceText, result.data.targetLang.code)
            }
        }
    }

    private fun checkIfFavorite(sourceText: String, targetLang: String) {
        viewModelScope.launch {
            favoritesRepo.isFavorite(sourceText, targetLang).collect { fav ->
                _isFavorite.value = fav
            }
        }
    }

    fun toggleFavorite() {
        val current = (_translationState.value as? TranslationUiState.Success)?.data ?: return
        viewModelScope.launch {
            translationRepo.toggleFavorite(current, _isFavorite.value)
            _isFavorite.value = !_isFavorite.value
        }
    }

    fun pasteFromClipboard() {
        val text = clipboardHelper.getClipboardText()
        if (!text.isNullOrBlank()) {
            inputText.value = text
            translate()
        }
    }

    fun clearInput() {
        inputText.value = ""
        _translationState.value = TranslationUiState.Idle
    }

    fun playAudio(text: String) {
        textToSpeechHelper.speak(text, _targetLang.value.code)
    }

    fun reuseHistory(item: TranslationHistoryEntity) {
        _sourceLang.value = Language.findByCode(item.sourceLangCode)
        _targetLang.value = Language.findByCode(item.targetLangCode)
        inputText.value = item.sourceText
        _translationState.value = TranslationUiState.Success(
            TranslationData(
                sourceText = item.sourceText,
                translatedText = item.translatedText,
                sourceLang = Language.findByCode(item.sourceLangCode),
                targetLang = Language.findByCode(item.targetLangCode)
            )
        )
    }
}
