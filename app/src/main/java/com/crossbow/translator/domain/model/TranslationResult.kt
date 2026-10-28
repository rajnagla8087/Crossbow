package com.crossbow.translator.domain.model

data class TranslationData(
    val sourceText: String,
    val translatedText: String,
    val sourceLang: Language,
    val targetLang: Language,
    val matchQuality: Float = 1.0f,
    val isCached: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

sealed interface TranslationUiState {
    data object Idle : TranslationUiState
    data object Loading : TranslationUiState
    data class Success(val data: TranslationData) : TranslationUiState
    data class Error(val message: String) : TranslationUiState
}
