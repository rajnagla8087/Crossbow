package com.crossbow.translator.ui.screens.conversation

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

data class ConversationMessage(
    val id: Long = System.currentTimeMillis(),
    val speakerId: Int, // 1 or 2
    val originalText: String,
    val translatedText: String,
    val sourceLang: Language,
    val targetLang: Language,
    val timestamp: Long = System.currentTimeMillis()
)

class ConversationViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as CrossbowApplication
    private val translationRepo = app.translationRepository
    val speechHelper = app.speechRecognizerHelper
    val ttsHelper = app.textToSpeechHelper
    val clipboardHelper = app.clipboardHelper

    private val _speaker1Lang = MutableStateFlow(Language.findByCode("en"))
    val speaker1Lang: StateFlow<Language> = _speaker1Lang.asStateFlow()

    private val _speaker2Lang = MutableStateFlow(Language.findByCode("es"))
    val speaker2Lang: StateFlow<Language> = _speaker2Lang.asStateFlow()

    private val _activeSpeaker = MutableStateFlow<Int?>(null)
    val activeSpeaker: StateFlow<Int?> = _activeSpeaker.asStateFlow()

    private val _messages = MutableStateFlow<List<ConversationMessage>>(emptyList())
    val messages: StateFlow<List<ConversationMessage>> = _messages.asStateFlow()

    private val _autoPlayAudio = MutableStateFlow(true)
    val autoPlayAudio: StateFlow<Boolean> = _autoPlayAudio.asStateFlow()

    fun setSpeaker1Language(lang: Language) {
        _speaker1Lang.value = lang
    }

    fun setSpeaker2Language(lang: Language) {
        _speaker2Lang.value = lang
    }

    fun toggleAutoPlayAudio() {
        _autoPlayAudio.value = !_autoPlayAudio.value
    }

    fun startListeningForSpeaker(speakerId: Int) {
        if (speechHelper.isListening.value && _activeSpeaker.value == speakerId) {
            speechHelper.stopListening()
            _activeSpeaker.value = null
            return
        }

        _activeSpeaker.value = speakerId
        val (src, dst) = if (speakerId == 1) {
            _speaker1Lang.value to _speaker2Lang.value
        } else {
            _speaker2Lang.value to _speaker1Lang.value
        }

        speechHelper.startListening(
            languageCode = src.code,
            onResult = { recognizedText ->
                _activeSpeaker.value = null
                translateAndAddMessage(speakerId, recognizedText, src, dst)
            },
            onError = {
                _activeSpeaker.value = null
            }
        )
    }

    private fun translateAndAddMessage(
        speakerId: Int,
        text: String,
        src: Language,
        dst: Language
    ) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val res = translationRepo.translate(text, src, dst)
            val translatedText = if (res is TranslationUiState.Success) {
                res.data.translatedText
            } else {
                text // Fallback
            }

            val msg = ConversationMessage(
                speakerId = speakerId,
                originalText = text,
                translatedText = translatedText,
                sourceLang = src,
                targetLang = dst
            )
            _messages.value = _messages.value + msg

            if (_autoPlayAudio.value) {
                ttsHelper.speak(translatedText, dst.code)
            }
        }
    }

    fun playMessageAudio(message: ConversationMessage) {
        ttsHelper.speak(message.translatedText, message.targetLang.code)
    }

    fun clearConversation() {
        _messages.value = emptyList()
    }

    fun getTranscript(): String {
        return _messages.value.joinToString("\n\n") { msg ->
            "Speaker ${msg.speakerId} (${msg.sourceLang.name}): ${msg.originalText}\nTranslated (${msg.targetLang.name}): ${msg.translatedText}"
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechHelper.stopListening()
        ttsHelper.stop()
    }
}
