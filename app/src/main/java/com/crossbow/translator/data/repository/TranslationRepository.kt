package com.crossbow.translator.data.repository

import com.crossbow.translator.data.api.RetrofitClient
import com.crossbow.translator.data.api.TranslationApiService
import com.crossbow.translator.data.api.models.BackendTranslateRequest
import com.crossbow.translator.data.local.dao.FavoriteDao
import com.crossbow.translator.data.local.dao.HistoryDao
import com.crossbow.translator.data.local.datastore.AppPreferences
import com.crossbow.translator.data.local.entity.FavoriteEntity
import com.crossbow.translator.data.local.entity.TranslationHistoryEntity
import com.crossbow.translator.domain.model.Language
import com.crossbow.translator.domain.model.TranslationData
import com.crossbow.translator.domain.model.TranslationUiState
import com.crossbow.translator.util.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class TranslationRepository(
    private val historyDao: HistoryDao,
    private val favoriteDao: FavoriteDao,
    private val appPreferences: AppPreferences,
    private val networkMonitor: NetworkMonitor
) {

    suspend fun translate(
        text: String,
        sourceLang: Language,
        targetLang: Language
    ): TranslationUiState = withContext(Dispatchers.IO) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            return@withContext TranslationUiState.Idle
        }

        if (sourceLang.code != "auto" && sourceLang.code == targetLang.code) {
            return@withContext TranslationUiState.Success(
                TranslationData(
                    sourceText = trimmed,
                    translatedText = trimmed,
                    sourceLang = sourceLang,
                    targetLang = targetLang,
                    matchQuality = 1.0f
                )
            )
        }

        val isOnline = networkMonitor.checkCurrentConnectivity()
        if (!isOnline) {
            // Check local history cache first
            val cached = historyDao.searchHistory(trimmed).first().firstOrNull {
                it.targetLangCode.equals(targetLang.code, ignoreCase = true)
            }
            if (cached != null) {
                return@withContext TranslationUiState.Success(
                    TranslationData(
                        sourceText = cached.sourceText,
                        translatedText = cached.translatedText,
                        sourceLang = Language.findByCode(cached.sourceLangCode),
                        targetLang = Language.findByCode(cached.targetLangCode),
                        isCached = true
                    )
                )
            }
            return@withContext TranslationUiState.Error(
                "You are currently offline. Please check your internet connection for live translations."
            )
        }

        val customBackendUrl = appPreferences.backendUrl.first()

        try {
            // If user configured a custom secure proxy backend
            val translatedText: String
            val detectedSourceLang: Language
            var matchQuality = 0.95f

            if (customBackendUrl.contains("mymemory.translated.net", ignoreCase = true)) {
                // Use MyMemory API
                val pair = "${if (sourceLang.code == "auto") "auto" else sourceLang.code}|${targetLang.code}"
                val response = RetrofitClient.translationService.translateWithMyMemory(
                    query = trimmed,
                    langPair = pair
                )

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    val rawResult = body.responseData?.translatedText
                    if (!rawResult.isNullOrBlank()) {
                        // Clean HTML entities if any
                        translatedText = android.text.Html.fromHtml(rawResult, android.text.Html.FROM_HTML_MODE_LEGACY).toString().trim()
                        matchQuality = (body.responseData.match ?: 0.95).toFloat()
                        detectedSourceLang = if (sourceLang.code == "auto") {
                            // Find from matches or default to English
                            Language.findByCode("en")
                        } else {
                            sourceLang
                        }
                    } else {
                        return@withContext TranslationUiState.Error("No translation found for the input phrase.")
                    }
                } else {
                    return@withContext TranslationUiState.Error("Translation service returned an error (${response.code()}).")
                }
            } else {
                // Use Custom Backend Server
                val backendService = RetrofitClient.createTranslationService(customBackendUrl)
                val response = backendService.translateWithBackend(
                    BackendTranslateRequest(
                        text = trimmed,
                        source = sourceLang.code,
                        target = targetLang.code
                    )
                )

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    translatedText = body.translatedText
                    detectedSourceLang = Language.findByCode(body.sourceLang)
                } else {
                    return@withContext TranslationUiState.Error("Backend server error: ${response.message()}")
                }
            }

            // Save to Room history database
            val historyEntry = TranslationHistoryEntity(
                sourceText = trimmed,
                translatedText = translatedText,
                sourceLangCode = detectedSourceLang.code,
                targetLangCode = targetLang.code,
                timestamp = System.currentTimeMillis()
            )
            historyDao.insertHistory(historyEntry)

            TranslationUiState.Success(
                TranslationData(
                    sourceText = trimmed,
                    translatedText = translatedText,
                    sourceLang = detectedSourceLang,
                    targetLang = targetLang,
                    matchQuality = matchQuality
                )
            )
        } catch (e: Exception) {
            TranslationUiState.Error(
                e.localizedMessage ?: "Network connection error. Please verify your connection."
            )
        }
    }

    suspend fun toggleFavorite(translation: TranslationData, isCurrentlyFavorite: Boolean) {
        withContext(Dispatchers.IO) {
            if (isCurrentlyFavorite) {
                favoriteDao.deleteBySourceAndTarget(
                    sourceText = translation.sourceText,
                    targetLangCode = translation.targetLang.code
                )
            } else {
                favoriteDao.insertFavorite(
                    FavoriteEntity(
                        sourceText = translation.sourceText,
                        translatedText = translation.translatedText,
                        sourceLangCode = translation.sourceLang.code,
                        targetLangCode = translation.targetLang.code
                    )
                )
            }
        }
    }
}
