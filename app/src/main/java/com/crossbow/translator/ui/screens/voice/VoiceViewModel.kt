package com.crossbow.translator.ui.screens.voice

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.crossbow.translator.CrossbowApplication
import com.crossbow.translator.domain.model.Language
import com.crossbow.translator.domain.model.TranslationUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VoiceViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as CrossbowApplication
    private val translationRepo = app.translationRepository
    val speechHelper = app.speechRecognizerHelper
    val ttsHelper = app.textToSpeechHelper
    val clipboardHelper = app.clipboardHelper

    private val _sourceLang = MutableStateFlow(Language.findByCode("en"))
    val sourceLang: StateFlow<Language> = _sourceLang.asStateFlow()

    private val _targetLang = MutableStateFlow(Language.findByCode("es"))
    val targetLang: StateFlow<Language> = _targetLang.asStateFlow()

    private val _spokenText = MutableStateFlow("")
    val spokenText: StateFlow<String> = _spokenText.asStateFlow()

    private val _translationState = MutableStateFlow<TranslationUiState>(TranslationUiState.Idle)
    val translationState: StateFlow<TranslationUiState> = _translationState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun setSourceLanguage(lang: Language) {
        _sourceLang.value = lang
    }

    fun setTargetLanguage(lang: Language) {
        _targetLang.value = lang
    }

    fun swapLanguages() {
        val temp = _sourceLang.value
        _sourceLang.value = _targetLang.value
        _targetLang.value = temp
    }

    fun toggleListening() {
        if (speechHelper.isListening.value) {
            speechHelper.stopListening()
        } else {
            _errorMessage.value = null
            speechHelper.startListening(
                languageCode = _sourceLang.value.code,
                onResult = { text ->
                    _spokenText.value = text
                    translateSpokenText(text)
                },
                onPartialResult = { partial ->
                    _spokenText.value = partial
                },
                onError = { err ->
                    _errorMessage.value = err
                }
            )
        }
    }

    private fun translateSpokenText(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            _translationState.value = TranslationUiState.Loading
            val result = translationRepo.translate(text, _sourceLang.value, _targetLang.value)
            _translationState.value = result

            if (result is TranslationUiState.Success) {
                // Auto play audio of translated speech
                ttsHelper.speak(result.data.translatedText, _targetLang.value.code)
            }
        }
    }

    fun replayAudio() {
        val current = (_translationState.value as? TranslationUiState.Success)?.data?.translatedText ?: return
        ttsHelper.speak(current, _targetLang.value.code)
    }

    override fun onCleared() {
        super.onCleared()
        speechHelper.stopListening()
        ttsHelper.stop()
    }
}
