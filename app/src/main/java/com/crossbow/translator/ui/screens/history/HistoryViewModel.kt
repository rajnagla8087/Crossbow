package com.crossbow.translator.ui.screens.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.crossbow.translator.CrossbowApplication
import com.crossbow.translator.data.local.entity.TranslationHistoryEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as CrossbowApplication
    private val historyRepo = app.historyRepository
    val clipboardHelper = app.clipboardHelper
    val ttsHelper = app.textToSpeechHelper

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val historyList: StateFlow<List<TranslationHistoryEntity>> = combine(
        historyRepo.allHistory,
        _searchQuery
    ) { list, query ->
        if (query.isBlank()) {
            list
        } else {
            list.filter {
                it.sourceText.contains(query, ignoreCase = true) ||
                        it.translatedText.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            historyRepo.deleteHistory(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            historyRepo.clearAllHistory()
        }
    }

    fun playAudio(text: String, langCode: String) {
        ttsHelper.speak(text, langCode)
    }
}
