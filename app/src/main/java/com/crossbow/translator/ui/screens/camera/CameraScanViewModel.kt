package com.crossbow.translator.ui.screens.camera

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.crossbow.translator.CrossbowApplication
import com.crossbow.translator.domain.model.Language
import com.crossbow.translator.domain.model.TranslationUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CameraScanViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as CrossbowApplication
    private val translationRepo = app.translationRepository
    val cameraHelper = app.cameraHelper
    val clipboardHelper = app.clipboardHelper
    val ttsHelper = app.textToSpeechHelper

    private val _targetLang = MutableStateFlow(Language.findByCode("en"))
    val targetLang: StateFlow<Language> = _targetLang.asStateFlow()

    private val _extractedText = MutableStateFlow("")
    val extractedText: StateFlow<String> = _extractedText.asStateFlow()

    private val _isProcessingImage = MutableStateFlow(false)
    val isProcessingImage: StateFlow<Boolean> = _isProcessingImage.asStateFlow()

    private val _translationState = MutableStateFlow<TranslationUiState>(TranslationUiState.Idle)
    val translationState: StateFlow<TranslationUiState> = _translationState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    var pendingImageUri: Uri? = null

    fun prepareCameraCaptureUri(): Uri {
        val uri = cameraHelper.createTempImageUri()
        pendingImageUri = uri
        return uri
    }

    fun setTargetLanguage(lang: Language) {
        _targetLang.value = lang
        if (_extractedText.value.isNotBlank()) {
            translateExtractedText()
        }
    }

    fun onExtractedTextChanged(text: String) {
        _extractedText.value = text
        if (text.isBlank()) {
            _translationState.value = TranslationUiState.Idle
        }
    }

    fun processCapturedImage(uri: Uri?) {
        val targetUri = uri ?: pendingImageUri ?: return
        viewModelScope.launch {
            _isProcessingImage.value = true
            _errorMessage.value = null

            val result = cameraHelper.recognizeTextFromUri(targetUri)
            _isProcessingImage.value = false

            result.onSuccess { recognized ->
                _extractedText.value = recognized
                translateExtractedText()
            }.onFailure { err ->
                _errorMessage.value = err.localizedMessage ?: "Failed to extract text from image."
            }
        }
    }

    fun translateExtractedText() {
        val text = _extractedText.value.trim()
        if (text.isEmpty()) return

        viewModelScope.launch {
            _translationState.value = TranslationUiState.Loading
            val result = translationRepo.translate(text, Language.AUTO, _targetLang.value)
            _translationState.value = result
        }
    }

    fun playAudio(text: String) {
        ttsHelper.speak(text, _targetLang.value.code)
    }

    fun clear() {
        _extractedText.value = ""
        _translationState.value = TranslationUiState.Idle
        _errorMessage.value = null
    }
}
